#!/bin/bash
# バージョン更新スクリプト
# 使い方: ./bump-version.sh [patch|minor|major]

set -e

GRADLE_PROPERTIES="gradle.properties"

if [ ! -f "$GRADLE_PROPERTIES" ]; then
    echo "Error: gradle.properties が見つかりません"
    exit 1
fi

# 現在のバージョンを取得
current_version_code=$(grep "app.versionCode=" $GRADLE_PROPERTIES | cut -d'=' -f2)
current_version_name=$(grep "app.versionName=" $GRADLE_PROPERTIES | cut -d'=' -f2)

echo "現在のバージョン: $current_version_name (code: $current_version_code)"

# バージョンを分解
IFS='.' read -r -a version_parts <<< "$current_version_name"
major="${version_parts[0]}"
minor="${version_parts[1]}"
patch="${version_parts[2]}"

# 引数に応じてバージョンを更新
case "$1" in
    patch)
        patch=$((patch + 1))
        echo "パッチバージョンを更新"
        ;;
    minor)
        minor=$((minor + 1))
        patch=0
        echo "マイナーバージョンを更新"
        ;;
    major)
        major=$((major + 1))
        minor=0
        patch=0
        echo "メジャーバージョンを更新"
        ;;
    *)
        echo "使い方: $0 [patch|minor|major]"
        echo ""
        echo "  patch: バグ修正 (例: 1.0.0 -> 1.0.1)"
        echo "  minor: 機能追加 (例: 1.0.0 -> 1.1.0)"
        echo "  major: 破壊的変更 (例: 1.0.0 -> 2.0.0)"
        exit 1
        ;;
esac

# 新しいバージョン
new_version_code=$((current_version_code + 1))
new_version_name="$major.$minor.$patch"

echo "新しいバージョン: $new_version_name (code: $new_version_code)"

# 確認
read -p "この変更を適用しますか? [y/N]: " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo "キャンセルしました"
    exit 0
fi

# gradle.properties を更新
sed -i.bak "s/app.versionCode=.*/app.versionCode=$new_version_code/" $GRADLE_PROPERTIES
sed -i.bak "s/app.versionName=.*/app.versionName=$new_version_name/" $GRADLE_PROPERTIES
rm -f ${GRADLE_PROPERTIES}.bak

echo "✅ バージョンを更新しました: $new_version_name (code: $new_version_code)"
echo ""
echo "次のステップ:"
echo "1. git add gradle.properties"
echo "2. git commit -m \"chore: bump version to $new_version_name\""
echo "3. git tag -a v$new_version_name -m \"Release version $new_version_name\""
echo "4. git push origin master --tags"
