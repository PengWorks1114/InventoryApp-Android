package com.example.inventoryapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.inventoryapp.model.AppDatabase
import com.example.inventoryapp.model.Product
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class ProductDetailActivity : AppCompatActivity() {

    // UI 元件
    private lateinit var txtProductInfo: TextView
    private lateinit var btnViewLogs: Button // ⬅️ 查看紀錄按鈕
    private var product: Product? = null     // ⬅️ 儲存當前查詢到的商品資料（用於跳轉時取用 ID）

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        // 綁定 UI 元件
        txtProductInfo = findViewById(R.id.txtProductInfo)
        val imgProductDetail = findViewById<ImageView>(R.id.imgProductDetail)
        btnViewLogs = findViewById(R.id.btnViewLogs) // 綁定查看紀錄按鈕

        // 從 Intent 中取出條碼字串
        val barcode = intent.getStringExtra("barcode") ?: return

        // 使用協程（背景執行）從資料庫查詢商品
        CoroutineScope(Dispatchers.IO).launch {
            val dao = AppDatabase.getDatabase(applicationContext).productDao()
            val result = dao.getProductByBarcode(barcode)

            // 回到主執行緒更新畫面
            launch(Dispatchers.Main) {
                if (result != null) {
                    product = result // 將查詢結果暫存，供按鈕跳轉使用

                    // 顯示商品資訊
                    val info = """
                        名稱：${result.name}
                        條碼：${result.barcode}
                        位置：${result.location}
                        單位：${result.unit}
                        庫存：${result.stock_quantity}
                    """.trimIndent()
                    txtProductInfo.text = info

                    // 顯示圖片（如果有的話）
                    result.image_path?.let {
                        val file = File(it)
                        if (file.exists()) {
                            imgProductDetail.setImageURI(Uri.fromFile(file))
                        }
                    }

                } else {
                    txtProductInfo.text = "查無商品資料（條碼：$barcode）"
                }
            }
        }

        // 點擊查看紀錄按鈕 → 跳轉至紀錄一覽畫面
        btnViewLogs.setOnClickListener {
            product?.let {
                val intent = Intent(this, StockLogListActivity::class.java)
                intent.putExtra("product_id", it.id)
                startActivity(intent)
            } ?: run {
                Toast.makeText(this, "無法取得商品資訊", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
