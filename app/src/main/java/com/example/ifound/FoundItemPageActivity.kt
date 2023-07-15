package com.example.ifound

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import android.view.View
import com.bumptech.glide.Glide
import com.example.ifound.databinding.ActivityFoundItemPageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.UUID

class FoundItemPageActivity : AppCompatActivity() {

    private lateinit var binding : ActivityFoundItemPageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFoundItemPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val foundItem = intent.getParcelableExtra("FoundItemData") as FoundItemData?

        if (foundItem != null) {
            binding.tvItemNameFoundItem.text = foundItem.name
            binding.tvDateFound.text = foundItem.date
            binding.tvItemType.text = foundItem.category

            isThisItemBeingClaimedByUser(foundItem)

            Glide.with(this)
                .load(foundItem.image)
                .into(binding.ivItemImg)

            val isItemSubmittedByCurrentUser = isItemSubmittedByCurrentUser(foundItem)
            val isUserAnAdmin = isUserAnAdmin()
            if (isItemSubmittedByCurrentUser || isUserAnAdmin) {
                binding.btnEdit.visibility = View.VISIBLE
                binding.btnDelete.visibility = View.VISIBLE
            } else {
                binding.btnEdit.visibility = View.GONE
                binding.btnDelete.visibility = View.GONE
            }
        }

        binding.btnEdit.setOnClickListener {
            val intent = Intent(this, FoundItemFormActivity::class.java)
            intent.putExtra("FoundItemData", foundItem)
            intent.putExtra("PageMode", FoundItemFormActivity.PageMode.EDIT)
            startActivity(intent)
        }

        binding.btnDelete.setOnClickListener {
            showDeleteDialog(foundItem!!)
        }

        binding.btnAddPhoto.setOnClickListener {
        ClaimRequestFragment(this, foundItem).show(supportFragmentManager, "ClaimingVerification")
        }
    }

    private fun isThisItemBeingClaimedByUser(foundItem : FoundItemData) {
        val firebaseAuth = FirebaseAuth.getInstance()
        val currentUserId = firebaseAuth.currentUser?.uid
        val currentItemId = foundItem.childUid

        val claimRequestDatabaseReference = FirebaseDatabase.getInstance("https://ifound-731c1-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Claim Requests")
        claimRequestDatabaseReference.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val claimExists = snapshot.children.any { claimSnapshot ->
                    val claimerId = claimSnapshot.child("claimer").getValue(String::class.java)
                    val itemId = claimSnapshot.child("foundItemId").getValue(String::class.java)

                    claimerId == currentUserId && itemId == currentItemId
                }

                if (claimExists) {
                    binding.btnAddPhoto.text = "For Approval"
                    binding.btnAddPhoto.isClickable = false
                } else {
                    binding.btnAddPhoto.isClickable = true
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })


        binding.btnDelete.setOnClickListener {
            showDeleteDialog(foundItem)
        }
    }

    private fun showDeleteDialog(foundItem : FoundItemData) {
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.delete_dialog, null)
        dialogBuilder.setView(dialogView)

        val alertDialog = dialogBuilder.create()
        alertDialog.setCancelable(false)

        val deleteButton = dialogView.findViewById<Button>(R.id.dialog_delete)
        val cancelButton = dialogView.findViewById<Button>(R.id.dialog_cancel)

        deleteButton.setOnClickListener {
            submitDeleteFoundItemLog()
            archiveFoundItem(foundItem!!)
            deleteItem(foundItem)
            finish()
            alertDialog.dismiss()
        }

        cancelButton.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    @SuppressLint("SimpleDateFormat")
    private fun submitDeleteFoundItemLog() {
        val firebaseAuth = FirebaseAuth.getInstance()
        val currentUserId = firebaseAuth.currentUser?.uid

        // Database to WRITE to
        val logDatabaseReference = FirebaseDatabase.getInstance("https://ifound-731c1-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Logs")

        // Information to be stored as LOG
        var userName : String?
        val childUid = UUID.randomUUID().toString()
        val message = " deleted a Found Item "

        // Gets the current timestamp/date
        val dateFormat = SimpleDateFormat("yy-MM-dd HH:mm:ss")
        val currentDateTime : String = dateFormat.format(Date())

        // Gets the name of the current user
        val usersDatabaseReference = FirebaseDatabase.getInstance("https://ifound-731c1-default-rtdb.asia-southeast1.firebasedatabase.app/").reference
        val usernameReference = usersDatabaseReference.child("Users").child(currentUserId!!).child("Name")

        usernameReference.addListenerForSingleValueEvent(object : ValueEventListener {
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

    private fun archiveFoundItem(foundItem : FoundItemData) {
        val database = FirebaseDatabase.getInstance("https://ifound-731c1-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("Found Items - Archive")

        database.child(foundItem.childUid.toString()).setValue(foundItem)
    }

    private fun deleteItem(foundItem: FoundItemData) {
        val foundItemId = foundItem.childUid.toString()

        val database = FirebaseDatabase.getInstance("https://ifound-731c1-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("Found item")
        val item = database.child(foundItemId)

        item.removeValue()
    }

    private fun isUserAnAdmin() : Boolean {
        val firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser

        if (currentUser?.email == "202101382@iacademy.edu.ph") {
            return true
        }
        return false
    }

    private fun isItemSubmittedByCurrentUser(foundItem : FoundItemData) : Boolean {
        val firebaseAuth = FirebaseAuth.getInstance()

        val submitterUid = foundItem.submittedBy.toString()
        val currentUserUid = firebaseAuth.currentUser?.uid

        if (submitterUid == currentUserUid) {
            return true
        }

        return false
    }
}