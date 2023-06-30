package com.example.ifound

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.ifound.databinding.ActivityFoundItemFormBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class FoundItemFormActivity : AppCompatActivity() {

    private lateinit var binding : ActivityFoundItemFormBinding
    private lateinit var databaseReference:  DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFoundItemFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSubmit.setOnClickListener {
            val item = binding.etWhatHaveYouFoundFoundItemForm.text.toString()
            val addInfo = binding.etAnyAdditionalInfoFoundItemForm.toString()
            val whereFound = binding.etILostMyLostForm.toString()
            val whatRoom = binding.etRoomFoundItemForm.toString()
            val name = binding.etNameFoundItemForm.toString()
            val phoneNo = binding.etPhoneFoundItemForm.text.toString()
            val email = binding.etEmailFoundItemForm.toString()

            databaseReference = FirebaseDatabase.getInstance().getReference("Phone Directory")
            val foundItemData = FoundItemData(item, addInfo, whereFound, whatRoom, name, phoneNo, email)
            databaseReference.child(phoneNo).setValue(foundItemData).addOnSuccessListener {
                binding.etWhatHaveYouFoundFoundItemForm.text.clear()
                binding.etAnyAdditionalInfoFoundItemForm.text.clear()
                binding.etILostMyLostForm.text.clear()
                binding.etRoomFoundItemForm.text.clear()
                binding.etNameFoundItemForm.text.clear()
                binding.etPhoneFoundItemForm.text.clear()
                binding.etEmailFoundItemForm.text.clear()

                Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()

                val intent = Intent(this@FoundItemFormActivity,MainActivity::class.java )
                startActivity(intent)
                finish()
            }.addOnFailureListener{
                Toast.makeText(this,"Failed", Toast.LENGTH_SHORT).show()
            }

        }



    }
}