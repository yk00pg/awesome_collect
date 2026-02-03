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

#### 3. `SessionManager` の中身を修正　**(TODO)**
        - ゲッター系メソッドの型安全化（ `Boolean.TRUE.equals(...)`　）
            - `Boolean.TRUE.equals(...)` では　null は false とみなされるが、更新フラグでは null を true とみなしたいので、フラグの名前と true / false の振り方を変更する
                - ex: （現在） `hasUpdatedRecordCount` null / true のときは新たにデータを集計し、false のときはキャッシュデータを使う
                      （修正） `hasCachedAwesomePoint` true のときは キャッシュデータを使い、null / false のときは新たにデータを集計する
        - セッター系メソッド（フラグ）を true / false に分け、それぞれ `enableXxx` / `disableXxx` とすることでフラグの状態をわかりやすくする
        - テスト実装やキー追加の可能性を考慮し、セッションキーを定数クラスとして切り出す

### 気づき
- ダミーデータ注入時に学習アクションごとにセッションの更新をしてしまっていたが、Controller に移動したことにより更新が1回で済むようになった
- `hasUpdatedRecordCount` という形でレコード数更新フラグをセッションに保存しているが、Goal, ArticleStock のステータス更新も含むためリネームしたほうが良さそう -> DONE
- 一度に複数レコードを扱う可能性のある `DailyTodoController` / `DailyDoneController` においては、各更新フラグをもっと厳密に扱うべきかもしれない -> PENDINGらsらs"
- ダッシュボードに表示するデータをキャッシュとしてセッション（HttpSession）に保存しているが、 Spring Cache（ `@EnableCaching`, `@Cachable` ）で扱うほうが良さそう -> PENDING