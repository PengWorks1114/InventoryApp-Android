package com.example.inventoryapp.model

import androidx.room.*

@Dao
interface StockLogDao {

    @Insert
    suspend fun insertLog(log: StockLog)

    @Query("SELECT * FROM stock_logs ORDER BY updated_at DESC")
    suspend fun getAllLogs(): List<StockLog>

    @Query("SELECT * FROM stock_logs WHERE product_id = :productId ORDER BY updated_at DESC")
    suspend fun getLogsByProduct(productId: Int): List<StockLog>
}
