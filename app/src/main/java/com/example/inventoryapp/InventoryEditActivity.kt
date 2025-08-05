package com.example.inventoryapp

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.inventoryapp.model.AppDatabase
import com.example.inventoryapp.model.Product
import kotlinx.coroutines.*

class InventoryEditActivity : AppCompatActivity() {

    // 宣告 UI 元件
    private lateinit var txtInfo: TextView
    private lateinit var edtQuantity: EditText
    private lateinit var edtMemo: EditText
    private lateinit var btnUpdate: Button

    private var product: Product? = null // 將傳入的商品儲存起來

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory_edit)

        // 綁定元件
        txtInfo = findViewById(R.id.txtInfo)
        edtQuantity = findViewById(R.id.edtQuantity)
        edtMemo = findViewById(R.id.edtMemo)
        btnUpdate = findViewById(R.id.btnUpdate)

        // 從 Intent 拿到條碼字串
        val barcode = intent.getStringExtra("barcode") ?: return

        // 取得商品資料並顯示
        CoroutineScope(Dispatchers.IO).launch {
            val dao = AppDatabase.getDatabase(applicationContext).productDao()
            product = dao.getProductByBarcode(barcode)

            launch(Dispatchers.Main) {
                if (product != null) {
                    val info = """
                        名稱：${product!!.name}
                        條碼：${product!!.barcode}
                        原本庫存：${product!!.stock_quantity}
                    """.trimIndent()
                    txtInfo.text = info
                } else {
                    txtInfo.text = "查無商品（條碼：$barcode）"
                    btnUpdate.isEnabled = false
                }
            }
        }

        // 點擊更新按鈕
        btnUpdate.setOnClickListener {
            val quantityText = edtQuantity.text.toString()

            if (product == null || quantityText.isBlank()) {
                Toast.makeText(this, "請輸入數量", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val newQty = quantityText.toIntOrNull()
            if (newQty == null || newQty < 0) {
                Toast.makeText(this, "請輸入有效數字", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 更新庫存數量（備註目前不處理）
            CoroutineScope(Dispatchers.IO).launch {
                val updatedProduct = product!!.copy(stock_quantity = newQty)
                AppDatabase.getDatabase(applicationContext).productDao().updateProduct(updatedProduct)

                launch(Dispatchers.Main) {
                    Toast.makeText(this@InventoryEditActivity, "更新成功", Toast.LENGTH_SHORT).show()
                    finish() // 回前一頁
                }
            }
        }
    }
}
