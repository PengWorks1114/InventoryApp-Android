package com.example.inventoryapp

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.inventoryapp.model.AppDatabase
import kotlinx.coroutines.*
import java.util.*

class SettingsActivity : AppCompatActivity() {

    private lateinit var btnClearData: Button
    private lateinit var txtVersion: TextView
    private lateinit var txtLanguage: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        btnClearData = findViewById(R.id.btnClearData)
        txtVersion = findViewById(R.id.txtVersion)
        txtLanguage = findViewById(R.id.txtLanguage)

        txtVersion.text = "版本：1.0.0\n開發者：彭淮靖"

        // 顯示目前語言
        val currentLang = getCurrentLanguageName()
        txtLanguage.text = "目前語言：$currentLang（點此切換）"

        // 點擊切換語言
        txtLanguage.setOnClickListener {
            showLanguagePicker()
        }

        // 清除資料按鈕
        btnClearData.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("確定要刪除？")
                .setMessage("此操作將清除所有商品與盤點紀錄。")
                .setPositiveButton("確認") { _, _ -> clearAllData() }
                .setNegativeButton("取消", null)
                .show()
        }
    }

    // 顯示語言選擇器
    private fun showLanguagePicker() {
        val languages = arrayOf("繁體中文", "English", "日本語")
        val languageCodes = arrayOf("zh", "en", "ja")

        AlertDialog.Builder(this)
            .setTitle("選擇語言")
            .setItems(languages) { _, which ->
                setLocale(languageCodes[which])
            }
            .show()
    }

    // 設定語系並重新啟動 App
    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration()
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)

        // 重啟 Activity 以套用新語言
        val intent = Intent(this, SettingsActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    // 取得當前語言中文名稱
    private fun getCurrentLanguageName(): String {
        return when (Locale.getDefault().language) {
            "zh" -> "繁體中文"
            "en" -> "English"
            "ja" -> "日本語"
            else -> "未知"
        }
    }

    // 資料清除
    private fun clearAllData() {
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(applicationContext)
            db.productDao().deleteAll()
            db.stockLogDao().deleteAll()

            launch(Dispatchers.Main) {
                Toast.makeText(this@SettingsActivity, "資料已清除", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
