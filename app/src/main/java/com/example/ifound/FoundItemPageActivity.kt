package com.example.ifound

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import android.view.View
import android.widget.Toast
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

    enum class PageMode {
        NORMAL, //kung galing ka sa home screen
        TICKET //galing sa ticket
    }
    enum class UserTypes {
        CLAIMER,
        FOUNDER
    }

    private lateinit var binding : ActivityFoundItemPageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFoundItemPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pageMode = intent.getSerializableExtra("PageMode") as PageMode
        val claimRequest = intent.getParcelableExtra("ClaimRequest") as ClaimRequestData?
        val foundItem = intent.getParcelableExtra("FoundItemData") as FoundItemData?

        val horizontalScrollView = binding.horizontalScrollView
        val currentUser = FirebaseAuth.getInstance().currentUser?.uid

        val readyForReturn = requestStatus.READY_FOR_RETURN.toString()
        readyForReturn.replace("_", " ")

        if (pageMode == PageMode.NORMAL) { //if galing sa homescreen
            horizontalScrollView.isHorizontalScrollBarEnabled = false

            horizontalScrollView.setOnTouchListener { v, event ->
                event.action == MotionEvent.ACTION_MOVE
            }


            binding.cvFoundItem2.visibility = View.GONE
            binding.tv3.visibility = View.GONE
            binding.tv4.visibility = View.INVISIBLE
            binding.tvSpecifics.visibility = View.GONE
            binding.tvLocation.visibility = View.INVISIBLE
            binding.llAcceptReject.visibility = View.GONE

            val notifReference = FirebaseDatabase.getInstance(
                "https://ifound-731c1-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Notifications")

            //get status ng claim request ng item with founderId
            val claimReference = FirebaseDatabase.getInstance("https://ifound-731c1-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Claim Requests")
            claimReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (claim in snapshot.children) {
                            val claimData = claim.getValue(ClaimRequestData::class.java)
                            if (claimData?.foundItemId == foundItem?.childUid) {
                                if (claimData?.claimer == currentUser) {
                                    if (claimData?.status == requestStatus.ACCEPTED.toString()) {
                                        binding.btnAddPhoto.visibility = View.GONE
                                        binding.btnDelete.visibility = View.GONE
                                        binding.llAcceptReject.visibility = View.VISIBLE
                                        binding.btnAccept.text = "IN PROGRESS. CHECK EMAIL"
                                        binding.btnAccept.isClickable = false
                                        binding.btnReject.visibility = View.GONE
                                    }
                                }
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@FoundItemPageActivity, error.message, Toast.LENGTH_SHORT).show()
                }

            })


        } else { //if galing sa ticket activity
            val userTypes = intent.getSerializableExtra("UserTypes") as UserTypes

            if (userTypes == UserTypes.CLAIMER) {
                horizontalScrollView.isHorizontalScrollBarEnabled = false
                horizontalScrollView.setOnTouchListener { v, event ->
                    event.action == MotionEvent.ACTION_MOVE
                }
                binding.cvFoundItem1.visibility = View.GONE
                binding.tv3.visibility = View.GONE
                binding.tv4.visibility = View.INVISIBLE
                binding.tvSpecifics.visibility = View.GONE
                binding.tvLocation.visibility = View.INVISIBLE
                binding.llAcceptReject.visibility = View.GONE
                binding.btnAddPhoto.visibility = View.GONE
                binding.btnDelete.visibility = View.VISIBLE
                binding.btnDelete.isClickable = false
                binding.btnDelete.text = "UNDER REVIEW. PLEASE WAIT"

                when (claimRequest?.status) {
                    requestStatus.ACCEPTED.toString() -> {
                            binding.llAcceptReject.visibility = View.VISIBLE
                            binding.btnDelete.visibility = View.GONE
                            binding.llAcceptReject.setPadding(80,0,80,0)
                            binding.btnReject.visibility = View.GONE
                            binding.btnAccept.text = "IN PROGRESS. CHECK EMAIL"
                            binding.btnAccept.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#06BF4A"))
                            binding.btnAccept.isClickable = false
                    } readyForReturn -> {
                        binding.btnDelete.visibility = View.GONE
                        binding.llAcceptReject.visibility = View.VISIBLE
                        binding.llAcceptReject.setPadding(80,0,80,0)
                        binding.btnReject.visibility = View.GONE
                        binding.btnAccept.text = "I HAVE RECEIVED MY ITEM"
                        binding.btnAccept.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#06BF4A"))
                        binding.btnAccept.isClickable = true

                    binding.btnAccept.setOnClickListener {


                        //notif for returned item
                        val foundItemReference = FirebaseDatabase.getInstance("https://ifound-731c1-default-rtdb.asia-southeast1.firebasedatabase.app")
                            .getReference("Found Items").child(claimRequest.foundItemId.toString())
                        val claimerNotifId = UUID.randomUUID().toString()
                        val founderNotifId = UUID.randomUUID().toString()
                        val notifTimestamp = System.currentTimeMillis().toString()
                        val image = claimRequest.foundItemImage.toString()

                        foundItemReference.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val foundItemSnap = snapshot.getValue(FoundItemData::class.java)
                                val notifMessageFounder =
                                    "You have been rewarded with stars for a successful return of ${foundItemSnap?.name}."
                                val notifMessageClaimer =
                                    "We're glad you have received your item. You have been rewarded with stars for a successful transaction!"

                                val claimerNotif = NotificationsModel(
                                    claimerNotifId,
                                    claimRequest.claimId,
                                    image,
                                    notifMessageClaimer,
                                    notifTimestamp,
                                    currentUser
                                )

                                val founderNotif = NotificationsModel(
                                    founderNotifId,
                                    claimRequest.claimId,
                                    image,
                                    notifMessageFounder,
                                    notifTimestamp,
                                    foundItemSnap?.submittedBy.toString()
                                )


                                val notifReference = FirebaseDatabase.getInstance("https://ifound-731c1-default-rtdb.asia-southeast1.firebasedatabase.app")
                                    .getReference("Notifications").child(claimerNotifId)
                                notifReference.setValue(claimerNotif)
                                notifReference.setValue(founderNotif)

                                //increment that 'Stars' of the claimer and founder
                                val claimerReference = FirebaseDatabase.getInstance("https://ifound-731c1-default-rtdb.asia-southeast1.firebasedatabase.app")
                                    .getReference("Users").child(currentUser.toString())

                                val founderReference = FirebaseDatabase.getInstance("https://ifound-731c1-default-rtdb.asia-southeast1.firebasedatabase.app")
                                    .getReference("Users").child(claimRequest.foundItemSubmitter.toString())
                                claimerReference.addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        val currentStars = snapshot.child("stars").getValue(Int::class.java) ?: 0
                                        val newStars = currentStars + 5
                                        claimerReference.child("stars").setValue(newStars)
                                            .addOnSuccessListener {
                                                //
                                            }
                                            .addOnFailureListener { error ->
                                                Toast.makeText(this@FoundItemPageActivity, "Failed to increment stars: ${error.message}", Toast.LENGTH_SHORT).show()
                                            }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        Toast.makeText(this@FoundItemPageActivity, error.message, Toast.LENGTH_SHORT).show()
                                    }


                                })

                                founderReference.addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        val currentStars = snapshot.child("stars").getValue(Int::class.java) ?: 0
                                        val newStars = currentStars + 5
                                        founderReference.child("stars").setValue(newStars)
                                            .addOnSuccessListener {
                                                //
                                            }
                                            .addOnFailureListener { error ->
                                                Toast.makeText(this@FoundItemPageActivity, "Failed to increment stars: ${error.message}", Toast.LENGTH_SHORT).show()
                                            }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        Toast.makeText(this@FoundItemPageActivity, error.message, Toast.LENGTH_SHORT).show()
                                    }


                                })




                            }
                            override fun onCancelled(error: DatabaseError) {
                                Toast.makeText(this@FoundItemPageActivity, error.message, Toast.LENGTH_SHORT).show()
                            }

                        })

                        val claimRequestReference =
                            FirebaseDatabase.getInstance("https://ifound-731c1-default-rtdb.asia-southeast1.firebasedatabase.app")
                                .getReference("Claim Requests")
                                .child(claimRequest.claimId.toString())
                        claimRequestReference.child("status").setValue(requestStatus.RETURNED.toString()).addOnSuccessListener {
                            Toast.makeText(this, "Thank you for using iFound! You have been rewarded!", Toast.LENGTH_SHORT).show()
                        finish()
                        }
                    }
                    } requestStatus.REJECTED.toString() -> {
                        binding.llAcceptReject.visibility = View.GONE
                        binding.btnAddPhoto.visibility = View.GONE
                        binding.btnDelete.visibility = View.VISIBLE
                        binding.btnDelete.isClickable = false
                        binding.btnDelete.text = "Request to claim item has been rejected by the founder."
                        binding.btnDelete.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FFD84444"))
                    } requestStatus.RETURNED.toString() -> {
                    binding.btnDelete.visibility = View.GONE
                    binding.llAcceptReject.visibility = View.VISIBLE
                    binding.llAcceptReject.setPadding(80,0,80,0)
                    binding.btnReject.visibility = View.GONE
                    binding.btnAccept.text = "You have already received this item."
                    binding.btnAccept.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#06BF4A"))
                    binding.btnAccept.isClickable = false
                    }
                }

            } else { //founder
                binding.btnAddPhoto.visibility = View.GONE
                binding.btnDelete.visibility = View.GONE

                val claimRequestReference = FirebaseDatabase.getInstance("https://ifound-731c1-default-rtdb.asia-southeast1.firebasedatabase.app")
                    .getReference("Claim Requests").child(claimRequest?.claimId.toString())
                val acceptBtn = binding.btnAccept
                val foundItemReference = FirebaseDatabase.getInstance("https://ifound-731c1-default-rtdb.asia-southeast1.firebasedatabase.app")
                    .getReference("Found Items").child(claimRequest?.foundItemId.toString())
                val claimerNotifId = UUID.randomUUID().toString()
                val founderNotifId = UUID.randomUUID().toString()

                val notifTimestamp = System.currentTimeMillis().toString()
                val image = claimRequest?.foundItemImage.toString()

                when (claimRequest?.status) {
                    requestStatus.PENDING.toString() -> { //item status == "PENDING
                        binding.llAcceptReject.visibility = View.VISIBLE

                        binding.btnAccept.setOnClickListener {
                            //notif



                            //get foundItemId's name

                            //use get() to get the value of the foundItemId's name

                            foundItemReference.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val foundItemSnap = snapshot.getValue(FoundItemData::class.java)
                                    val notifMessageClaimer =
                                        "Founder of " + foundItemSnap?.name + " has accepted your claim request. Check your email."

                                    val claimerNotif = NotificationsModel(
                                        claimerNotifId,
                                        claimRequest.claimId,
                                        image,
                                        notifMessageClaimer,
                                        notifTimestamp,
                                        claimRequest.claimer
                                    )


                                    val notifReference = FirebaseDatabase.getInstance("https://ifound-731c1-default-rtdb.asia-southeast1.firebasedatabase.app")
                                        .getReference("Notifications").child(claimerNotifId)
                                    notifReference.setValue(claimerNotif)



                                }
                                override fun onCancelled(error: DatabaseError) {
                                    Toast.makeText(this@FoundItemPageActivity, error.message, Toast.LENGTH_SHORT).show()
                                }

                            })

                            claimRequestReference.child("status").setValue(requestStatus.ACCEPTED.toString())
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Ticket is now open. Please check your email.", Toast.LENGTH_SHORT).show()
                                }
                            acceptBtn.text = "RELEASE ITEM"
                        }
                        binding.btnReject.setOnClickListener {
                            rejectBtn(claimRequest)
                        }
                    } requestStatus.ACCEPTED.toString() -> { //item status == "ACCEPTED"
                    binding.btnReject.visibility = View.VISIBLE
                    binding.btnAccept.text = "RELEASE ITEM"
                    binding.btnAccept.isClickable = true

                    binding.btnAccept.setOnClickListener {
                        //notif
                        foundItemReference.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val foundItemSnap = snapshot.getValue(FoundItemData::class.java)
                                val notifMessageClaimer =
                                    foundItemSnap?.name + " has been approved and ready for return!"

                                val claimerNotif = NotificationsModel(
                                    claimerNotifId,
                                    claimRequest.claimId,
                                    image,
                                    notifMessageClaimer,
                                    notifTimestamp,
                                    claimRequest.claimer
                                )


                                val notifReference = FirebaseDatabase.getInstance("https://ifound-731c1-default-rtdb.asia-southeast1.firebasedatabase.app")
                                    .getReference("Notifications").child(claimerNotifId)
                                notifReference.setValue(claimerNotif)



                            }
                            override fun onCancelled(error: DatabaseError) {
                                Toast.makeText(this@FoundItemPageActivity, error.message, Toast.LENGTH_SHORT).show()
                            }

                        })


                        claimRequestReference.child("status").setValue(readyForReturn)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Item ready for return", Toast.LENGTH_SHORT).show()
                            }
                        binding.btnAccept.visibility = View.GONE
                        binding.btnReject.text = "Waiting for Claimer to get the item!"
                    }
                    binding.btnReject.setOnClickListener {
                        rejectBtn(claimRequest)
                    }


                    } readyForReturn -> { //item status == "READY FOR RETURN"
                    binding.btnAccept.visibility = View.GONE
                    binding.btnReject.text = "Waiting for Claimer to get the item!"
                    binding.btnReject.isClickable = false

                    } requestStatus.REJECTED.toString() -> { //item status == "REJECTED"
                    binding.btnAccept.visibility = View.GONE
                    binding.btnReject.text = "You have rejected this claim request."
                    binding.btnReject.isClickable = false


                    } requestStatus.RETURNED.toString() -> { //item status == "RETURNED"
                    binding.btnAccept.visibility = View.GONE
                    binding.btnReject.visibility = View.GONE
                    binding.llAcceptReject.visibility = View.VISIBLE
                    binding.btnAccept.visibility = View.GONE
                    binding.btnReject.text = "Item has been returned!"
                }
                }
            }


            if (claimRequest != null) {
                val foundItemId = claimRequest.foundItemId

                binding.btnDatePicker.text = claimRequest.date
                binding.etItemLocation.setText(claimRequest.location)
                binding.etAnySpecifics.setText(claimRequest.description)

                val foundItemReference = FirebaseDatabase.getInstance("https://ifound-731c1-default-rtdb.asia-southeast1.firebasedatabase.app")
                    .getReference("Found Items").child(foundItemId!!)

                foundItemReference.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val foundItemSnap = snapshot.getValue(FoundItemData::class.java) //overshadowed kanina
                        if (foundItemSnap != null) {
                            binding.tvItemNameFoundItem.text = foundItemSnap.name
                            binding.tvDateFound.text = foundItemSnap.date
                            binding.tvItemType.text = foundItemSnap.category
                            binding.tvSpecifics.text = foundItemSnap.description
                            binding.tvLocation.text = foundItemSnap.location


                            //find the user who is claiming the found item
                            val foundItemUserReference = FirebaseDatabase.getInstance("https://ifound-731c1-default-rtdb.asia-southeast1.firebasedatabase.app")
                                .getReference("Users").child(claimRequest.claimer!!).child("Name")
                            foundItemUserReference.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val foundItemUser = snapshot.getValue(String::class.java)
                                    if (foundItemUser != null) {
                                        binding.tvName.text = foundItemUser
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    //do nothing
                                }
                            })
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        //do nothing
                    }
                })


                Glide.with(this)
                    .load(claimRequest.foundItemImage)
                    .into(binding.ivItemImg)


            }
        }
//        val foundItem = intent.getParcelableExtra("FoundItemData") as FoundItemData?

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

    private fun rejectBtn(claimRequest: ClaimRequestData?) {
        val foundItemReference = FirebaseDatabase.getInstance("https://ifound-731c1-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("Found Items").child(claimRequest?.foundItemId.toString())
        val claimerNotifId = UUID.randomUUID().toString()

        val notifTimestamp = System.currentTimeMillis().toString()
        val image = claimRequest?.foundItemImage.toString()

        foundItemReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val foundItemSnap = snapshot.getValue(FoundItemData::class.java)
                val notifMessageClaimer =
                    "Claim request for "+ foundItemSnap?.name + " has been rejected by the founder"

                val claimerNotif = NotificationsModel(
                    claimerNotifId,
                    claimRequest?.claimId,
                    image,
                    notifMessageClaimer,
                    notifTimestamp,
                    claimRequest?.claimer
                )


                val notifReference = FirebaseDatabase.getInstance("https://ifound-731c1-default-rtdb.asia-southeast1.firebasedatabase.app")
                    .getReference("Notifications").child(claimerNotifId)
                notifReference.setValue(claimerNotif)



            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@FoundItemPageActivity, error.message, Toast.LENGTH_SHORT).show()
            }

        })

        val claimRequestReference = FirebaseDatabase.getInstance("https://ifound-731c1-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Claim Requests").child(
            claimRequest?.claimId.toString())
        claimRequestReference.child("status").setValue(requestStatus.REJECTED.toString())
        finish()
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