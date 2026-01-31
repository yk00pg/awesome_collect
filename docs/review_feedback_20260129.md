### 全体総評

- **全体像**: Spring Boot + MyBatis の典型的なレイヤード構成で、パッケージ分割や責務分担はかなり意識されています。DTO/Entity/Repository/Service/Controller も大きくは破綻していません。
- **率直評価**: 個人開発としては明らかに「よく書けている」レベルですが、「プロ現場で PR レビューに乗る」前提で見ると、**(1) Web セッションとドメインロジックの強結合、(2) トランザクション境界の粒度と命名、(3) テスト戦略の薄さ**がリジェクト理由になり得ます。

---

### 1. オブジェクト指向設計 / SOLID

#### **良い点**

- **レイヤーごとの責務分離は概ね適切**
  - `controller` はリクエスト受付と DTO の受け渡しに集中 (`DashboardController` など)。
  - `service` は集約されたビジネスロジックを持ち、`repository` に依存する方向もレイヤードアーキテクチャに沿っています。
  - `BaseActionTagJunctionService<T>` のように、タグ連携ロジックを共通化しているのは良い抽象化です。

- **小さめのサービスクラス**
  - `TagService`, `BonusAwesomeService` はメソッドも短く、責務も明確でテストしやすい形になっています。

#### **問題点1: GoalService の責務肥大化**

`GoalService` は以下を一つのクラスで行っています。

- 目標の CRUD（リポジトリ制御）
- タグの解決・紐付け (`TagService`, `GoalTagJunctionService`)
- ユーザ進捗更新 (`UserProgressService`)
- セッションのキャッシュフラグ操作 (`SessionManager`)

```169:185:/Users/yuki/IdeaProjects/AwesomeCollect/src/main/java/webapp/AwesomeCollect/service/action/GoalService.java
@Transactional
public SaveResult saveGoal(int userId, GoalRequestDto dto) {
  List<String> pureTagList = JsonConverter.extractValues(dto.getTags());
  List<Integer> tagIdList = tagService.resolveTagIdList(userId, pureTagList);

  int goalId = dto.getId();
  SaveResult saveResult;
  if (goalId == 0) {
    saveResult = registerGoal(userId, dto, tagIdList);
  } else {
    saveResult = updateGoal(userId, dto, tagIdList, goalId);
  }

  sessionManager.setHasUpdatedRecordCount(true);

  return saveResult;
}
```

- **現場レビュー観点**:
  - 「**アプリケーションサービス**」と「**純粋なドメインサービス**」が混在している印象です。
  - `SessionManager` に直接触ることで Web 層の関心事がビジネス層に漏れており、SRP/レイヤ分離的に弱い。

**改善案（プロ現場レベル）**

- **レイヤを明確化**
  - `GoalApplicationService`（ユースケース単位の orchestration）と、`GoalDomainService`（目標ドメインの純粋なロジック）に分離。
  - `GoalApplicationService` から `SessionManager` を扱い、`GoalDomainService` はあくまで `GoalRepository`, `GoalTagJunctionService`, `UserProgressService` のみ扱う。

- **セッション更新はコントローラ側で行う**
  - `saveGoal` の戻り値 `SaveResult` をコントローラで受け取り、「保存が行われたかどうか」でセッションフラグを更新するようにすると、サービス層が Web に依存しなくなります。

#### **問題点2: SessionManager の責務と型安全性**

```32:50:/Users/yuki/IdeaProjects/AwesomeCollect/src/main/java/webapp/AwesomeCollect/common/util/SessionManager.java
public Boolean hasUpdatedRecordCount() {
  return (Boolean) httpSession.getAttribute(HAS_UPDATED_RECORD_COUNT);
}
...
public Boolean hasUpdatedTime() {
  return (Boolean) httpSession.getAttribute(HAS_UPDATED_TIME);
}
```

- **指摘ポイント**
  - `Boolean` ラッパ型 + キャストで、セッション未設定時に **平然と `null` を返す** 実装になっており、呼び出し側次第で NPE が発生し得ます。
  - セッションキー文字列をクラス内にベタ書きしているので、他箇所から同じキーを誤って使うと発見が困難。

**改善案**

- `Boolean.TRUE.equals(...)` パターンで **常に `boolean` で返す** インターフェースにする。
- セッションキーは enum または専用の `SessionKey` クラスに集約し、クラス外から直接文字列を参照させない。
- さらに踏み込むと、セッションではなく **Spring Cache (Caffeine/Redis)** に置き換えれば、Web セッションとビジネスロジックの結合を外せます。

#### **問題点3: `UserProgressService` における凝集度**

`updateUserProgress` はストリーク計算とボーナスロジックのトリガを1メソッドにまとめています。

```45:66:/Users/yuki/IdeaProjects/AwesomeCollect/src/main/java/webapp/AwesomeCollect/service/user/UserProgressService.java
@Transactional
public void updateUserProgress(int userId) {
  UserProgress userProgress = userProgressRepository.findUserProgressByUserId(userId);

  LocalDate today = LocalDate.now();
  LocalDate yesterday = today.minusDays(1);
  LocalDate lastActionDate = userProgress.getLastActionDate();
  int currentStreak = userProgress.getCurrentStreak();

  if (lastActionDate == null || !lastActionDate.equals(today)) {
    userProgress.setTotalActionDays(userProgress.getTotalActionDays() + 1);
    userProgress.setLastActionDate(today);

    currentStreak = updateStreak(yesterday, lastActionDate, currentStreak, userProgress);

    int newStreakBonusCount =
        bonusAwesomeService.registerBonusAwesome(userId, currentStreak, today);

    int currentStreakBonusCount = userProgress.getStreakBonusCount();
    userProgress.setStreakBonusCount(currentStreakBonusCount + newStreakBonusCount);

    userProgressRepository.updateUserProgress(userProgress);
  }
}
```

- **指摘ポイント**
  - 日付計算 (`LocalDate.now()`)、ストリーク計算、ボーナス登録、副作用（DB更新）が密結合。
  - テスト観点では `LocalDate.now()` 依存と `bonusAwesomeService` 呼び出しにより、**ユニットテストではなく結合テスト寄り**になりがち。

**改善案**

- `Clock` を DI して、日付計算を外部化（Effective Java でも推奨）。
- 「ストリーク計算＋ボーナス判定」部分を別の `UserProgressCalculator` 的クラスに抽出し、純粋関数に近づける。

---

### 2. Java のベストプラクティス

#### **トランザクション境界の設計**

- `GoalService` の `prepareResponseDtoList` / `prepareResponseDto` / `prepareRequestDto` まで `@Transactional` が付与されています。
  - シンプルな読み取りであれば `@Transactional(readOnly = true)` にするか、そもそも外しても良い場面が多いです。
  - 現場では「どこからどこまでがトランザクションか？」をかなりシビアに見るので、**「全部とりあえず @Transactional」** はレビューで突っ込まれます。

**改善案**

- **コマンド系（更新）メソッドのみに書く**か、クエリ系には `readOnly = true` をつけて意図を明確にする。

#### **例外設計**

- `DuplicateException` / `IncorrectPasswordException` 自体はシンプルで悪くないですが、**どこで捕まえてどうユーザーに返すか**が重要です。
- `DuplicateException` の message にメッセージキーを入れているのは i18n を見据えた設計で良いですが、**エラーハンドラ（例: `@ControllerAdvice`）との連携**が見えないと「設計として中途半端」に見えることがあります。

**改善案**

- 共通の `GlobalExceptionHandler` を用意し、これらドメイン例外を明示的にハンドリングする。
- 例外名は用途が明確なので、**これら以外の汎用 RuntimeException 乱発を避ける**方針を README or RULE に明記。

#### **Stream API / コレクション操作**

- `BonusAwesomeService`, `TagService` は Stream を過度に使い過ぎず、読みやすいレベルで使えています。
- ただし `BaseActionTagJunctionService` では `for` ループと `Set` を組み合わせていますが、`toAddTagList` / `toRemoveList` のような名前は少し抽象的で、**「何を追加・削除するのか」が一瞬で分かる名前にできるとより良い**です（例: `tagIdsToAdd`, `tagIdsToRemove`）。

#### **日付/時間の扱い**

- `DateTimeFormatUtil` は `final` クラス + private コンストラクタ + static メソッドで、典型的なユーティリティクラスとしてきれいです。
- 一方で、ビジネスロジック側 (`UserProgressService` など) が `LocalDate.now()` を直接呼んでおり、**テストと再現性の観点で Clock 抽象化がない**のは現場だと指摘対象になりがちです。

---

### 3. アーキテクチャ / パッケージ依存

#### **良い点**

- `controller` → `service` → `repository` → `mapper` の依存方向は一貫しており、**循環依存はなさそう**です。
- `entity` パッケージも `action`, `user`, `junction` ごとに分けており、ドメインごとのまとまりが意識されています。

#### **問題点: Web セッション依存のリーク**

- `SessionManager` が `common.util` にあり、`GoalService` などビジネスロジックから直接参照されています。
  - これはレイヤードアーキテクチャ的には「インフラ層（Web/Session）がドメイン層に侵入」している形です。

**改善案**

- `SessionManager` を `web`（または `controller` 配下）に移動し、サービス層からは見えないようにする。
- どうしてもビジネス層でキャッシュ管理したい場合は、`CacheService` インターフェースを `service` パッケージに置き、その実装として `HttpSessionBasedCacheService` を `web` 側に実装するなど、**依存方向を逆転**させる。

---

### 4. 拡張性・テスト容易性

#### **現状のテスト**

- `BonusAwesomeServiceTest` のように、Mockito で Repository をモックしつつユニットテストを書いているのは良いです。
- しかし、ビジネス的に複雑な `GoalService`, `UserProgressService` などのテストが（ざっと見る限り）見当たらず、**一番壊れると困るところが無テスト**に近い状態です。

#### **テストを阻害している要因**

- `LocalDate.now()` / `LocalDateTime.now()` の直呼び出し。
- `SessionManager` のような Servlet API への依存がサービス層に入り込んでいる。
- `GoalRepository.findGoalByIds` が `null` を返す API になっており、**Optional での明示的な「存在/非存在」表現がない**ため、境界条件テストが抜けやすい。

#### **改善案**

- **Clock の導入**: `Clock` をコンストラクタインジェクションし、`LocalDate.now(clock)` を使う。
- **Optional の活用**: Repository レイヤで `Optional<Goal>` を返し、サービス側も `orElseThrow` / `orElse(null)` 等で明示的に扱う。
- **セッションとドメインの分離**: 先述のとおり、セッション更新はコントローラ／アプリケーションサービス側に寄せる。

---

### 5. 「プロ現場ならリジェクトされ得る」ポイントまとめ

- **Web セッションとサービス層の強結合**
  - `SessionManager` をサービスから直接触っている設計は、多くの現場で「レイヤ分離の観点で NG」としてリジェクトされます。
- **トランザクション境界の曖昧さ**
  - ほぼ全てのメソッドに `@Transactional` を付けるスタイルは、「DB ロック時間が読めない」「責務の切り方が曖昧」として指摘されがちです。
- **ビジネスクリティカルなロジックのテスト不足**
  - ストリーク・ボーナス・目標達成など、**仕様バグが出やすい箇所にユニットテストがない**のは、プロジェクトの成熟度として厳しく見られます。
- **時間依存ロジックの直接 `now()` 呼び出し**
  - 小規模なら許容されますが、長期運用・仕様追加を前提とした現場では「後で必ずテストで詰む」として早めに Clock 抽象化を求められます。

---

### まとめ

- **設計レベルは全体としてかなり高く、個人開発としては間違いなく合格点以上**ですが、プロ現場の PR レビューとしては「セッション依存の切り離し」「トランザクション境界の明確化」「時間依存ロジックの抽象化」「重要サービスのユニットテスト追加」が最低ラインの修正要求になると思います。
- もしよければ、次のステップとして「`GoalService` をアプリケーションサービス＋ドメインサービスに分割するリファクタ案」を具体的なクラス設計・メソッドシグネチャまで一緒に詰めていきましょう。

