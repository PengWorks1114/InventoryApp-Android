package com.example.inventoryapp

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.inventoryapp.model.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProductDetailActivity : AppCompatActivity() {

    // UI 元件
    private lateinit var txtProductInfo: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        // 綁定 UI 元件
        txtProductInfo = findViewById(R.id.txtProductInfo)

        // 從 Intent 中取出條碼字串
        val barcode = intent.getStringExtra("barcode") ?: return

        // 使用協程（背景執行）從資料庫查詢商品
        CoroutineScope(Dispatchers.IO).launch {
            val dao = AppDatabase.getDatabase(applicationContext).productDao()
            val product = dao.getProductByBarcode(barcode)

            // 回到主執行緒更新畫面
            launch(Dispatchers.Main) {
                if (product != null) {
                    // 顯示商品資訊
                    val info = """
                        名稱：${product.name}
                        條碼：${product.barcode}
                        位置：${product.location}
                        單位：${product.unit}
                        庫存：${product.stock_quantity}
                    """.trimIndent()
                    txtProductInfo.text = info
                } else {
                    txtProductInfo.text = "查無商品資料（條碼：$barcode）"
                }
            }
        }
    }
}
