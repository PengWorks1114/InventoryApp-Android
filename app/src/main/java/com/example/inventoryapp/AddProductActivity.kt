package com.example.inventoryapp

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
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

    // 儲存選擇的圖片路徑（不論來源為相簿或相機）
    private var selectedImagePath: String? = null

    // 儲存相機拍攝的暫存檔案 Uri
    private var cameraImageUri: Uri? = null


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

        // 點選圖片選擇按鈕時，顯示「選擇來源」對話框（相簿或相機）
        btnPickImage.setOnClickListener {
            val options = arrayOf("從相簿選擇", "使用相機拍照")
            AlertDialog.Builder(this)
                .setTitle("選擇圖片來源")
                .setItems(options) { _, which ->
                    when (which) {
                        0 -> selectImageFromGallery()
                        1 -> captureImageFromCamera()
                    }
                }.show()
        }

        // 若有從 Intent 傳來的條碼，則預填到條碼欄位
        val intentBarcode = intent.getStringExtra("barcode")
        if (!intentBarcode.isNullOrEmpty()) {
            edtBarcode.setText(intentBarcode)
        }

        // 點選「新增商品」按鈕時
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

            // 封裝成 Product 物件
            val product = Product(
                name = name,
                barcode = barcode,
                location = location,
                unit = unit,
                stock_quantity = quantity,
                image_path = selectedImagePath
            )

            // 插入資料到 Room 資料庫（使用協程）
            CoroutineScope(Dispatchers.IO).launch {
                AppDatabase.getDatabase(applicationContext).productDao().insertProduct(product)

                launch(Dispatchers.Main) {
                    Toast.makeText(this@AddProductActivity, "商品新增成功", Toast.LENGTH_SHORT).show()
                    finish() // 返回上一頁
                }
            }
        }
    }

    // 開啟相簿選擇圖片
    private fun selectImageFromGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, 101)
    }

    // 開啟相機拍照
    private fun captureImageFromCamera() {
        val fileName = "camera_photo_${System.currentTimeMillis()}.jpg"
        val imageFile = File(getExternalFilesDir(null), fileName)
        cameraImageUri = Uri.fromFile(imageFile)

        val intent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, cameraImageUri)
        startActivityForResult(intent, 102)
    }

    // 處理圖片返回結果（相簿或相機）
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            101 -> { // 相簿選取
                if (resultCode == RESULT_OK && data != null) {
                    val uri = data.data ?: return
                    val inputStream = contentResolver.openInputStream(uri)
                    val fileName = "gallery_photo_${System.currentTimeMillis()}.jpg"
                    val file = File(getExternalFilesDir(null), fileName)
                    val outputStream = file.outputStream()
                    inputStream?.copyTo(outputStream)
                    inputStream?.close()
                    outputStream.close()

                    selectedImagePath = file.absolutePath
                    imgPreview.setImageURI(uri)
                }
            }

            102 -> { // 相機拍照
                if (resultCode == RESULT_OK && cameraImageUri != null) {
                    selectedImagePath = File(cameraImageUri!!.path!!).absolutePath
                    imgPreview.setImageURI(cameraImageUri)
                }
            }
        }
    }
}
