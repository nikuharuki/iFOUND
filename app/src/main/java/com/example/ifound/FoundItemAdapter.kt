package com.example.ifound

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ifound.databinding.EachItemBinding

class FoundItemAdapter(private val context: Context, private var foundItemList : ArrayList<FoundItemData>):
    RecyclerView.Adapter<FoundItemAdapter.FoundItemViewHolder>() {

    var onItemClick: ((FoundItemData) -> Unit)? = null

    fun setFilteredList(foundItemList: ArrayList<FoundItemData>){
        this.foundItemList = foundItemList
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoundItemAdapter.FoundItemViewHolder {
        val binding = EachItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoundItemViewHolder(binding, context)
    }

    override fun onBindViewHolder(holder: FoundItemAdapter.FoundItemViewHolder, position: Int) {
        val foundItem = foundItemList[position]
        holder.bind(foundItem, position)

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(foundItem)
        }
    }

    override fun getItemCount(): Int {
        return foundItemList.size
    }

    class FoundItemViewHolder(private val binding: EachItemBinding, private val context: Context):
        RecyclerView.ViewHolder(binding.root) {
        fun bind(foundItem: FoundItemData, position: Int){
//
            binding.titleTv.text = foundItem.name
            binding.tags.text = foundItem.category

            Glide.with(itemView.context)
                .load(foundItem.image)
                .into(binding.imageView)
        }
    }
}