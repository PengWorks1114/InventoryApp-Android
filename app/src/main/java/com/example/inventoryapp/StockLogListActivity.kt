package com.example.inventoryapp

import android.os.Bundle
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.inventoryapp.model.AppDatabase
import com.example.inventoryapp.model.StockLog
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class StockLogListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StockLogAdapter
    private val stockLogs = mutableListOf<StockLog>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stock_log_list)

        recyclerView = findViewById(R.id.recyclerStockLog)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = StockLogAdapter(stockLogs)
        recyclerView.adapter = adapter

        val productId = intent.getIntExtra("product_id", -1)
        if (productId != -1) {
            loadLogs(productId)
        } else {
            Toast.makeText(this, "無效的商品 ID", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun loadLogs(productId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val logs = AppDatabase.getDatabase(applicationContext)
                .stockLogDao().getLogsByProduct(productId)

            withContext(Dispatchers.Main) {
                stockLogs.clear()
                stockLogs.addAll(logs)
                adapter.notifyDataSetChanged()
            }
        }
    }

    class StockLogAdapter(private val logs: List<StockLog>) :
        RecyclerView.Adapter<StockLogAdapter.LogViewHolder>() {

        class LogViewHolder(view: LinearLayout) : RecyclerView.ViewHolder(view) {
            val txtQuantity: TextView = view.findViewById(R.id.txtQuantity)
            val txtMemo: TextView = view.findViewById(R.id.txtMemo)
            val txtTime: TextView = view.findViewById(R.id.txtTime)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
            val view = LinearLayout.inflate(parent.context, R.layout.item_stock_log, null) as LinearLayout
            return LogViewHolder(view)
        }

        override fun getItemCount(): Int = logs.size

        override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
            val log = logs[position]
            holder.txtQuantity.text = "輸入數量：${log.input_quantity}"
            holder.txtMemo.text = "備註：${log.memo ?: "無"}"
            val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
            holder.txtTime.text = "時間：${sdf.format(Date(log.updated_at))}"
        }
    }
}
