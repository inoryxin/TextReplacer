# 祈歆QQ喵喵助手（QQ / TIM）

一个基于 Android 无障碍服务的文本替换工具：在 QQ / TIM 的聊天输入框里，当你输入句号（以及 `！` `？` `!` `?` `…`）后，自动按你设置的规则替换**当前这个句子**里的文本，并在句尾自动加“喵”。例如规则「我 → 咱喵」，输入 `你好，我叫小美。` 会自动变成 `你好，咱喵叫小美喵。`

## 功能

- UI 顶部实时显示无障碍服务是否已启用（进入/退出页面自动刷新）。
- 三个独立开关：文字替换、句尾添加“喵”、句号后添加颜文字，可分别开关；句尾文本可自定义（默认“喵”）。
- 下方可增删、编辑替换规则（原文本 → 替换为）。
- 自定义颜文字输入框（每行一个）：留空随机使用内置的几十个卖萌颜文字，有内容则随机使用自定义项。
- 服务只监听 QQ / TIM，不监听其它应用，减少耗电与隐私影响。
- 只处理「最后一个句子」，避免反复替换历史内容。

## 工程结构

```
TextReplacer/
├── app/src/main/
│   ├── java/com/example/textreplacer/
│   │   ├── MainActivity.kt          # 主界面：权限状态 + 规则编辑
│   │   ├── TextReplacerService.kt   # 无障碍服务核心逻辑
│   │   ├── RulesAdapter.kt          # 规则列表适配器
│   │   ├── RulesStore.kt            # SharedPreferences 持久化
│   │   └── ReplacementRule.kt       # 规则数据类
│   ├── res/layout/activity_main.xml
│   ├── res/layout/item_rule.xml
│   ├── res/xml/accessibility_service_config.xml  # 服务声明
│   └── AndroidManifest.xml
└── app/build.gradle.kts
```

## 如何构建

1. 安装 [Android Studio](https://developer.android.com/studio)（自带 JDK 17、Gradle、Android SDK）。
2. 用 Android Studio 打开本目录 `TextReplacer`，等待首次 Gradle 同步完成（会自动下载 Gradle 8.7 与所需依赖）。
3. 连接手机（开启 USB 调试）或使用模拟器，点击运行。

> 命令行构建（需本机已安装 JDK 17）：
> - 调试版：`gradlew.bat assembleDebug` → `app/build/outputs/apk/debug/app-debug.apk`
> - 精简发布版（已 R8 混淆 + 资源裁剪 + 签名）：`gradlew.bat assembleRelease` → `app/build/outputs/apk/release/app-release.apk`
> 若提示缺少 `gradle-wrapper.jar`，直接改用 Android Studio 打开即可。

## 使用方法

1. 安装并打开 App，点「去开启」进入系统无障碍设置。
2. 找到「文本替换助手」，打开开关并确认。
3. 回到 App，确认顶部状态变为「已启用」。
4. 在规则列表里配置你的替换规则（默认已内置「我 → 咱喵」），点「保存」。
5. 打开 QQ / TIM，正常打字，输入句号后即可看到替换效果。

## 注意事项 / 兼容性

- 只在目标输入框能被无障碍读取和写入时生效。QQ / TIM 的聊天输入框通常是 `EditText`，可正常工作；部分改版或极简输入框可能无法写入。
- 部分国产 ROM（MIUI / EMUI / ColorOS / OriginOS 等）需要在系统设置里额外开启本应用的「自启动」，并关闭其电池优化，否则后台服务可能被系统杀掉。
- 部分 ROM 对无障碍服务的「修改文本」能力有限制，若替换不生效，请先确认该 ROM 允许无障碍写入文本。
- 替换只在输入**句子结束符**时触发一次；若你在句中想触发，请先输入句号。

## 可自定义的触发符

默认触发符：`。 ！ ？ ! ? …`。如需修改，编辑
`TextReplacerService.kt` 里的 `terminators` 集合即可。
