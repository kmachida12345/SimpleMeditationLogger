# リリース対応完了

このプロジェクトにはリリースに必要な設定が完了しています。

## 📁 追加されたドキュメント

### 1. [RELEASE_GUIDE.md](RELEASE_GUIDE.md)
リリースビルドを作成するための詳細な手順書です。
- キーストアの作成方法
- 署名設定の構成
- Google Playリリース要件
- トラブルシューティング

### 2. [VERSIONING.md](VERSIONING.md)
バージョン管理の完全ガイドです。
- セマンティックバージョニングの説明
- バージョン更新の方法
- リリースワークフロー
- よくある質問

### 3. [bump-version.sh](bump-version.sh)
バージョンを自動更新する便利なスクリプトです。
```bash
./bump-version.sh patch   # バグ修正
./bump-version.sh minor   # 機能追加
./bump-version.sh major   # 大規模変更
```

## ⚙️ 実施済みの設定

### ビルド設定（app/build.gradle.kts）
- ✅ compileSdk構文の修正（36）
- ✅ リリースビルドの最適化
  - コード難読化（minifyEnabled = true）
  - リソース最適化（shrinkResources = true）
- ✅ 署名設定のテンプレート追加
- ✅ バージョン管理の一元化（gradle.propertiesから読み込み）

### ProGuard設定（app/proguard-rules.pro）
- ✅ Room用ルール
- ✅ Kotlinx Serialization用ルール
- ✅ Kotlin Coroutines用ルール
- ✅ Hilt用ルール
- ✅ デバッグ情報の保持

### バージョン管理（gradle.properties）
- ✅ versionCode: 1
- ✅ versionName: 1.0.0
- ✅ コメント付きで管理方法を記載

### セキュリティ（.gitignore）
- ✅ キーストアファイルの除外設定

## 🚀 クイックスタート

### 初回リリースの場合

1. **キーストアを作成**
   ```bash
   keytool -genkey -v -keystore release-keystore.jks -keyalg RSA -keysize 2048 -validity 10000 -alias simplemeditationlogger
   ```

2. **app/build.gradle.kts の署名設定を有効化**
   - `signingConfigs` ブロックのコメントを外す
   - `buildTypes.release` に `signingConfig` を追加

3. **環境変数を設定**
   ```bash
   export KEYSTORE_PASSWORD="your_password"
   export KEY_ALIAS="simplemeditationlogger"
   export KEY_PASSWORD="your_key_password"
   ```

4. **リリースビルドを作成**
   ```bash
   ./gradlew bundleRelease
   ```

### 2回目以降のリリース

1. **バージョンを更新**
   ```bash
   ./bump-version.sh patch  # または minor/major
   ```

2. **変更をコミット**
   ```bash
   git add gradle.properties
   git commit -m "chore: bump version to X.X.X"
   git tag -a vX.X.X -m "Release version X.X.X"
   ```

3. **リリースビルドを作成**
   ```bash
   ./gradlew bundleRelease
   ```

## 📋 リリース前チェックリスト

リリース前に以下を確認してください：

- [ ] 全てのテストが通る: `./gradlew test`
- [ ] バージョンが更新されている
- [ ] 署名設定が正しく構成されている
- [ ] キーストアファイルがgitignoreに含まれている
- [ ] リリースノートを準備
- [ ] スクリーンショットを準備（Google Play用）

## 📚 詳細情報

各トピックの詳細は、それぞれのドキュメントを参照してください：

- リリースビルドの作成 → [RELEASE_GUIDE.md](RELEASE_GUIDE.md)
- バージョン管理 → [VERSIONING.md](VERSIONING.md)
- Git履歴の確認 → `git log --oneline`

## 🔧 トラブルシューティング

問題が発生した場合は：

1. [RELEASE_GUIDE.md](RELEASE_GUIDE.md) のトラブルシューティングセクションを確認
2. ビルドログを確認: `./gradlew assembleRelease --stacktrace`
3. ProGuardのマッピングファイルを確認: `app/build/outputs/mapping/release/`

## 🎯 次のステップ

リリース対応は完了しています。以下の順序で進めてください：

1. 開発を続ける
2. テストを実行
3. バージョンを更新
4. リリースビルドを作成
5. Google Playにアップロード

Happy Releasing! 🎉
