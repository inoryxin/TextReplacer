package com.example.textreplacer

import android.accessibilityservice.AccessibilityService
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

/**
 * 无障碍服务：监听 QQ / TIM 的输入框文本变化，
 * 在用户输入句号等句子结束符后，按开关执行：
 * 1) 文字替换  2) 句尾加“喵”  3) 句号后加颜文字。
 */
class TextReplacerService : AccessibilityService() {

    private val handler = Handler(Looper.getMainLooper())
    private val rulesStore by lazy { RulesStore(this) }
    private val settingsStore by lazy { SettingsStore(this) }

    /** 最近一次由本服务写入的文本，用于过滤自身写入触发的事件，避免死循环。 */
    private var lastWrittenText: String? = null
    private val clearLastWritten = Runnable { lastWrittenText = null }

    /** 视为“句子结束”的字符，出现即触发。 */
    private val terminators = setOf('。', '！', '？', '!', '?', '…')

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        if (event.eventType != AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) return

        val node = event.source ?: return
        val text = node.text?.toString() ?: return
        if (text.isEmpty()) return

        // 忽略我们自己写入文本后触发的回调事件
        if (text == lastWrittenText) return

        // 只处理可编辑文本框
        if (!node.isEditableTextField()) return

        // 仅当句末出现句子结束符时触发
        if (!text.last().isTerminator()) return

        val settings = settingsStore.load()
        val rules = if (settings.replaceEnabled) {
            rulesStore.loadRules().filter { it.from.isNotBlank() }
        } else emptyList()

        val newText = transformText(text, rules, settings)
        if (newText == text) return

        setNodeText(node, newText)
    }

    /**
     * 对“最后一个句子”依次执行：文字替换 → 句尾加“喵” → 句号后加颜文字。
     */
    private fun transformText(
        text: String,
        rules: List<ReplacementRule>,
        settings: AppSettings
    ): String {
        val end = text.length - 1
        var start = 0
        for (i in end - 1 downTo 0) {
            if (text[i].isTerminator()) {
                start = i + 1
                break
            }
        }

        var sentence = text.substring(start, end)

        if (settings.replaceEnabled) {
            for (rule in rules) {
                if (rule.from.isNotEmpty()) {
                    sentence = sentence.replace(rule.from, rule.to)
                }
            }
        }

        if (settings.appendMiaoEnabled) {
            val suffix = settings.miaoSuffix
            if (suffix.isNotEmpty() && sentence.isNotEmpty() && !sentence.endsWith(suffix)) {
                sentence += suffix
            }
        }

        var result = text.substring(0, start) + sentence + text[end]

        if (settings.appendKaomojiEnabled && sentence.isNotEmpty()) {
            result += pickKaomoji(settings.kaomojiCustom)
        }
        return result
    }

    /** 自定义颜文字非空时随机取自定义项，否则随机取内置颜文字库。 */
    private fun pickKaomoji(custom: String): String {
        val lines = custom.lines().map { it.trim() }.filter { it.isNotEmpty() }
        val pool = if (lines.isNotEmpty()) lines else KaomojiPreset.list
        return pool.random()
    }

    private fun setNodeText(node: AccessibilityNodeInfo, text: String) {
        val args = Bundle()
        args.putCharSequence(
            AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
            text
        )
        lastWrittenText = text
        handler.removeCallbacks(clearLastWritten)
        handler.postDelayed(clearLastWritten, 600)

        node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, args)

        // 把光标移到末尾，避免设置文本后光标跳回开头
        val selArgs = Bundle()
        selArgs.putInt(AccessibilityNodeInfo.ACTION_ARGUMENT_SELECTION_START_INT, text.length)
        selArgs.putInt(AccessibilityNodeInfo.ACTION_ARGUMENT_SELECTION_END_INT, text.length)
        node.performAction(AccessibilityNodeInfo.ACTION_SET_SELECTION, selArgs)
    }

    private fun Char.isTerminator(): Boolean = this in terminators

    private fun AccessibilityNodeInfo.isEditableTextField(): Boolean {
        if (isEditable) return true
        val cls = className?.toString() ?: return false
        return cls.contains("EditText") || cls.contains("AutoCompleteTextView")
    }

    override fun onInterrupt() {}
}
