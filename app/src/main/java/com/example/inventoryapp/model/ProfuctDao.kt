package com.example.inventoryapp.model

import androidx.room.*

@Dao // 標記為 Room DAO（Data Access Object）介面
interface ProductDao {

    // 根據條碼查找單一商品，最多回傳一筆
    @Query("SELECT * FROM products WHERE barcode = :barcode LIMIT 1")
    suspend fun getProductByBarcode(barcode: String): Product?

    // 模糊查詢：從商品名稱中尋找包含關鍵字的所有商品
    @Query("SELECT * FROM products WHERE name LIKE '%' || :keyword || '%'")
    suspend fun searchProducts(keyword: String): List<Product>

    // 新增商品，如果條碼已存在則覆蓋
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product)

    // 更新商品資料（根據主鍵 id）
    @Update
    suspend fun updateProduct(product: Product)

    // 刪除商品資料
    @Delete
    suspend fun deleteProduct(product: Product)

    // 查詢所有商品資料
    @Query("SELECT * FROM products")
    suspend fun getAllProducts(): List<Product>
}
