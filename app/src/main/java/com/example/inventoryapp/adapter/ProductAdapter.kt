package com.example.inventoryapp.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.inventoryapp.InventoryEditActivity
import com.example.inventoryapp.R
import com.example.inventoryapp.model.Product
import java.io.File

class ProductAdapter(
    private val context: Context,
    private val productList: List<Product>
) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    // ViewHolder 用來綁定單一 item 的畫面元件
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtName: TextView = view.findViewById(R.id.txtItemName)
        val txtBarcode: TextView = view.findViewById(R.id.txtItemBarcode)
        val imgThumb: ImageView = view.findViewById(R.id.imgItemThumb)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = productList[position]
        holder.txtName.text = product.name
        holder.txtBarcode.text = product.barcode

        // 顯示圖片縮圖（若存在）
        val imagePath = product.image_path
        if (!imagePath.isNullOrEmpty()) {
            val file = File(imagePath)
            if (file.exists()) {
                holder.imgThumb.setImageURI(Uri.fromFile(file))
            } else {
                holder.imgThumb.setImageResource(R.drawable.ic_no_image) // 預設圖片
            }
        } else {
            holder.imgThumb.setImageResource(R.drawable.ic_no_image)
        }

        // 點擊項目導向 InventoryEditActivity
        holder.itemView.setOnClickListener {
            val intent = Intent(context, InventoryEditActivity::class.java)
            intent.putExtra("barcode", product.barcode)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = productList.size
}
