package com.example.ifound

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.ifound.databinding.ActivityChangeEmailBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class ChangeEmailActivity : AppCompatActivity() {

    private lateinit var binding : ActivityChangeEmailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCancel.setOnClickListener {
            finish()
        }

        binding.btnSubmit.setOnClickListener {
            val firebaseAuth = FirebaseAuth.getInstance()
            val user = firebaseAuth.currentUser
            val email = user?.email
            val password = binding.etPw.text.toString()
            val credential = EmailAuthProvider.getCredential(email!!, password)

            user.reauthenticate(credential).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    user.updateEmail(binding.etNewEmail.text.toString()).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Email changed", Toast.LENGTH_SHORT).show()
                            modifyDatabase(email)
                            finish()
                        }
                    }
                } else {
                    binding.etPw.error = "Check your credentials!"
                }
            }
        }
    }

    // Change the registered email in the database
    private fun modifyDatabase(email : String) {
        val firebaseAuth = FirebaseAuth.getInstance()
        val user = firebaseAuth.currentUser
        val userId = user?.uid.toString()

        val database = FirebaseDatabase.getInstance("https://ifound-731c1-default-rtdb.asia-southeast1.firebasedatabase.app/").reference
        database.child("Users").child(userId).child("Email").setValue(email)
    }
}