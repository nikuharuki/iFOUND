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

class LostItemAdapter(private val context: Context, private var lostItemList : ArrayList<LostItemData>):
    RecyclerView.Adapter<LostItemAdapter.LostItemViewHolder>() {

    var onItemClick: ((LostItemData) -> Unit)? = null


    fun setFilteredList(lostItemList: ArrayList<LostItemData>){
        this.lostItemList = lostItemList

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LostItemAdapter.LostItemViewHolder {
        val binding = EachItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LostItemViewHolder(binding, context)
    }

    override fun onBindViewHolder(holder: LostItemAdapter.LostItemViewHolder, position: Int) {
        val lostItem = lostItemList[position]
        holder.bind(lostItem, position)


        holder.itemView.setOnClickListener {
            onItemClick?.invoke(lostItem)
        }
    }

    override fun getItemCount(): Int {
        return lostItemList.size
    }

    class LostItemViewHolder(private val binding: EachItemBinding, private val context: Context):
        RecyclerView.ViewHolder(binding.root) {
        fun bind(lostItem: LostItemData, position: Int){
//
            binding.titleTv.text = lostItem.name
            binding.tags.text = lostItem.description

            Glide.with(itemView.context)
                .load(lostItem.image)
                .into(binding.imageView)
        }
    }
}