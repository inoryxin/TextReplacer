package com.example.textreplacer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.switchmaterial.SwitchMaterial

class MainActivity : AppCompatActivity() {

    private lateinit var statusText: TextView
    private lateinit var adapter: RulesAdapter
    private lateinit var switchReplace: SwitchMaterial
    private lateinit var switchMiao: SwitchMaterial
    private lateinit var switchKaomoji: SwitchMaterial
    private lateinit var miaoSuffixEdit: EditText
    private lateinit var kaomojiEdit: EditText

    private val rulesStore by lazy { RulesStore(this) }
    private val settingsStore by lazy { SettingsStore(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statusText = findViewById(R.id.statusText)
        switchReplace = findViewById(R.id.switchReplace)
        switchMiao = findViewById(R.id.switchMiao)
        switchKaomoji = findViewById(R.id.switchKaomoji)
        miaoSuffixEdit = findViewById(R.id.miaoSuffixEdit)
        kaomojiEdit = findViewById(R.id.kaomojiEdit)

        val recycler: RecyclerView = findViewById(R.id.rulesRecycler)
        adapter = RulesAdapter()
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter
        recycler.isNestedScrollingEnabled = false
        adapter.submit(rulesStore.loadRules())

        // 读取设置
        val settings = settingsStore.load()
        switchReplace.isChecked = settings.replaceEnabled
        switchMiao.isChecked = settings.appendMiaoEnabled
        switchKaomoji.isChecked = settings.appendKaomojiEnabled
        miaoSuffixEdit.setText(settings.miaoSuffix)
        kaomojiEdit.setText(settings.kaomojiCustom)

        // 开关即时保存
        switchReplace.setOnCheckedChangeListener { _, _ -> saveSettings() }
        switchMiao.setOnCheckedChangeListener { _, _ -> saveSettings() }
        switchKaomoji.setOnCheckedChangeListener { _, _ -> saveSettings() }

        findViewById<Button>(R.id.openSettingsBtn).setOnClickListener {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }

        findViewById<Button>(R.id.addRuleBtn).setOnClickListener { adapter.addRule() }

        findViewById<Button>(R.id.saveBtn).setOnClickListener { saveAll() }
    }

    override fun onResume() {
        super.onResume()
        refreshStatus()
    }

    override fun onPause() {
        super.onPause()
        saveAll()
    }

    private fun refreshStatus() {
        val enabled = isAccessibilityServiceEnabled(this, TextReplacerService::class.java)
        statusText.text = getString(
            if (enabled) R.string.status_enabled else R.string.status_disabled
        )
        statusText.setTextColor(
            ContextCompat.getColor(this, if (enabled) R.color.status_on else R.color.status_off)
        )
    }

    private fun saveSettings() {
        settingsStore.save(
            AppSettings(
                replaceEnabled = switchReplace.isChecked,
                appendMiaoEnabled = switchMiao.isChecked,
                miaoSuffix = miaoSuffixEdit.text?.toString() ?: "",
                appendKaomojiEnabled = switchKaomoji.isChecked,
                kaomojiCustom = kaomojiEdit.text?.toString() ?: ""
            )
        )
    }

    private fun saveAll() {
        rulesStore.saveRules(adapter.getRules())
        saveSettings()
        Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show()
    }

    companion object {
        fun isAccessibilityServiceEnabled(context: Context, service: Class<*>): Boolean {
            val expected = ComponentName(context, service)
            val enabled = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            ) ?: return false
            return enabled.split(':').any { part ->
                ComponentName.unflattenFromString(part) == expected
            }
        }
    }
}
