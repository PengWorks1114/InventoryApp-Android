package com.example.inventoryapp.util

import android.content.Context
import android.os.Environment
import android.widget.Toast
import com.example.inventoryapp.model.Product
import com.opencsv.CSVWriter
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

object CsvExportUtil {

    // 實作匯出 CSV 功能
    fun exportProductsToCSV(context: Context, products: List<Product>) {
        // 建立輸出資料夾
        val exportDir = File(context.getExternalFilesDir(null), "exports")
        if (!exportDir.exists()) {
            exportDir.mkdirs() // 若資料夾不存在就建立
        }

        // 建立檔案名稱，使用時間戳記避免覆蓋
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val file = File(exportDir, "inventory_$timestamp.csv")

        try {
            val writer = CSVWriter(FileWriter(file))

            // 寫入標題列
            writer.writeNext(arrayOf("商品名稱", "條碼", "位置", "單位", "庫存"))

            // 寫入每筆商品資料
            for (product in products) {
                writer.writeNext(
                    arrayOf(
                        product.name,
                        product.barcode,
                        product.location,
                        product.unit,
                        product.stock_quantity.toString()
                    )
                )
            }

            writer.close()

            Toast.makeText(context, "匯出成功：${file.absolutePath}", Toast.LENGTH_LONG).show()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "匯出失敗：${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
