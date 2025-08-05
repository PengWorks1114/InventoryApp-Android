package com.example.inventoryapp

import android.os.Bundle
import android.widget.ListView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.inventoryapp.model.AppDatabase
import com.example.inventoryapp.model.Product
import com.example.inventoryapp.model.StockLog
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class LogListActivity : AppCompatActivity() {

    private lateinit var lstLogs: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_list)

        lstLogs = findViewById(R.id.lstLogs)

        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(applicationContext)
            val logs = db.stockLogDao().getAllLogs()      // 取得所有紀錄
            val products = db.productDao().getAllProducts() // 用來找出商品名稱

            val productMap = products.associateBy { it.id } // 快速查名稱用 map

            val displayList = logs.map { log ->
                val productName = productMap[log.product_id]?.name ?: "(未知商品)"
                val timeStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                    .format(Date(log.updated_at))
                val memoText = if (!log.memo.isNullOrBlank()) "備註：${log.memo}" else ""

                "$productName｜數量：${log.input_quantity}｜$timeStr\n$memoText"
            }

            launch(Dispatchers.Main) {
                val adapter = ArrayAdapter(this@LogListActivity,
                    android.R.layout.simple_list_item_1, displayList)
                lstLogs.adapter = adapter
            }
        }
    }
}
