package com.example.ifound

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ifound.databinding.EachItemBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class TicketAdapter(private val context: Context, private val claimList: ArrayList<ClaimRequestData>):
    RecyclerView.Adapter<TicketAdapter.TicketViewHolder>() {

    var onItemClick: ((ClaimRequestData) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketAdapter.TicketViewHolder {
        val binding = EachItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TicketViewHolder(binding, context)
    }

    override fun onBindViewHolder(holder: TicketAdapter.TicketViewHolder, position: Int) {
        val ticket = claimList[position]
        holder.bind(ticket, position)

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(ticket)
        }
    }

    override fun getItemCount(): Int {
        return claimList.size
    }

    class TicketViewHolder(private val binding: EachItemBinding, private val context: Context):
        RecyclerView.ViewHolder(binding.root) {
        fun bind(ticket: ClaimRequestData, position: Int){



            val foundItemId = ticket.foundItemId.toString()
            val foundItemRef = FirebaseDatabase.getInstance("https://ifound-731c1-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Found Items").child(foundItemId)

            foundItemRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val foundItem = dataSnapshot.getValue(FoundItemData::class.java)
                        val itemName = foundItem?.name

                        binding.titleTv.text = itemName
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle error
                }
            })

            binding.tags.text = ticket.status

            Glide.with(itemView.context)
                .load(ticket.foundItemImage)
                .into(binding.imageView)
        }
    }
}