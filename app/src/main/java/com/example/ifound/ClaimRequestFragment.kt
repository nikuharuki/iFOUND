package com.example.ifound

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.ifound.databinding.FragmentClaimRequestBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.UUID

class ClaimRequestFragment(private val context: Context,
                           private val foundItemData: FoundItemData?)
    : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentClaimRequestBinding
    private lateinit var database: DatabaseReference
    private lateinit var notifReference: DatabaseReference
    private lateinit var usernameReference: DatabaseReference

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.btnSubmit.setOnClickListener {
            val claimId = UUID.randomUUID().toString() //child uid ng item sa database na claim requests
            val claimer = FirebaseAuth.getInstance().currentUser?.uid //claimer
            val where = binding.etItemLocation.text.toString()
            val date = binding.btnDatePicker.text.toString()
            val description = binding.etAnySpecifics.text.toString()
            val timestamp = System.currentTimeMillis().toString()
            val pendingStatus = requestStatus.PENDING.toString()

            val foundItemId = foundItemData?.childUid.toString() //child uid ng item sa database na found items
            val foundItemSubmitter = foundItemData?.submittedBy.toString()
            val foundItemImage = foundItemData?.image.toString()

            database = FirebaseDatabase.getInstance(
                "https://ifound-731c1-default-rtdb.asia-southeast1.firebasedatabase.app/")
                    .getReference("Claim Requests")


            val claimRequest = ClaimRequestData(
                claimId,
                claimer,
                where,
                date,
                description,
                foundItemId,
                foundItemSubmitter,
                foundItemImage,
                timestamp,
                pendingStatus
            )

            database.child(claimId).setValue(claimRequest).addOnSuccessListener {
                binding.etItemLocation.text.clear()
                binding.btnDatePicker.setText("")
                binding.etAnySpecifics.text.clear()
                dismiss()
                Toast.makeText(context, "Claim request submitted", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(context, "Failed to submit claim request", Toast.LENGTH_SHORT).show()
            }

            //notification
            notifReference = FirebaseDatabase.getInstance(
                "https://ifound-731c1-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Notifications")
            val claimerNotifId = UUID.randomUUID().toString()
            val founderNotifId = UUID.randomUUID().toString()

            val notifTimestamp = System.currentTimeMillis().toString()
            val image= foundItemData?.image.toString()
//            val founderUsername = foundItemData?.submittedBy.toString()



            //messages

            val notifMessageClaimer =
                "Claim request sent to the founder of " + foundItemData?.name + ". Wait for their response."

            val currentUser = FirebaseAuth.getInstance().currentUser?.uid.toString()


            val notifClaimer = NotificationsModel(
                claimerNotifId,
                claimId,
                image,
                notifMessageClaimer,
                notifTimestamp,
                currentUser)

            //notifFounder
            getUsername { username ->
                val notifMessageFounder = "$username has requested to claim ${foundItemData?.name}. Check your claim requests."
                val notifFounder = NotificationsModel(
                    founderNotifId,
                    claimId,
                    image,
                    notifMessageFounder,
                    notifTimestamp,
                    foundItemSubmitter)

                //for the founder
                notifReference.child(founderNotifId).setValue(notifFounder).addOnSuccessListener {
                    Log.d("notif", "notif sent to founder")
                }.addOnFailureListener {
                    Toast.makeText(context, "Failed to send notification", Toast.LENGTH_SHORT).show()
                }
            }



            //for the claimer
            notifReference.child(claimerNotifId).setValue(notifClaimer).addOnSuccessListener {
                Log.d("notif", "notif sent to claimer")
            }.addOnFailureListener {
                Toast.makeText(context, "Failed to send notification", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun getUsername(callback: (String) -> Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser?.uid
        usernameReference = FirebaseDatabase
            .getInstance("https://ifound-731c1-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .reference.child("Users")
            .child(currentUser!!)
            .child("Name")

        usernameReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val username = snapshot.getValue(String::class.java).toString()
                    callback(username)
                } else {
                    callback("")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error case if needed
                callback("")
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState:
    Bundle?): View {
        binding = FragmentClaimRequestBinding.inflate(inflater, container, false)
        return binding.root
    }
}