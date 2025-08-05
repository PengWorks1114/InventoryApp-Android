package com.example.inventoryapp

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        // 將按鈕與 ID 綁定
        val btnStartInventory: Button = findViewById(R.id.btnStartInventory)
        val btnSearchProduct: Button = findViewById(R.id.btnSearchProduct)
        val btnExportReport: Button = findViewById(R.id.btnExportReport)

        btnStartInventory.setOnClickListener {
            // TODO: 跳轉至盤點畫面
        }

        btnSearchProduct.setOnClickListener {
            // TODO: 跳轉至查詢畫面
        }

        btnExportReport.setOnClickListener {
            // TODO: 跳轉至匯出畫面
        }

    }
}