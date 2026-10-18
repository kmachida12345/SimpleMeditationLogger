# バージョン管理ガイド

## 概要

このプロジェクトでは、バージョン情報を `gradle.properties` で一元管理しています。

## バージョン番号の構成

### versionCode（整数）
- Google Playでアプリの新旧を判断するための番号
- リリースごとに必ず1ずつ増やす
- 一度使った番号は二度と使えない
- 現在: `3`

### versionName（文字列）
- ユーザーに表示されるバージョン番号
- セマンティックバージョニング（`MAJOR.MINOR.PATCH`）を使用
- 現在: `1.2.0`

#### セマンティックバージョニングのルール

```
MAJOR.MINOR.PATCH

MAJOR: メジャーバージョン
  - 互換性のない大きな変更
  - API変更、大規模なUI刷新など
  - 例: 1.0.0 → 2.0.0

MINOR: マイナーバージョン
  - 後方互換性のある機能追加
  - 新機能の追加
  - 例: 1.0.0 → 1.1.0

PATCH: パッチバージョン
  - バグ修正
  - 小さな改善
  - 例: 1.0.0 → 1.0.1
```

## バージョン更新方法

### 方法1: 自動更新スクリプトを使用（推奨）

```bash
# バグ修正リリース（1.0.0 → 1.0.1）
./bump-version.sh patch

# 機能追加リリース（1.0.0 → 1.1.0）
./bump-version.sh minor

# 大規模変更リリース（1.0.0 → 2.0.0）
./bump-version.sh major
```

スクリプトが自動的に：
- `gradle.properties` のバージョンを更新
- 次のGitコマンドを提案

### 方法2: 手動更新

`gradle.properties` を直接編集：

```properties
app.versionCode=2
app.versionName=1.0.1
```

## リリースワークフロー

リリースは GitHub Actions で自動化されています（詳細は `RELEASE_GUIDE.md` の「10. Play Console への自動アップロード」）。

### 通常の流れ

1. 機能や修正を master にマージしていく
2. 前回のリリース後に自動作成された **バージョンアップ PR**（`chore/bump-version` ブランチ、versionCode+1 / patch+1）を、リリースしたいタイミングでマージする
   - minor / major にしたい場合は、その PR のブランチ上で `gradle.properties` の `app.versionName` を書き換えてからマージ
3. master で versionCode が増えたことを検知して `v<versionName>` タグが付き、Play Console の internal トラックへ自動アップロードされる
4. アップロード成功後、次のバージョンアップ PR が自動で作られる

### 手動でバージョンを上げる場合

```bash
./bump-version.sh minor
git add gradle.properties
git commit -m "chore: bump version to 1.3.0"
git push origin master   # versionCode が増えていればタグ付け〜アップロードが自動で走る
```

### リリースノート作成

GitHub Releasesでリリースノートを作成：

```markdown
## 変更内容

### 追加
- 新しい瞑想タイマー機能

### 改善
- UI/UXの改善

### 修正
- タイマーのバグ修正

### 技術的変更
- ProGuard設定の最適化
```

## バージョン履歴の例

```
v1.0.0 (code: 1) - 初回リリース
  - 基本的な瞑想記録機能
  - タイマー機能
  - 履歴表示

v1.0.1 (code: 2) - バグ修正リリース
  - タイマーの不具合修正
  - クラッシュ修正

v1.1.0 (code: 3) - 機能追加リリース
  - 統計画面の追加
  - エクスポート機能

v1.1.1 (code: 4) - マイナー修正
  - 統計画面の表示不具合修正

v2.0.0 (code: 5) - メジャーアップデート
  - UI全面刷新
  - データベース構造変更
  - ダークモード対応
```

## よくある質問

### Q: versionCodeを間違えてしまった場合は？

A: Google Playに一度アップロードしたversionCodeは変更できません。次のリリースで正しい番号を使用してください。

### Q: 内部テストでもバージョンを上げる必要がある？

A: はい。Google Playにアップロードする場合は、テスト版でもversionCodeを上げる必要があります。

### Q: 開発中のバージョン番号は？

A: 開発中は `-SNAPSHOT` や `-alpha` を付けることができます：
```properties
app.versionName=1.1.0-SNAPSHOT
```

リリース時に削除してください。

### Q: versionNameとGitタグの関係は？

A: versionNameとGitタグは一致させるのが推奨です：
- versionName: `1.0.1`
- Gitタグ: `v1.0.1`

## 参考リンク

- [セマンティックバージョニング](https://semver.org/lang/ja/)
- [Android App Versioning](https://developer.android.com/studio/publish/versioning)
