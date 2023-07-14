package com.example.ifound

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.example.ifound.databinding.ActivityLostItemPageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

// FoundItemPage =/= FoundItemFeed

class LostItemPageActivity() : AppCompatActivity() {

    private lateinit var binding: ActivityLostItemPageBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLostItemPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val lostItem = intent.getParcelableExtra("LostItemData") as LostItemData?

        if (lostItem != null) {
            binding.tvItemNameLostItem.text = lostItem.name
            binding.tvLocation.text = lostItem.location
            binding.tvLastSeen.text = lostItem.date
            binding.tvLostBy.text = lostItem.submittedBy
            binding.tvEmailContactInfo.text = lostItem.contact
            binding.tvSpecifics.text = lostItem.description

            Glide.with(this)
                .load(lostItem.image)
                .into(binding.ivItemImg)

            // Checks if the current user is the same user who submitted the current item
            val isItemSubmittedByCurrentUser = isItemSubmittedByCurrentUser(lostItem)
            if (isItemSubmittedByCurrentUser) {
                binding.btnEditButtonLostItem.visibility = View.VISIBLE
                binding.btnDelete.visibility = View.VISIBLE
            } else {
                binding.btnEditButtonLostItem.visibility = View.GONE
                binding.btnDelete.visibility = View.GONE
            }
        }


        binding.btnEditButtonLostItem.setOnClickListener {
            val intent = Intent(this, LostItemFormActivity::class.java)
            intent.putExtra("LostItemData", lostItem)
            intent.putExtra("PageMode", LostItemFormActivity.PageMode.EDIT)
            startActivity(intent)
        }

        binding.btnDelete.setOnClickListener {
            deleteItem(lostItem!!)
            finish()
        }
    }

    private fun isItemSubmittedByCurrentUser(lostItem : LostItemData) : Boolean {
        val firebaseAuth = FirebaseAuth.getInstance()

        val submitterUid = lostItem.submittedBy.toString()
        val currentUserUid = firebaseAuth.currentUser?.uid

        if (submitterUid == currentUserUid) {
            return true
        }

        return false
    }

    private fun deleteItem(lostItem : LostItemData) {
        val lostItemId = lostItem.childUid.toString()

        val databaseReference = FirebaseDatabase.getInstance("https://ifound-731c1-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Lost Items")
        val itemReference = databaseReference.child(lostItemId)

        itemReference.removeValue()
            .addOnSuccessListener {
                Log.d("Delete Data", "Data deleted successfully")
            }

        finish()
    }
}