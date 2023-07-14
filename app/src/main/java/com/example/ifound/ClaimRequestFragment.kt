package com.example.ifound

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.ifound.databinding.FragmentClaimRequestBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.UUID

class ClaimRequestFragment(private val context: Context,
                           private val foundItemData: FoundItemData?)
    : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentClaimRequestBinding
    private lateinit var database: DatabaseReference

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.btnSubmit.setOnClickListener {
            val claimId = UUID.randomUUID().toString() //child uid ng item sa database na claim requests
            val claimer = FirebaseAuth.getInstance().currentUser?.uid //claimer
            val where = binding.etItemLocation.text.toString()
            val date = binding.btnDatePicker.text.toString()
            val description = binding.etAnySpecifics.text.toString()

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
                foundItemImage)

            database.child(claimId).setValue(claimRequest).addOnSuccessListener {
                binding.etItemLocation.text.clear()
                binding.btnDatePicker.setText("")
                binding.etAnySpecifics.text.clear()
                dismiss()
            }.addOnFailureListener {
                Toast.makeText(context, "Failed to submit claim request", Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState:
    Bundle?): View {
        binding = FragmentClaimRequestBinding.inflate(inflater, container, false)
        return binding.root
    }
}