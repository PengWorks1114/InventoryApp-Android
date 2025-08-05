package com.example.inventoryapp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.inventoryapp.model.AppDatabase
import com.example.inventoryapp.model.Product
import kotlinx.coroutines.*
import java.io.File

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

        btnPickImage = findViewById(R.id.btnPickImage)
        imgPreview = findViewById(R.id.imgPreview)

        btnPickImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, 101)
        }

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
                image_path = selectedImagePath
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == RESULT_OK && data != null) {
            val uri = data.data ?: return

            // 產生儲存圖片的路徑
            val inputStream = contentResolver.openInputStream(uri)
            val fileName = "product_${System.currentTimeMillis()}.jpg"
            val file = File(getExternalFilesDir(null), fileName)

            val outputStream = file.outputStream()
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()

            selectedImagePath = file.absolutePath
            imgPreview.setImageURI(uri)
        }
    }


}
