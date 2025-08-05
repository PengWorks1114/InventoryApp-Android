package com.example.inventoryapp

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.inventoryapp.model.AppDatabase
import com.example.inventoryapp.model.Product
import com.example.inventoryapp.model.StockLog
import kotlinx.coroutines.*
import java.io.File

class InventoryEditActivity : AppCompatActivity() {

    // 宣告 UI 元件
    private lateinit var txtInfo: TextView
    private lateinit var edtQuantity: EditText
    private lateinit var edtMemo: EditText
    private lateinit var btnUpdate: Button
    private lateinit var imgProduct: ImageView
    private lateinit var btnChangeImage: Button

    private var product: Product? = null // 儲存傳入的商品資料
    private var cameraImageUri: Uri? = null // 相機圖片暫存位置
    private var selectedImagePath: String? = null // 最終圖片路徑

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory_edit)

        // 綁定元件
        txtInfo = findViewById(R.id.txtInfo)
        edtQuantity = findViewById(R.id.edtQuantity)
        edtMemo = findViewById(R.id.edtMemo)
        btnUpdate = findViewById(R.id.btnUpdate)
        imgProduct = findViewById(R.id.imgProduct)
        btnChangeImage = findViewById(R.id.btnChangeImage)

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

                    // 顯示原圖片（若有）
                    product?.image_path?.let {
                        val imgFile = File(it)
                        if (imgFile.exists()) {
                            imgProduct.setImageURI(Uri.fromFile(imgFile))
                            selectedImagePath = it
                        }
                    }

                } else {
                    txtInfo.text = "查無商品（條碼：$barcode）"
                    btnUpdate.isEnabled = false
                }
            }
        }

        // 更換圖片按鈕：選擇相簿或拍照
        btnChangeImage.setOnClickListener {
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

            // 使用協程更新庫存資料
            CoroutineScope(Dispatchers.IO).launch {
                // 更新商品（含圖片路徑）
                val updatedProduct = product!!.copy(
                    stock_quantity = newQty,
                    image_path = selectedImagePath
                )
                AppDatabase.getDatabase(applicationContext).productDao().updateProduct(updatedProduct)

                // 新增一筆盤點紀錄
                val log = StockLog(
                    product_id = product!!.id,
                    input_quantity = newQty,
                    memo = edtMemo.text.toString(),
                    updated_at = System.currentTimeMillis()
                )
                AppDatabase.getDatabase(applicationContext).stockLogDao().insertLog(log)

                launch(Dispatchers.Main) {
                    Toast.makeText(this@InventoryEditActivity, "更新成功", Toast.LENGTH_SHORT).show()

                    // 回到掃描畫面
                    startActivity(Intent(this@InventoryEditActivity, BarcodeScanActivity::class.java))
                    finish()
                }
            }
        }
    }

    // 相簿選擇圖片
    private fun selectImageFromGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, 201)
    }

    // 相機拍照
    private fun captureImageFromCamera() {
        val fileName = "camera_${System.currentTimeMillis()}.jpg"
        val imageFile = File(getExternalFilesDir(null), fileName)
        cameraImageUri = Uri.fromFile(imageFile)

        val intent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, cameraImageUri)
        startActivityForResult(intent, 202)
    }

    // 處理相簿/相機返回圖片
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            201 -> { // 相簿
                if (resultCode == RESULT_OK && data != null) {
                    val uri = data.data ?: return
                    val inputStream = contentResolver.openInputStream(uri)
                    val fileName = "gallery_${System.currentTimeMillis()}.jpg"
                    val file = File(getExternalFilesDir(null), fileName)
                    val outputStream = file.outputStream()
                    inputStream?.copyTo(outputStream)
                    inputStream?.close()
                    outputStream.close()

                    selectedImagePath = file.absolutePath
                    imgProduct.setImageURI(uri)
                }
            }

            202 -> { // 相機
                if (resultCode == RESULT_OK && cameraImageUri != null) {
                    selectedImagePath = cameraImageUri!!.path
                    imgProduct.setImageURI(cameraImageUri)
                }
            }
        }
    }
}
