package com.example.ifound

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.ifound.databinding.ActivityLostItemPageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.UUID

// FoundItemPage =/= FoundItemFeed

class LostItemPageActivity() : AppCompatActivity() {

    private lateinit var binding: ActivityLostItemPageBinding
    private lateinit var firebaseAuth : FirebaseAuth


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
            binding.tvItemDescription.text = lostItem.description
            binding.tvSpecifics.text = lostItem.itemType

            Glide.with(this)
                .load(lostItem.image)
                .into(binding.ivItemImg)

            // Checks if the current user is the same user who submitted the current item OR if the user is an admin
            val isItemSubmittedByCurrentUser = isItemSubmittedByCurrentUser(lostItem)
            val isUserAnAdmin = isUserAnAdmin()
            if (isItemSubmittedByCurrentUser || isUserAnAdmin) {
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
            showDeleteDialog(lostItem!!)
        }

        binding.btnIFoundThisItem.setOnClickListener {

            firebaseAuth = FirebaseAuth.getInstance()
            val currentUser = firebaseAuth.currentUser?.uid
            //insert data into notification database
            val notificationRef = FirebaseDatabase.getInstance("https://ifound-731c1-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Notifications")
            val notificationId = UUID.randomUUID().toString()

            val timestamp = System.currentTimeMillis().toString()
            //get name of current user
            val usersDatabaseReference = FirebaseDatabase.getInstance("https://ifound-731c1-default-rtdb.asia-southeast1.firebasedatabase.app/").reference
            val usernameReference = usersDatabaseReference.child("Users").child(currentUser!!).child("Name")
            usernameReference.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userName = snapshot.getValue(String::class.java)
                    val message = "${userName} has found your item. Please check your email."
                    // Adds current LOG to the Logs Database
                    val notification = NotificationsModel(
                        notificationId,
                        lostItem?.childUid,
                        lostItem?.image,
                        message,
                        timestamp,
                        lostItem?.submittedBy
                    )
                    notificationRef.child(notificationId).setValue(notification).addOnSuccessListener {
                        Log.d("TAG", "Notification sent")
                     Toast.makeText(this@LostItemPageActivity, "Notification sent", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener {
                        Log.d("TAG", "Notification failed")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("TAG", "Failed to get username")
                }
            })
        }
    }



    private fun showDeleteDialog(lostItem : LostItemData) {
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.delete_dialog, null)
        dialogBuilder.setView(dialogView)

        val alertDialog = dialogBuilder.create()
        alertDialog.setCancelable(false)

        val deleteButton = dialogView.findViewById<Button>(R.id.dialog_delete)
        val cancelButton = dialogView.findViewById<Button>(R.id.dialog_cancel)

        deleteButton.setOnClickListener {
            // Perform delete operation here
            submitDeleteLostItemLog()
            archiveLostItem(lostItem!!)
            deleteItem(lostItem)
            finish()
            alertDialog.dismiss()
        }

        cancelButton.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    @SuppressLint("SimpleDateFormat")
    private fun submitDeleteLostItemLog() {
        val firebaseAuth = FirebaseAuth.getInstance()
        val currentUserId = firebaseAuth.currentUser?.uid

        // Database to WRITE to
        val logDatabaseReference = FirebaseDatabase.getInstance("https://ifound-731c1-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Logs")

        // Information to be stored as LOG
        var userName : String?
        val childUid = UUID.randomUUID().toString()
        val message = " deleted a Lost Item "

        // Gets the current timestamp/date
        val dateFormat = SimpleDateFormat("yy-MM-dd HH:mm:ss")
        val currentDateTime : String = dateFormat.format(Date())

        // Gets the name of the current user
        val usersDatabaseReference = FirebaseDatabase.getInstance("https://ifound-731c1-default-rtdb.asia-southeast1.firebasedatabase.app/").reference
        val usernameReference = usersDatabaseReference.child("Users").child(currentUserId!!).child("Name")

        usernameReference.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                userName = snapshot.getValue(String::class.java)
                // Adds current LOG to the Logs Database
                val log = "LOG: $userName $message | $currentDateTime"
                logDatabaseReference.child(childUid).child("Log Message").setValue(log)
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun isUserAnAdmin() : Boolean {
        val firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser

        if (currentUser?.email == "202101382@iacademy.edu.ph") {
            return true
        }
        return false
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
    }

    private fun archiveLostItem(lostItem : LostItemData) {
        val database = FirebaseDatabase.getInstance("https://ifound-731c1-default-rtdb.asia-southeast1.firebasedatabase.app/")
                 .getReference("Lost Items - Archive")

        database.child(lostItem.childUid.toString()).setValue(lostItem)
    }
}