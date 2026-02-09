# リファクタ方針に基づいたリファクタ計画

## 1. セッション管理周りのリファクタリング

### レビューの解釈
- セッションの読み書きは「HTTP リクエスト/レスポンスに関連した処理」のため、Service ではなく Controller が担うべき
- **「関心の分離（Separation of Concerns）」** の観点から、「Service は HTTP やセッションを知らないほうが良い」ため、ビジネスロジックに集中すべき

### 修正案
- `SessionManager` は Controller 配下の web パッケージに移動し、Service や Repository などから直接触れないようにする
- セッションの読み書きは Controller で行う（ビジネスロジックは Service のまま）
    - Service の更新系メソッドでは、`SaveResult` など結果だけを返し、セッションの更新は Controller で行う

### タスク
#### 1. `SessionManager` を "com.awesomecollect.common.util" から "com.awesomecollect.controller.web" に移動　**(DONE)**
- IDEにて実施し、importも安全に変更
- @Codebase を使ったリストアップにより、移動による影響範囲は9ファイル（ `SessionManager` + Service 8ファイル）と判明
- 以降の作業により、Controllerにも影響が出る見込み

#### 2. セッションの読み書きを Service から Controller に移動　**(DONE)**
- 確認時: Controller でセッション情報を取得し、結果を Service に渡す
- 更新時: Service から結果を受け取り、Controller でセッションを更新する

#### 3. `SessionManager` の中身を修正　**(DONE)**
- ゲッター系メソッドの型安全化（ `Boolean.TRUE.equals(...)`　）
    - `Boolean.TRUE.equals(...)` では　null は false とみなされるが、更新フラグでは null を true とみなしたいので、フラグの名前と true / false の振り方を変更する
        - ex: （現在） `hasUpdatedRecordCount` null / true のときは新たにデータを集計し、false のときはキャッシュデータを使う
                （修正） `hasCachedAwesomePoint` true のときは キャッシュデータを使い、null / false のときは新たにデータを集計する
- セッター系メソッド（フラグ）を true / false に分け、それぞれ `enableXxx` / `disableXxx` とすることでフラグの状態をわかりやすくする
- テスト実装やキー追加の可能性を考慮し、セッションキーを定数クラスとして切り出す

#### 4. キャッシュ使用フラグの更新場所を整理 **(PENDING)**
- 登録内容更新時のフラグ更新を厳密に棲み分ける
    - `DailyTodoController` のレコード数変更時
    - `DailyDoneController` のレコード数変更時、学習時間変更時、学習日数変更時、タグ変更時
    - `GaolController` / `ArticleStockController` のステータス更新時

### 気づき
- ダミーデータ注入時に学習アクションごとにセッションの更新をしてしまっていたが、Controller に移動したことにより更新が1回で済むようになった
- `hasUpdatedRecordCount` という形でレコード数更新フラグをセッションに保存しているが、Goal, ArticleStock のステータス更新も含むためリネームしたほうが良さそう -> DONE
- 一度に複数レコードを扱う可能性のある `DailyTodoController` / `DailyDoneController` においては、各更新フラグをもっと厳密に扱うべきかもしれない -> PENDING
- ダッシュボードに表示するデータをキャッシュとしてセッション（HttpSession）に保存しているが、 Spring Cache（ `@EnableCaching`, `@Cachable` ）で扱うほうが良さそう -> PENDING


## 2. Goal / Memo / ArticleStock 周りの Service をユースケース単位に整理

### レビューの解釈
- Service を「ドメインロジック」と「インフラ（アプリケーションサービス）」に分ける
    - まずはクラスは分けずに、メソッドの順番等を整理して見た方をユースケース単位に分ける
- 何でもトランザクションをつけているように見えるので、コマンド系（更新）とクエリ系（参照）で分ける

### 修正案
- クエリ系（参照）メソッドのトランザクションを `@Transactional(readOnly = true)` に変更し、ユースケースのトランザクション境界を明確にする
- メソッドの並び順をクエリ系（参照）とコマンド系（更新）に分け、「読む / 書く」の教会をわかりやすくする
- JavaDoc にもユースケース名を記載し、メソッドの役割を明示する

### タスク
#### 1. クエリ系（参照）メソッドのトランザクションを変更 **(DONE)**
- `@Transactional(readOnly = true)` に変更し、読み取り専用であることを明示する

#### 2. メソッドの並びを変更 **(DONE)**
- 上部: クエリ系（参照）: 一覧画面用DTOの用意、詳細画面用DTOの用意、編集画面用DTOの用意、
- 下部: コマンド系（更新）: 保存、削除、ダミーデータ登録
- private メソッドは 呼び出し元で判断し、まとめて public の下に置く

#### 3. JavaDoc にユースケース名を追記 **(DONE)**

### 気づき
- Controller から見てDTO名だけでなくメソッド名からも取得するデータの名前がわかりやすいほうが良いかもしれない -> DONE

## 3. トランザクション境界の整理

### レビューの解釈
- 何でもトランザクションをつけているように見えるので、コマンド系とクエリ系で分ける

### 修正案
- クエリ系メソッドのトランザクションを `@Transactional(readOnly = true)` に変更し、ユースケースのトランザクション境界を明確にする

### タスク
#### 1. クエリ系（参照）メソッドのトランザクションを変更 **(DONE)**
- `@Transactional(readOnly = true)` に変更し、読み取り専用であることを明示する

#### 2. 学習アクション周りと同様にメソッドをユースケースで切り分けて整理 **(DONE)**

#### 3. 一部 `@Transactional` の付与漏れがあるため対応 **(DONE)**

## 4. Repository で Optional を導入

### レビューの解釈
- java8 以降、「存在しないもの」は `null` ではなく `Optional<T>` で返すのがスタンダード
- `Optional<T>` を使うことで、NPEを防ぐとともに、「存在しないケースがある」ことを明示的に表現できるため、`null` チェックよりも可読性が上がる

### 修正案
- `find` 系メソッドの戻り値を `Optional<T>` に変更する
- Service 側で `orElseThrow` / `orElse(null)` / `ifPresentOrElse` 等を使い、見つからない場合の処理を明示的に書く

### タスク
#### 1. Repository の `find` 系メソッドの戻り値を `Optional<T>` に変更 **(DONE)**
- エンティティ取得系メソッドを優先
    - 学習アクション周り -> ユーザー周りの順に着手
- ID取得系メソッドは今のままで良さそう

#### 2. Service で見つからない場合（存在しない場合）の処理を明示的に記述 **(DONE)**

### 気づき
- `GoalService.checkUpdatedStatus` のように副作用を伴う処理は、`Optional` の `.map()` / `.filter()` とは分けて扱うべき
- 「 `.filter()` で条件を絞り込んでから、 `isPresent()` / `.isEmpty()` で判定」は `Optional` の典型的なイディオムのひとつ
- `Optional<T>` は「存在しない可能性がある」ことを明示する
    - 戻り値が `Chocolate` で `null` の場合
        - 指示: 「ゴディバのチョコを持ってきてください」
            - null チェックなし -> 「在庫ないじゃん、何もできん。おこ」 -> NPE
            - null チェックあり -> 「在庫がない場合はどうするんだ？　あ、売り切れ札つけるのか」 -> 早期リターン
    - 戻り値が `Optional<Chocolate>` で存在しない場合
        - 指示: 「ゴディバのチョコをこの袋に入れて持ってきてください。もしかしたら在庫がないかもしれないので、その場合は袋だけ持ってきてください」
            - 「在庫がある場合はラピングして、ない場合は売り切れ札をつけるんだね、よし」
- 戻り値が `List` の場合は、もともと `List` という箱に入れられているようなものなので、 `Optional` にする必要はない
    - 「コレクションを返すメソッドは `null` ではなく空コレクションを返すべき」という原則からも `Oprional` でラップする必要はない
- `.orElse(value)` : 値を直接渡す
    - `.orElse(defaultValue)`
- `.orElseThrow(Supplier)` : 例外を生成するラムダを渡す（Supplier: 引数なしで値を返すラムダ）
    - `.orElseThrow(() -> new SomeExecption("message")`

## 5. Clock を導入

### レビューの解釈
- `LocalDateTime.now()` / `LocalDate.now()` を直接呼ぶと時刻を固定できずテストがしづらい

### 修正案
- `Clock` を DI、日付/時間取得を抽象化する

### タスク
- `Clock` を DI し、`LocalDateTime.now()` / `LocalDate.now()` を直接読んでいる部分を `LocalDateTime.now(clock)` / `LocalDate.now(clock)` に変更する **(DONE)**
    - DTO は依存性を持たないという原則があるため、Service に DI して DTO に引数で渡す設計に変更する

### 気づき
- Spring では Clock を自動で Bean 登録しないので、`@Configuration` をつけた設定クラスを作成し、 `@Bean` をつけて CLock の戻り値を定義する必要がある

## 6. 仕様が複雑な領域のテストを強化

### レビューの解釈
- 現状、ビジネスロジックとして複雑な `UserProgressService` や `GoalService` に対するテストがない

### 修正案
- `UserProgressService` のストリーク計算・ボーナス付与のロジックを、小さなメソッドに切り出してユニットテストを書く
- `GoalService` では、仕様がブレやすい部分を中心にテストシナリオを作る
    - 重複タイトル検知
    - 目標達成状態変更時の日時付与
    - タグの紐付け・削除

### タスク
#### 1. `UserProgressService` のユニットテストを作成 **(TODO)**
- `UserProgressRepository` , `BonusAwesomeService` をモック化
- 固定日時の `Clock` を注入
- テストケースは以下を想定

| 最終記録日 | 累計記録日数の更新 | 最終記録日の更新 | 連続記録日数の更新 | 連続記録ボーナス獲得回数の更新 |
| :-- | :-- | :-- | :-- | :-- |
| 今日 | — | — | — | — |
| 昨日 | +1 | 今日 | +1 | アクション登録 + n日連続アクション登録(3, 7, 30の倍数) |
| 一昨日 | +1 | 今日 | 1 | アクション登録 |
| null（初回） | 1 | 今日 | 1 | アクション登録 |
