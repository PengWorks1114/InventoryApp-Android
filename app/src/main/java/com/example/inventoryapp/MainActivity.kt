package com.example.inventoryapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.inventoryapp.model.AppDatabase
import com.example.inventoryapp.util.CsvExportUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
            // 跳轉至掃描畫面（BarcodeScanActivity）
            val intent = Intent(this, BarcodeScanActivity::class.java)
            startActivity(intent)
        }

        btnSearchProduct.setOnClickListener {
            // 跳轉至查詢畫面 (ProductSearchActivity)
            val intent = Intent(this, ProductSearchActivity::class.java)
            startActivity(intent)
        }

        btnExportReport.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val dao = AppDatabase.getDatabase(applicationContext).productDao()
                val products = dao.getAllProducts()

                launch(Dispatchers.Main) {
                    CsvExportUtil.exportProductsToCSV(this@MainActivity, products)
                }
            }
        }

        findViewById<Button>(R.id.btnViewLogs).setOnClickListener {
            startActivity(Intent(this, LogListActivity::class.java))
        }

        findViewById<Button>(R.id.btnAddProduct).setOnClickListener {
            startActivity(Intent(this, AddProductActivity::class.java))
        }



    }
}