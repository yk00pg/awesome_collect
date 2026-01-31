### 現状の課題

- **Web セッションとサービス層の強結合**
  - `GoalService` などのサービスクラスが `SessionManager`（`HttpSession`）を直接参照しており、Web 層の関心事がビジネスロジック層に漏れている。
  - セッションフラグ更新（`hasUpdatedRecordCount` など）がドメイン操作と同一メソッド内で行われており、ユースケースとプレゼンテーションの境界が曖昧になっている。

- **サービスクラスの責務肥大化**
  - `GoalService` が「目標 CRUD」「タグ紐付け」「ユーザ進捗更新」「セッション更新」まで一括で担っており、アプリケーションサービスとドメインサービスの責務が混在している。
  - `UserProgressService` も、日付計算・ストリーク計算・ボーナス登録・DB 更新を1メソッドでまとめて扱っており、凝集度が高すぎる。

- **トランザクション境界の曖昧さ**
  - DTO 準備系のメソッド（単純な読取）にも一律で `@Transactional` が付与されており、「どこからどこまでがトランザクションか」がコードから読み取りづらい。
  - 読取専用か更新を含むのかがアノテーションレベルで区別されていないため、将来的なパフォーマンス・ロック時間の問題につながり得る。

- **時間依存ロジックの直接 `now()` 呼び出し**
  - `UserProgressService` などで `LocalDate.now()` / `LocalDateTime.now()` を直接呼び出しており、日付に依存するロジックのテストが書きづらい。

- **Repository の戻り値が `null` ベース**
  - 例: `GoalRepository.findGoalByIds` が `null` を返しうる設計で、存在しないケースを `Optional` ではなく `null` 判定で扱っている。
  - ヌルチェック漏れによる NPE リスクがあり、境界条件テストも書き忘れやすい。

- **ビジネスクリティカルな領域のテスト不足**
  - `BonusAwesomeService` には良いユニットテストが存在する一方で、より複雑な `GoalService` / `UserProgressService` 周りのテストが不足している。
  - ストリーク計算やボーナス付与など、仕様変更の影響が大きい箇所にテストの網が張れていない。

---

### 理想的な設計

- **レイヤごとの責務分離が明確なアプリケーション**
  - `controller` は「HTTP 入出力」と「ユースケースの起動」に専念し、セッション更新やビュー遷移のみを扱う。
  - `application service`（ユースケース単位）は、「1 ユースケース内でのオーケストレーション（複数ドメインサービス・リポジトリの組み合わせ）」に責務を限定する。
  - `domain service` / `entity` は「ドメインルールの実装」に特化し、Web セッション/HTTP/DB 具体実装に依存しない。

- **インフラ関心事（セッション、キャッシュ）の抽象化**
  - ビジネスロジックから見えるのは `CacheService` のような抽象インターフェースのみとし、その実装として `HttpSessionBasedCacheService` や `SpringCacheService` をインフラ層に配置する。
  - サービス層は「キャッシュを使うかどうか」だけを意識し、「どのように保存されているか（セッションかメモリか）」は知らない。

- **明確なトランザクション境界**
  - コマンド系（登録/更新/削除）ユースケースの入口となるメソッドにのみ `@Transactional` を付与し、クエリ系（表示用 DTO 準備）は原則 `readOnly = true` か非トランザクションとする。
  - 「どのユースケースが DB の一貫性境界か」をメソッドシグネチャから判別できる状態にする。

- **時間と外部環境を抽象化したビジネスロジック**
  - 日付・時刻取得には `Clock` などの抽象を必ず経由し、ビジネスロジックはテスト時に任意の日付／時刻を注入できる。
  - 外部 API / セッション / メール送信なども同様にインターフェース越しに扱い、副作用の発生箇所を明示的にする。

- **Optional ベースのリポジトリ設計**
  - `findXxx` 系のメソッドは `Optional<T>` を返し、「存在しない」ケースを型で表現する。
  - サービス側では `orElseThrow` / `orElse` 等で明示的に分岐し、「存在しない場合どうするか」がコード上に必ず現れるようにする。

- **ユニットテストしやすい小さなサービスと純粋な関数**
  - ストリーク計算・ボーナス付与条件などは副作用を持たないクラス／関数として切り出し、パラメータと戻り値だけで検証できるようにする。
  - ユースケース単位のテスト（アプリケーションサービス）と、ドメインルール単位のテスト（ドメインサービス）を分けて書ける構造にする。

---

### 具体的な修正方針

#### 1. Goal 周りのアプリケーションサービス分割

- **`GoalService` の責務分解**
  - 現在の `GoalService` を以下 2 種類のクラスに分割するイメージで設計する。
    - `GoalApplicationService`（新規作成）: ユースケース単位（登録・更新・削除・ダミーデータ登録）で、セッションフラグ更新や他サービス連携をまとめる。
    - `GoalDomainService`（または既存 `GoalService` をスリム化）: 目標エンティティの生成・状態遷移・タグ関係の操作など、純粋なビジネスロジックに集中。
  - コントローラは `GoalApplicationService` のみを呼び出し、直接 `SessionManager` や `UserProgressService` に触れない。

- **セッションフラグ更新の移動**
  - 例: `saveGoal` の中にある `sessionManager.setHasUpdatedRecordCount(true);` をアプリケーションサービス（またはコントローラ）の責務とする。
  - サービスメソッドの戻り値（`SaveResult`）を利用して、「実際にレコードが変更された場合のみフラグ更新」といったルールをコントローラ層に押し上げる。

##### 参考パターン / ライブラリのヒント

- **参考になる設計・パターン**
  - ドメイン駆動設計の「**アプリケーションサービス / ドメインサービス**」の分離（Eric Evans『ドメイン駆動設計』, Vaughn Vernon『Implementing DDD』）。
  - **Facade パターン**: 複数サービス・リポジトリをまとめる薄い窓口として `GoalApplicationService` を置くイメージ。
  - **ユースケース駆動設計 (Use Case Layer)**: Clean Architecture における UseCase インターフェース層の考え方。
- **参考になりそうな Spring 関連**
  - Spring の公式ドキュメントにある「Service Layer」サンプル構成。
  - Spring Data JPA / Spring MVC のサンプルプロジェクト（controller → service → repository の典型構成）を、責務分離のベースラインとして参照。

#### 2. SessionManager とキャッシュの扱い改善

- **SessionManager への依存方向の見直し**
  - `common.util.SessionManager` を、`web`/`controller` 層に近いパッケージに移動し、ドメイン／サービス層からは直接参照しない。
  - どうしてもサービス層で利用したい場合は、`DashboardCache` のような抽象インターフェースを `service` パッケージに定義し、その実装として `SessionDashboardCache` を作成する。

- **Boolean フラグ操作の型安全化**
  - `SessionManager` のゲッター系を次のようなインターフェースに変更するイメージでリファクタリングする。
    - 例: `public boolean hasUpdatedRecordCount()` （内部では `Boolean.TRUE.equals(session.getAttribute(...))`）
  - セッションキーは専用 enum または定数クラスに集約し、キーの typo によるバグを防ぐ。

##### 参考パターン / ライブラリのヒント

- **参考になる設計・パターン**
  - **ポート＆アダプタ（Hexagonal Architecture）**: `DashboardCache` をポート、`SessionDashboardCache` をアダプタとして実装する考え方。
  - **Proxy / Decorator パターン**: キャッシュ付きサービスを既存サービスの前段に挟む構成の検討。
- **Java / Spring の標準・有名どころ**
  - Spring Framework の **`@Cacheable` / `@CacheEvict`** と `CacheManager`（Caffeine, Ehcache, Redis など）によるキャッシュ抽象化。
  - `javax.cache` (JSR-107) 仕様および `javax.cache.Cache` インターフェース（JCache）に基づくキャッシュ API。
  - セッションラッパの例として、Spring の `SessionStatus` や Spring Session（`spring-session-jdbc`/`spring-session-data-redis`）を参考にする。

#### 3. トランザクション境界の整理

- **読取系メソッドのアノテーション見直し**
  - 例: `GoalService.prepareResponseDtoList`, `GoalService.prepareResponseDto`, `GoalService.prepareRequestDto` など、純粋な読取メソッドは以下のいずれかとする。
    - `@Transactional(readOnly = true)` に変更する。
    - またはアノテーションを外し、呼び出し側（アプリケーションサービス）で必要に応じて付与する。

- **ユースケース単位でのトランザクション定義**
  - 「1 ユースケースで何を一貫性境界として扱うか？」を決め、アプリケーションサービスのメソッドに `@Transactional` を集約する。
  - 例: `GoalApplicationService.saveGoal()` にのみ `@Transactional` を付与し、その中から `GoalDomainService` / `GoalRepository` / `GoalTagJunctionService` を呼び出す。

##### 参考パターン / ライブラリのヒント

- **参考になる設計・パターン**
  - **Unit of Work パターン**: 1 ユースケースを 1 トランザクションとして扱う考え方。
  - **Service Layer パターン**: アプリケーションサービス層にトランザクション境界を集約する典型パターン。
- **Java / Spring の標準・有名どころ**
  - Spring のトランザクション管理 (`@Transactional`, `PlatformTransactionManager`, 伝搬属性 `PROPAGATION_REQUIRED` など)。
  - Spring Data JPA / MyBatis-Spring のトランザクション連携のドキュメント。
  - JTA (`javax.transaction.Transactional`) の考え方を、分散トランザクションまでは使わない前提で設計指針として参照。

#### 4. 時刻取得の抽象化

- **`Clock` の導入**
  - `UserProgressService`, `BonusAwesomeService` など日付依存ロジックを持つサービスに `Clock` をコンストラクタインジェクションする。
    - 例: `public UserProgressService(UserProgressRepository repo, BonusAwesomeService bonusAwesomeService, Clock clock) { ... }`
  - すべての `LocalDate.now()` / `LocalDateTime.now()` 呼び出しを `LocalDate.now(clock)` / `LocalDateTime.now(clock)` に置き換える。

- **テストコードでの Clock 差し替え**
  - ユニットテストでは `Clock.fixed(...)` を使用し、特定日付での挙動（例: 連続7日目、30日目など）を再現性高く検証する。

##### 参考パターン / ライブラリのヒント

- **参考になる設計・パターン**
  - **Dependency Injection パターン**: `Clock` をコンストラクタ DI することで、時間を外部から注入する。
  - **テストダブル（Fake/Stub）**: `Clock` 実装を差し替えることで、テスト時のみ固定時刻を使う。
- **Java / 標準ライブラリ**
  - Java 8+ の `java.time.Clock`, `ZoneId`, `Instant`。
  - Spring の `@Configuration` で `Clock` Bean を定義し、アプリ全体で共有するパターン。
  - テストでの `Clock.fixed(Instant, ZoneId)` / `Clock.offset(...)` の使い分け。

#### 5. Repository API と Optional の採用

- **`find` 系メソッドの戻り値変更**
  - 例: `GoalRepository.findGoalByIds(int goalId, int userId)` を `Optional<Goal>` 戻り値に変更する。
  - サービス層では `orElseThrow` や `orElse(null)` を明示的に選択し、「見つからない場合」のポリシーをコードに表現する。

- **境界条件テストの追加**
  - 目標が存在しない場合（不正 ID、他ユーザーの ID など）、ストリークが 0/1/7/30 日の境界など、Optional 化したことで表現しやすくなった条件に対するテストを追加する。

##### 参考パターン / ライブラリのヒント

- **参考になる設計・パターン**
  - **Null Object パターン** と対比しつつ、`Optional` による「存在しない」の明示的表現。
  - Domain-Driven Design における「リポジトリは集約のコレクション」としての設計（返り値の一貫性ルール）。
- **Java / 有名どころ**
  - Java 8+ の `java.util.Optional`（`orElseThrow`, `map`, `flatMap`, `ifPresentOrElse` など）。
  - Spring Data の `findById` が `Optional<T>` を返す仕様をベンチマークにする。
  - Vavr など関数型ライブラリの `Option` 型（Optional をより強化したモデル）も発想の参考になる。

#### 6. テスト戦略の強化

- **ドメインロジックのユニットテスト**
  - `UserProgressService` のロジックを担当するクラス（例: `UserProgressCalculator`）を切り出し、ストリーク計算・ボーナス発生条件を純粋関数としてテストする。
  - `GoalService`（または分割後の `GoalDomainService`）についても、「重複タイトル検知」「達成状態変更時の日時付与」など、仕様バグが出やすい部分に対してテストを書く。

- **アプリケーションサービスの結合テスト**
  - `GoalApplicationService` のようなユースケース単位のサービスには、Mock を使った結合寄りのテストを用意し、「どのサービスがどの順番で呼ばれるか」「トランザクション境界内でどこまで行うか」を検証する。

##### 参考パターン / ライブラリのヒント

- **参考になる設計・パターン**
  - **テストピラミッド**: Unit / Integration / E2E のバランスを意識したテスト戦略。
  - **Given-When-Then** スタイル（BDD）的なテスト記述（JUnit + AssertJ / Mockito など）。
- **Java / テスト系ライブラリ**
  - JUnit 5 (`@Test`, `@Nested`, `@ParameterizedTest` など)。
  - AssertJ（`assertThat` ベースの fluent assertion）。
  - Mockito / Spring Boot Test (`@SpringBootTest`, `@WebMvcTest`, `@DataJpaTest` などのスライス・テスト）。
  - Testcontainers（必要に応じて、実 DB に近い環境での統合テストを行う場合）。

---

このガイドラインに沿ってリファクタリングを進めることで、

- レイヤごとの責務分離
- テスト容易性
- 変更時の影響範囲の明確化

が一段レベルアップし、プロダクション運用を前提とした Spring Boot アプリケーションとしても十分通用する設計に近づいていきます。

