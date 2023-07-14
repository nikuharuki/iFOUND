package com.example.ifound

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ifound.databinding.EachClaimRequestBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

private lateinit var foundItemRef: DatabaseReference

class ClaimRequestAdapter(private val context: Context, private val claimRequestList :
ArrayList<ClaimRequestData>):
    RecyclerView.Adapter<ClaimRequestAdapter.ClaimRequestViewHolder>() {

    var onItemClick: ((ClaimRequestData) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            ClaimRequestAdapter.ClaimRequestViewHolder {
        val binding = EachClaimRequestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ClaimRequestViewHolder(binding, context)
    }

    override fun onBindViewHolder(holder: ClaimRequestAdapter.ClaimRequestViewHolder, position: Int
    ) {
        val claimRequest = claimRequestList[position]
        holder.bind(claimRequest, position)

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(claimRequest)
        }
    }

    override fun getItemCount(): Int {
        return claimRequestList.size
    }

    class ClaimRequestViewHolder(
        private val binding: EachClaimRequestBinding,
        private val context: Context
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(claim: ClaimRequestData, position: Int){

            //database reference

//
            foundItemRef = FirebaseDatabase.getInstance(
                "https://ifound-731c1-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Found Items")

            val childId = claim.foundItemId

            if (childId != null) {
                Log.d("ClaimRequestAdapter", "childId is not null: $childId")
                foundItemRef.child(childId).addListenerForSingleValueEvent(object :
                    ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val foundItem = dataSnapshot.getValue(FoundItemData::class.java) //umm
                        val itemName = foundItem?.name
                        Log.d("ClaimRequestAdapter", "itemName: $itemName")
                        Log.d("ClaimRequestAdapter", "claim.claimer: ${claim.claimer}")

                        val message = "${claim.claimer} wants to claim $itemName"
                        binding.tvMessage.text = message
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Handle error
                    }
                })
            }

            Glide.with(itemView.context)
                .load(claim.foundItemImage)
                .into(binding.imageView)
        }

    }
}