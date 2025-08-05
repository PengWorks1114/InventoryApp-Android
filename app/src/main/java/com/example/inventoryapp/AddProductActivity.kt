package com.example.inventoryapp

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.inventoryapp.model.AppDatabase
import com.example.inventoryapp.model.Product
import kotlinx.coroutines.*

class AddProductActivity : AppCompatActivity() {

    // 宣告 UI 元件
    private lateinit var edtName: EditText
    private lateinit var edtBarcode: EditText
    private lateinit var edtLocation: EditText
    private lateinit var edtUnit: EditText
    private lateinit var edtQuantity: EditText
    private lateinit var btnAdd: Button

    private lateinit var btnPickImage: Button
    private lateinit var imgPreview: ImageView
    private var selectedImagePath: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

        // 綁定元件
        edtName = findViewById(R.id.edtName)
        edtBarcode = findViewById(R.id.edtBarcode)
        edtLocation = findViewById(R.id.edtLocation)
        edtUnit = findViewById(R.id.edtUnit)
        edtQuantity = findViewById(R.id.edtQuantity)
        btnAdd = findViewById(R.id.btnAdd)

        btnAdd.setOnClickListener {
            val name = edtName.text.toString()
            val barcode = edtBarcode.text.toString()
            val location = edtLocation.text.toString()
            val unit = edtUnit.text.toString()
            val quantity = edtQuantity.text.toString().toIntOrNull()

            // 基本驗證
            if (name.isBlank() || barcode.isBlank() || location.isBlank() || unit.isBlank() || quantity == null) {
                Toast.makeText(this, "請填寫所有欄位（數量需為數字）", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val product = Product(
                name = name,
                barcode = barcode,
                location = location,
                unit = unit,
                stock_quantity = quantity,
                image_path = null
            )

            // 插入資料到 Room
            CoroutineScope(Dispatchers.IO).launch {
                AppDatabase.getDatabase(applicationContext).productDao().insertProduct(product)

                launch(Dispatchers.Main) {
                    Toast.makeText(this@AddProductActivity, "商品新增成功", Toast.LENGTH_SHORT).show()
                    finish() // 返回上一頁
                }
            }
        }
    }
}
