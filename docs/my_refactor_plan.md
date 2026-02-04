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
#### 1. クエリ系（参照）メソッドのトランザクションを変更
        - `@Transactional(readOnly = true)` に変更し、読み取り専用であることを明示する

#### 2. メソッドの並びを変更
        - 上部: クエリ系（参照）: 一覧画面用DTOの用意、詳細画面用DTOの用意、編集画面用DTOの用意、
        - 下部: コマンド系（更新）: 保存、削除、ダミーデータ登録
        - private メソッドは 呼び出し元で判断し、まとめて public の下に置く

#### 3. JavaDoc にユースケース名を追記