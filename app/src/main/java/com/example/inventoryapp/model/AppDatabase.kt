package com.example.inventoryapp.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// 定義資料庫版本與包含的 Entity 類別（目前只有 Product）
@Database(entities = [Product::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    // 抽象方法：取得 DAO 實例
    abstract fun productDao(): ProductDao

    companion object {
        // 使用 volatile 確保多執行緒下資料一致性
        @Volatile private var instance: AppDatabase? = null

        // 單例模式取得資料庫實例
        fun getDatabase(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "inventory_db" // 指定資料庫檔名
                ).build().also { instance = it }
            }
    }
}
