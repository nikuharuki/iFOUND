package com.example.ifound

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.ifound.databinding.ActivityLostItemFormBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.UUID

class LostItemFormActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLostItemFormBinding
    private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLostItemFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = Firebase.database.reference

        binding.btnSubmit.setOnClickListener {
            val name = binding.etItemName.text.toString()
            val location = binding.etItemLocation.text.toString()
            val date = binding.etItemDate.text.toString()
            val description = binding.etItemSpecifics.text.toString()

            val user = FirebaseAuth.getInstance().currentUser
            val submittedBy = user?.displayName ?: "" // Replace with the appropriate user information


            database = FirebaseDatabase.getInstance("https://ifound-731c1-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Lost Items")


            val itemId = UUID.randomUUID().toString()
            val itemRef = database.child("Lost Items").child(itemId ?: "")
            val lostItemData = LostItemData(name, location, date, description, submittedBy)
            itemRef.setValue(lostItemData).addOnSuccessListener{
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