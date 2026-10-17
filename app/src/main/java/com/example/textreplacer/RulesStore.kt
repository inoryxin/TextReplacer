package com.example.textreplacer

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

/**
 * 使用 SharedPreferences + JSON 持久化替换规则。
 */
class RulesStore(context: Context) {

    private val prefs = context.getSharedPreferences("rules", Context.MODE_PRIVATE)

    fun loadRules(): List<ReplacementRule> {
        val json = prefs.getString(KEY_RULES, null) ?: return defaultRules()
        return try {
            val arr = JSONArray(json)
            (0 until arr.length()).map { i ->
                val obj = arr.getJSONObject(i)
                ReplacementRule(obj.getString("from"), obj.getString("to"))
            }
        } catch (e: Exception) {
            defaultRules()
        }
    }

    fun saveRules(rules: List<ReplacementRule>) {
        val arr = JSONArray()
        rules.forEach { r ->
            arr.put(JSONObject().put("from", r.from).put("to", r.to))
        }
        prefs.edit().putString(KEY_RULES, arr.toString()).apply()
    }

    private fun defaultRules(): List<ReplacementRule> =
        listOf(ReplacementRule("我", "咱喵"))

    companion object {
        private const val KEY_RULES = "rules_json"
    }
}
