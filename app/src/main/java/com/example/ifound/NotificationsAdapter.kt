package com.example.ifound

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ifound.databinding.ActivityNotificationsBinding
import com.example.ifound.databinding.EachClaimRequestBinding
import com.example.ifound.databinding.EachItemBinding

class NotificationsAdapter(private val context: Context, private val notifList : ArrayList<NotificationsModel>):
    RecyclerView.Adapter<NotificationsAdapter.NotificationsViewHolder>()  {
    var onItemClick: ((NotificationsModel) -> Unit)? = null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NotificationsAdapter.NotificationsViewHolder {
        val binding = EachClaimRequestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotificationsAdapter.NotificationsViewHolder(binding, context)
    }

    override fun onBindViewHolder(
        holder: NotificationsAdapter.NotificationsViewHolder,
        position: Int
    ) {
        val notif = notifList[position]
        holder.bind(notif, position)


        holder.itemView.setOnClickListener {
            onItemClick?.invoke(notif)
        }
    }

    override fun getItemCount(): Int {
        return notifList.size
    }

    class NotificationsViewHolder(private val binding: EachClaimRequestBinding, private val context: Context):
        RecyclerView.ViewHolder(binding.root) {
        fun bind(notif: NotificationsModel, position: Int){
//
            binding.tvMessage.text = notif.message
            binding.tvTimestamp.text = notif.timestamp

            Glide.with(itemView.context)
                .load(notif.image)
                .into(binding.imageView)
        }
    }
}