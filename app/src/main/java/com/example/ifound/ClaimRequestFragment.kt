package com.example.ifound

import android.R
import android.app.DatePickerDialog
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
import java.util.Calendar
import java.util.UUID

class ClaimRequestFragment(private val context: Context,
                           private val foundItemData: FoundItemData?)
    : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentClaimRequestBinding
    private lateinit var database: DatabaseReference
    private lateinit var datePickerDialog: DatePickerDialog


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.btnSubmit.setOnClickListener {
            val claimId = UUID.randomUUID().toString() //child uid ng item sa database na claim requests
            val claimer = FirebaseAuth.getInstance().currentUser?.uid //claimer
            val where = binding.etItemLocation.text.toString()
            val date = binding.btnDatePicker.text.toString()
            val description = binding.etAnySpecifics.text.toString()
            val timestamp = System.currentTimeMillis().toString()

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
                timestamp
            )

            database.child(claimId).setValue(claimRequest).addOnSuccessListener {
                binding.etItemLocation.text.clear()
                binding.btnDatePicker.setText("")
                binding.etAnySpecifics.text.clear()
                dismiss()
            }.addOnFailureListener {
                Toast.makeText(context, "Failed to submit claim request", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnDatePicker.setOnClickListener {
            initDatePicker()
            binding.btnDatePicker.text = getTodaysDate()
            datePickerDialog.show()
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState:
    Bundle?): View {
        binding = FragmentClaimRequestBinding.inflate(inflater, container, false)
        return binding.root
    }
    private fun getTodaysDate(): String {
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH) + 1
        val day = cal.get(Calendar.DAY_OF_MONTH)
        return makeDateString(day, month, year)
    }

    private fun initDatePicker() {
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                val formattedDate = makeDateString(dayOfMonth, monthOfYear + 1, year)
                binding.btnDatePicker.text = formattedDate
            }

        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)

        val style = R.style.Theme_Holo_Light_Dialog

        datePickerDialog = DatePickerDialog(requireActivity(), style, dateSetListener, year, month, day)
    }

    private fun makeDateString(day: Int, month: Int, year: Int): String {
        return getMonthFormat(month) + " " + day + " " + year
    }

    private fun getMonthFormat(month: Int): String {
        return when (month) {
            1 -> "JAN"
            2 -> "FEB"
            3 -> "MAR"
            4 -> "APR"
            5 -> "MAY"
            6 -> "JUN"
            7 -> "JUL"
            8 -> "AUG"
            9 -> "SEP"
            10 -> "OCT"
            11 -> "NOV"
            12 -> "DEC"
            else -> "JAN"
        }
    }

    fun openDatePicker(view: View) {
        datePickerDialog.show()
    }
}