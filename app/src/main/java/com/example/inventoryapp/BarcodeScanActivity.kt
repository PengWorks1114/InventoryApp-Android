package com.example.inventoryapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.inventoryapp.model.AppDatabase
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.coroutines.*

class BarcodeScanActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ 邊緣到邊緣 UI（Android 11+ 沉浸樣式）
        enableEdgeToEdge()

        // ✅ 套用 layout，保留 padding 處理
        setContentView(R.layout.activity_barcode_scan)

        // ✅ 套用狀態列／導航列 padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // ✅ 初始化 ZXing 掃描器
        val integrator = IntentIntegrator(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
        integrator.setPrompt("請將條碼置於掃描框內")
        integrator.setCameraId(0)
        integrator.setBeepEnabled(true)
        integrator.setOrientationLocked(false)
        integrator.initiateScan()
    }

    // ✅ 掃描完畢後自動呼叫
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "掃描已取消", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                val barcode = result.contents
                Toast.makeText(this, "掃描成功：$barcode", Toast.LENGTH_SHORT).show()

                // ✅ 掃描後查詢資料庫（背景執行）
                CoroutineScope(Dispatchers.IO).launch {
                    val dao = AppDatabase.getDatabase(applicationContext).productDao()
                    val product = dao.getProductByBarcode(barcode)

                    launch(Dispatchers.Main) {
                        if (product != null) {
                            // ✅ 商品存在 → 進入盤點頁
                            val intent = Intent(this@BarcodeScanActivity, InventoryEditActivity::class.java)
                            intent.putExtra("barcode", barcode)
                            startActivity(intent)
                        } else {
                            // ❌ 商品不存在 → 進入新增頁，並自動帶入條碼
                            Toast.makeText(this@BarcodeScanActivity, "查無此商品，請新增", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@BarcodeScanActivity, AddProductActivity::class.java)
                            intent.putExtra("barcode", barcode)
                            startActivity(intent)
                        }

                        // ✅ 結束掃描頁
                        finish()
                    }
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}
