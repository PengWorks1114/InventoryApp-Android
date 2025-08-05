package com.example.inventoryapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.inventoryapp.adapter.ProductAdapter
import com.example.inventoryapp.model.AppDatabase
import kotlinx.coroutines.*

class InventoryListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory_list)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 讀取資料並設定 Adapter
        CoroutineScope(Dispatchers.IO).launch {
            val productList = AppDatabase.getDatabase(applicationContext).productDao().getAllProducts()

            launch(Dispatchers.Main) {
                recyclerView.adapter = ProductAdapter(this@InventoryListActivity, productList)
            }
        }
    }
}
