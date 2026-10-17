# 无障碍服务由系统通过 manifest 实例化，保留其类名防止被 R8 混淆
-keep class com.example.textreplacer.TextReplacerService { *; }
