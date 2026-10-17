package com.example.textreplacer

import android.content.Context

/**
 * 功能开关与自定义颜文字的持久化。
 */
class SettingsStore(context: Context) {

    private val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    fun load(): AppSettings = AppSettings(
        replaceEnabled = prefs.getBoolean(KEY_REPLACE, true),
        appendMiaoEnabled = prefs.getBoolean(KEY_MIAO, true),
        miaoSuffix = prefs.getString(KEY_MIAO_SUFFIX, "喵") ?: "喵",
        appendKaomojiEnabled = prefs.getBoolean(KEY_KAOMOJI, true),
        kaomojiCustom = prefs.getString(KEY_KAOMOJI_CUSTOM, "") ?: ""
    )

    fun save(settings: AppSettings) {
        prefs.edit()
            .putBoolean(KEY_REPLACE, settings.replaceEnabled)
            .putBoolean(KEY_MIAO, settings.appendMiaoEnabled)
            .putString(KEY_MIAO_SUFFIX, settings.miaoSuffix)
            .putBoolean(KEY_KAOMOJI, settings.appendKaomojiEnabled)
            .putString(KEY_KAOMOJI_CUSTOM, settings.kaomojiCustom)
            .apply()
    }

    companion object {
        private const val KEY_REPLACE = "replace_enabled"
        private const val KEY_MIAO = "append_miao_enabled"
        private const val KEY_MIAO_SUFFIX = "miao_suffix"
        private const val KEY_KAOMOJI = "append_kaomoji_enabled"
        private const val KEY_KAOMOJI_CUSTOM = "kaomoji_custom"
    }
}
