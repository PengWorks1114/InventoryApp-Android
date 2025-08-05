package com.example.inventoryapp

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.inventoryapp.model.AppDatabase
import kotlinx.coroutines.*

class SettingsActivity : AppCompatActivity() {

    private lateinit var btnClearData: Button
    private lateinit var txtVersion: TextView
    private lateinit var txtLanguage: TextView // 預留語言切換顯示用

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        btnClearData = findViewById(R.id.btnClearData)
        txtVersion = findViewById(R.id.txtVersion)
        txtLanguage = findViewById(R.id.txtLanguage)

        // 顯示版本資訊（可寫死或讀 BuildConfig）
        txtVersion.text = "版本：1.0.0\n開發者：彭淮靖"

        // 預留語言切換顯示
        txtLanguage.text = "目前語言：繁體中文（語言切換功能尚未實作）"

        // 點擊清除資料按鈕
        btnClearData.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("確定要刪除？")
                .setMessage("此操作將清除所有商品與盤點紀錄。")
                .setPositiveButton("確認") { _, _ ->
                    clearAllData()
                }
                .setNegativeButton("取消", null)
                .show()
        }
    }

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
