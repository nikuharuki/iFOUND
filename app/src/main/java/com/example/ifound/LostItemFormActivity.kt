package com.example.ifound

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.ifound.databinding.ActivityLostItemFormBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class LostItemFormActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLostItemFormBinding
    private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLostItemFormBinding.inflate(layoutInflater)

        database = Firebase.database.reference

        binding.btnSubmit.setOnClickListener {
            val name = binding.etItemName.text.toString()
            val location = binding.etItemLocation.text.toString()
            val date = binding.etItemDate.text.toString()
            val description = binding.etItemSpecifics.text.toString()

            database = FirebaseDatabase.getInstance().getReference("Lost Items")
            val LostItemData = LostItemData(name, location, date, description)
            database.child(name).setValue(LostItemData).addOnSuccessListener {
                binding.etItemName.setText("")
                binding.etItemLocation.setText("")
                binding.etItemDate.setText("")
                binding.etItemSpecifics.setText("")

                Toast.makeText(this, "Submitted", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@LostItemFormActivity, MainActivity::class.java)
                startActivity(intent)
                finish()

            }.addOnFailureListener {
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}