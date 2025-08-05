package com.example.inventoryapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "stock_logs")
data class StockLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val product_id: Int,            // 對應 product.id
    val input_quantity: Int,        // 輸入的新庫存數量
    val memo: String?,              // 備註（可為 null）
    val updated_at: Long            // 更新時間（timestamp）
)
