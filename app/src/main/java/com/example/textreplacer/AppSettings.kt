package com.example.textreplacer

/**
 * 应用设置：三个功能开关 + 自定义颜文字。
 */
data class AppSettings(
    val replaceEnabled: Boolean = true,
    val appendMiaoEnabled: Boolean = true,
    val miaoSuffix: String = "喵",
    val appendKaomojiEnabled: Boolean = true,
    val kaomojiCustom: String = ""
)
