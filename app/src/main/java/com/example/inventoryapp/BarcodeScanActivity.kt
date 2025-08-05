package com.example.inventoryapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.zxing.integration.android.IntentIntegrator

class BarcodeScanActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 啟用邊緣到邊緣的 UI（Android 11+ 預設沉浸樣式）
        enableEdgeToEdge()

        // 顯示掃描畫面（此頁實際上不需 UI，但為了套用 padding 邏輯仍需設一個 layout）
        setContentView(R.layout.activity_barcode_scan)

        // 套用系統欄位（狀態列、導航列）的 padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 初始化 ZXing 條碼掃描器
        val integrator = IntentIntegrator(this)

        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES) // 支援所有條碼格式
        integrator.setPrompt("請將條碼置於掃描框內") // 掃描時顯示的提示文字
        integrator.setCameraId(0) // 使用後鏡頭（0 = 後鏡頭，1 = 前鏡頭）
        integrator.setBeepEnabled(true) // 掃描成功時發出聲音
        integrator.setOrientationLocked(false) // 允許轉向（不強制直向）
        integrator.initiateScan() // 啟動掃描器畫面
    }

    // 掃描完畢會自動呼叫此函式
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        if (result != null) {
            if (result.contents == null) {
                // 使用者取消掃描
                Toast.makeText(this, "掃描已取消", Toast.LENGTH_SHORT).show()
            } else {
                // 掃描成功，取出條碼字串
                val barcode = result.contents
                Toast.makeText(this, "掃描成功：$barcode", Toast.LENGTH_LONG).show()

                // 📌 TODO: 未來這裡可加上資料庫查詢並跳轉至查詢頁面
                // 例如：startActivity(Intent(this, ProductDetailActivity::class.java).putExtra("barcode", barcode))
            }

            // 掃描完畢後自動返回前一畫面
            finish()
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}
