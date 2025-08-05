package com.example.inventoryapp

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.inventoryapp.model.AppDatabase
import com.example.inventoryapp.model.Product
import kotlinx.coroutines.*

class ProductSearchActivity : AppCompatActivity() {

    // 宣告 UI 元件變數
    private lateinit var edtKeyword: EditText
    private lateinit var btnSearch: Button
    private lateinit var lstResults: ListView
    private lateinit var adapter: ArrayAdapter<String>

    // 儲存查詢結果（商品）
    private var productList = listOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_search)

        // 綁定畫面元件
        edtKeyword = findViewById(R.id.edtKeyword)
        btnSearch = findViewById(R.id.btnSearch)
        lstResults = findViewById(R.id.lstResults)

        // 點擊查詢按鈕時執行查詢邏輯
        btnSearch.setOnClickListener {
            val keyword = edtKeyword.text.toString()

            if (keyword.isBlank()) {
                Toast.makeText(this, "請輸入關鍵字", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 執行背景查詢
            CoroutineScope(Dispatchers.IO).launch {
                val dao = AppDatabase.getDatabase(applicationContext).productDao()
                productList = dao.searchProducts(keyword)

                // 回到主執行緒顯示結果
                launch(Dispatchers.Main) {
                    if (productList.isEmpty()) {
                        Toast.makeText(this@ProductSearchActivity, "查無資料", Toast.LENGTH_SHORT).show()
                        lstResults.adapter = null
                    } else {
                        val names = productList.map { "${it.name}（${it.barcode}）" }
                        adapter = ArrayAdapter(this@ProductSearchActivity, android.R.layout.simple_list_item_1, names)
                        lstResults.adapter = adapter
                    }
                }
            }
        }

        // 點選結果項目（後續可跳轉）
        lstResults.setOnItemClickListener { _, _, position, _ ->
            val product = productList[position]
            val info = """
                名稱：${product.name}
                條碼：${product.barcode}
                位置：${product.location}
                單位：${product.unit}
                庫存：${product.stock_quantity}
            """.trimIndent()
            Toast.makeText(this, info, Toast.LENGTH_LONG).show()
        }
    }
}
