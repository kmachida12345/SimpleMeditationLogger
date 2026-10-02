# リリースガイド

## 1. キーストアの作成

リリース用のキーストアを作成します（初回のみ）：

```bash
keytool -genkey -v -keystore release-keystore.jks -keyalg RSA -keysize 2048 -validity 10000 -alias simplemeditationlogger
```

入力が求められる情報：
- キーストアのパスワード（覚えておくこと）
- 名前、組織、都市などの情報
- キーのパスワード（覚えておくこと）

**重要**: `release-keystore.jks` ファイルは `.gitignore` に追加済みです。絶対にGitにコミットしないでください。

## 2. 署名設定の有効化

`app/build.gradle.kts` の `signingConfigs` ブロックのコメントを外し、以下を設定：

```kotlin
signingConfigs {
    create("release") {
        storeFile = file("release-keystore.jks")
        storePassword = System.getenv("KEYSTORE_PASSWORD")
        keyAlias = System.getenv("KEY_ALIAS")
        keyPassword = System.getenv("KEY_PASSWORD")
    }
}
```

`buildTypes` の `release` ブロックで署名を有効化：

```kotlin
release {
    signingConfig = signingConfigs.getByName("release")
    // ...
}
```

## 3. 環境変数の設定

ビルド前に環境変数を設定：

```bash
export KEYSTORE_PASSWORD="your_keystore_password"
export KEY_ALIAS="simplemeditationlogger"
export KEY_PASSWORD="your_key_password"
```

または `~/.gradle/gradle.properties` に追加（より安全）：

```properties
KEYSTORE_PASSWORD=your_keystore_password
KEY_ALIAS=simplemeditationlogger
KEY_PASSWORD=your_key_password
```

## 4. バージョンの更新

バージョンは `gradle.properties` で一元管理されています。

### 自動更新（推奨）

```bash
# バグ修正リリース（例: 1.0.0 → 1.0.1）
./bump-version.sh patch

# 機能追加リリース（例: 1.0.0 → 1.1.0）
./bump-version.sh minor

# 大規模変更リリース（例: 1.0.0 → 2.0.0）
./bump-version.sh major
```

### 手動更新

`gradle.properties` を編集：

```properties
# versionCode は前回より1増やす / versionName はセマンティックバージョニング
app.versionCode=2
app.versionName=1.0.1
```

詳細は `VERSIONING.md` を参照してください。

## 5. リリースビルドの作成

```bash
./gradlew assembleRelease
```

生成されるファイル：
- `app/build/outputs/apk/release/app-release.apk`

または AAB（Google Play推奨）：

```bash
./gradlew bundleRelease
```

生成されるファイル：
- `app/build/outputs/bundle/release/app-release.aab`

## 6. ビルド前チェックリスト

- [ ] テストが全て通ることを確認: `./gradlew test`
- [ ] versionCode と versionName を更新
- [ ] ProGuardルールが正しく設定されている
- [ ] アプリアイコンが正しく表示される
- [ ] 署名設定が正しく構成されている
- [ ] `.gitignore` にキーストアファイルが含まれている

## 7. Google Play リリース要件

### 必須項目

1. **プライバシーポリシー**
   - アプリがデータ収集する場合は必須
   - 本アプリ用のプライバシーポリシーを作成済み
   - ファイル: `privacy_policy.html` (日本語), `privacy_policy_en.html` (英語)
   - GitHub Pages等で公開する必要あり
   - 詳細は `PRIVACY_POLICY_README.md` を参照
   - **必須アクション**: 
     - メールアドレスを実際のものに更新
     - HTMLファイルをホスティング（GitHub Pages推奨）
     - Google Play Consoleに公開URLを登録

2. **アプリの説明**
   - 短い説明（80文字以内）
   - 詳細な説明
   - スクリーンショット（最低2枚）

3. **コンテンツレーティング**
   - Google Playコンソールで質問票に回答

4. **ターゲット層と内容**
   - ターゲット年齢層の選択
   - 広告の有無

5. **アプリカテゴリ**
   - 健康とフィットネス > 瞑想

### 推奨項目

- フィーチャーグラフィック（1024x500）
- プロモーションビデオ
- タブレット用スクリーンショット

## 8. テストリリース

初回リリース時は内部テストまたはクローズドテストから開始することを推奨：

1. Google Play Console で内部テストトラックを作成
2. テスターのメールアドレスを登録
3. AABをアップロード
4. テスターに確認してもらう
5. 問題なければ本番リリース

## 9. リリース後

- バージョン管理のためにGitタグを作成：
  ```bash
  git tag -a v1.0 -m "Release version 1.0"
  git push origin v1.0
  ```

- リリースノートを作成してGitHubにプッシュ

## 10. Play Console への自動アップロード（GitHub Actions）

`v*` タグを push すると `.github/workflows/release.yml` が走り、テスト → 署名付きAAB生成 → Play Console の **internal トラック**へ自動アップロードします（本番昇格は Console で手動）。

### 初期設定（1回のみ）

1. Google Cloud でサービスアカウントを作成し、JSONキーを発行（Google Play Android Developer API を有効化）
2. Play Console「ユーザーと権限」でそのサービスアカウントを招待し、リリース権限を付与
3. **初回AABは Console から手動アップロード**（APIでは未登録アプリへの初回投入不可）
4. GitHub の Settings > Secrets and variables > Actions に登録：

| Secret | 内容 |
|---|---|
| `KEYSTORE_BASE64` | `base64 -i release-keystore.jks` の出力 |
| `KEYSTORE_PASSWORD` / `KEY_ALIAS` / `KEY_PASSWORD` | キーストア情報 |
| `PLAY_SERVICE_ACCOUNT_JSON` | サービスアカウントJSONの中身 |

### リリース手順

```bash
./bump-version.sh patch      # versionCode は既アップロード分より大きくする
git commit -am "chore: bump version" && git push
git tag v1.2.1 && git push origin v1.2.1   # タグは app.versionName と一致させる
```

ローカルから署名ビルドする場合は `KEYSTORE_FILE`（キーストアのパス）、`KEYSTORE_PASSWORD`、`KEY_ALIAS`、`KEY_PASSWORD` を環境変数または `~/.gradle/gradle.properties` に設定します。未設定の場合、release ビルドは署名されません。

## トラブルシューティング

### ProGuardエラー
- ログを確認: `app/build/outputs/mapping/release/`
- 必要に応じて `proguard-rules.pro` にkeepルールを追加

### 署名エラー
- キーストアファイルのパスが正しいか確認
- 環境変数が正しく設定されているか確認
- パスワードとエイリアスが正しいか確認
