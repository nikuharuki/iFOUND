package com.example.ifound

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ifound.databinding.EachItemBinding

class LostItemAdapter(private val context: Context, private val lostItemList : ArrayList<LostItemData>):
    RecyclerView.Adapter<LostItemAdapter.LostItemViewHolder>() {

    var onItemClick: ((LostItemData) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LostItemAdapter.LostItemViewHolder {
        val binding = EachItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LostItemViewHolder(binding, context)
    }

    override fun onBindViewHolder(holder: LostItemAdapter.LostItemViewHolder, position: Int) {
        lostItemList[position].let {
            holder.bind(it, position)
        }
    }

    override fun getItemCount(): Int {
        return lostItemList.size
    }

    class LostItemViewHolder(private val binding: EachItemBinding, private val context: Context):
        RecyclerView.ViewHolder(binding.root) {

        fun bind(lostItem: LostItemData, position: Int){
//            binding.profileIv.setImageDrawable(context.getDrawable(R.drawable.baseline_account_circle_24))
            binding.titleTv.text = lostItem.name
            binding.tags.text = lostItem.description
        }
    }
}