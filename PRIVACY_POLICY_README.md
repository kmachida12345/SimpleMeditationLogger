# プライバシーポリシー

このディレクトリには、Simple Meditation Logger のプライバシーポリシーが含まれています。

## ファイル

- `privacy_policy.html` - 日本語版プライバシーポリシー
- `privacy_policy_en.html` - 英語版プライバシーポリシー

## 内容

プライバシーポリシーには以下の情報が含まれています：

1. **収集する情報**
   - 瞑想記録データ（ローカル保存）
   - Health Connect 連携データ（オプション、今後実装予定）

2. **データの保存場所**
   - ローカルストレージ
   - Health Connect（オプション）

3. **情報の利用目的**
   - 瞑想記録の保存と表示
   - 統計情報の生成
   - Health Connect 経由での健康データ連携

4. **データの安全性**
   - 外部サーバーへの送信なし
   - デバイス内の保護されたストレージに保存
   - 広告・分析サービスの不使用

5. **Health Connect の権限**
   - マインドフルネスセッションの書き込み権限
   - 完全オプトイン
   - いつでも無効化可能

## ホスティング方法

プライバシーポリシーは公開 URL でアクセス可能にする必要があります。以下の方法があります：

### 方法1: GitHub Pages（推奨）

1. GitHubリポジトリの Settings → Pages を開く
2. Source を「Deploy from a branch」に設定
3. Branch を「main」または「master」、フォルダを「/」に設定
4. Save をクリック
5. HTMLファイルをルートディレクトリに配置
6. 公開URL: `https://[username].github.io/[repository]/privacy_policy.html`

**手順:**
```bash
# privacy_policy.html をリポジトリのルートに配置（すでに完了）
git add privacy_policy.html privacy_policy_en.html
git commit -m "docs: add privacy policy"
git push origin master
```

GitHub Pages を有効化すると、以下のURLでアクセス可能になります：
- 日本語: `https://kmachida12345.github.io/SimpleMeditationLogger/privacy_policy.html`
- 英語: `https://kmachida12345.github.io/SimpleMeditationLogger/privacy_policy_en.html`

### 方法2: その他のホスティングサービス

- **Netlify**: 無料、カスタムドメイン可能
- **Vercel**: 無料、高速
- **Firebase Hosting**: Googleのサービス
- **自分のWebサーバー**: 完全コントロール

## Google Play Console での設定

1. Google Play Console にログイン
2. アプリを選択
3. 「ポリシー」→「アプリのコンテンツ」を開く
4. 「プライバシーポリシー」セクションで URL を入力
   - 例: `https://kmachida12345.github.io/SimpleMeditationLogger/privacy_policy.html`
5. 保存

## メールアドレスの更新

プライバシーポリシー内のメールアドレスは例です。実際のメールアドレスに更新してください：

1. `privacy_policy.html` を開く
2. `kmachida12345@example.com` を実際のメールアドレスに変更
3. `privacy_policy_en.html` も同様に更新

```html
<!-- 例 -->
<strong>メールアドレス:</strong> <a href="mailto:your-email@example.com">your-email@example.com</a>
```

## 更新時の注意事項

プライバシーポリシーを更新する場合：

1. **最終更新日を変更**: `最終更新日: 20XX年XX月XX日`
2. **重要な変更の場合**: アプリ内で通知を表示することを推奨
3. **バージョン管理**: 変更履歴を Git で管理
4. **ユーザー通知**: 大幅な変更の場合はアプリのアップデートノートに記載

## 確認事項

プライバシーポリシー公開前に以下を確認：

- [ ] メールアドレスが正しい
- [ ] GitHub リンクが正しい
- [ ] HTML が正しく表示される（ブラウザで確認）
- [ ] 日本語版と英語版の内容が一致している
- [ ] リンクが機能する
- [ ] モバイルで正しく表示される
- [ ] GitHub Pages または他のホスティングサービスで公開済み
- [ ] Google Play Console に URL を登録済み

## 法的注意事項

このプライバシーポリシーはテンプレートです。以下の点に注意してください：

- 地域の法律（GDPR、CCPA等）に準拠しているか確認
- 必要に応じて弁護士に相談
- アプリの実装内容と一致しているか確認
- 定期的に見直しと更新を実施

## サポート

プライバシーポリシーに関する質問がある場合：

- Google Play のプライバシーポリシー要件: https://support.google.com/googleplay/android-developer/answer/9859455
- Health Connect のプライバシー: https://developer.android.com/health-and-fitness/guides/health-connect
