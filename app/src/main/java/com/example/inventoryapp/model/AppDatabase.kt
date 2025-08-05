package com.example.inventoryapp.model

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
// 定義資料庫版本與包含的 Entity 類別（目前只有 Product）
@Database(entities = [Product::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    // 抽象方法：取得 DAO 實例
    abstract fun productDao(): ProductDao
    abstract fun stockLogDao(): StockLogDao

    companion object {
        // 使用 volatile 確保多執行緒下資料一致性
        @Volatile private var instance: AppDatabase? = null

        // 單例模式取得資料庫實例
        fun getDatabase(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "inventory_db"// 指定資料庫檔名
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)

                            // 使用協程插入測試資料
                            CoroutineScope(Dispatchers.IO).launch {
                                val dao = getDatabase(context).productDao()

                                dao.insertProduct(
                                    Product(
                                        barcode = "1234567890123",
                                        name = "礦泉水",
                                        location = "飲料區",
                                        unit = "瓶",
                                        stock_quantity = 100,
                                        image_path = null
                                    )
                                )

                                dao.insertProduct(
                                    Product(
                                        barcode = "9876543210987",
                                        name = "衛生紙",
                                        location = "家庭用品區",
                                        unit = "包",
                                        stock_quantity = 50,
                                        image_path = null
                                    )
                                )
                            }
                        }
                    })
                    .build()
                    .also { instance = it }
            }
    }
}
