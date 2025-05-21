package com.example.imagegenerator

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.imagegenerator.databinding.ItemImageBinding

class ImageAdapter(private var imageUrls: MutableList<String>) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUrl = imageUrls[position]
        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .into(holder.binding.imageView)
    }

    override fun getItemCount(): Int {
        return imageUrls.size
    }

    // Yeni resimler geldiğinde adapter'ı güncelleyen metot
    fun updateImages(newImageUrls: List<String>) {
        imageUrls.clear() // Eski verileri temizle
        imageUrls.addAll(newImageUrls) // Yeni verileri ekle
        notifyDataSetChanged() // RecyclerView'i güncelle
    }

    class ImageViewHolder(val binding: ItemImageBinding) : RecyclerView.ViewHolder(binding.root)
}
