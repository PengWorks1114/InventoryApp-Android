package com.example.inventoryapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// 將這個資料類別標記為 Room 資料表，表名為 "products"
@Entity(tableName = "products")
data class Product(
    // 主鍵欄位，並設定為自動遞增
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    // 商品條碼（字串型態）
    val barcode: String,

    // 商品名稱
    val name: String,

    // 商品存放位置（如：貨架A1）
    val location: String,

    // 單位（如：個、箱、袋）
    val unit: String,

    // 原始庫存數量
    val stock_quantity: Int,

    // 商品圖片路徑（選填，可以為 null）
    val image_path: String? = null
)
