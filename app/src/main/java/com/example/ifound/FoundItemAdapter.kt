package com.example.ifound

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ifound.databinding.EachItemBinding

class FoundItemAdapter(private val context: Context, private val foundItemList : ArrayList<FoundItemData>):
    RecyclerView.Adapter<FoundItemAdapter.FoundItemViewHolder>() {

    var onItemClick: ((FoundItemData) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoundItemAdapter.FoundItemViewHolder {
        val binding = EachItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoundItemViewHolder(binding, context)
    }

    override fun onBindViewHolder(holder: FoundItemAdapter.FoundItemViewHolder, position: Int) {
        foundItemList[position].let {
            holder.bind(it, position)
        }
    }

    override fun getItemCount(): Int {
        return foundItemList.size
    }

    class FoundItemViewHolder(private val binding: EachItemBinding, private val context: Context):
        RecyclerView.ViewHolder(binding.root) {
        fun bind(foundItem: FoundItemData, position: Int){
//
            binding.titleTv.text = foundItem.item
            binding.tags.text = foundItem.addInfo

            val bytes = android.util.Base64.decode(foundItem.image, android.util.Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            binding.imageView.setImageBitmap(bitmap)
        }
    }
}