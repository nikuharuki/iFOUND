package com.example.ifound

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ifound.databinding.ActivityChangeUsernameBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ChangeUsernameActivity : AppCompatActivity() {

    private lateinit var binding : ActivityChangeUsernameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeUsernameBinding.inflate(layoutInflater)
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

            val newName = binding.etNewUsername.text.toString()

            user.reauthenticate(credential).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    modifyDatabase(newName)
                    finish()
                } else {
                    binding.etPw.error = "Check your credentials"
                }
            }
        }
    }

    private fun modifyDatabase(newName : String) {
        val firebaseAuth = FirebaseAuth.getInstance()
        val user = firebaseAuth.currentUser
        val userId = user?.uid.toString()

        val database = FirebaseDatabase.getInstance("https://ifound-731c1-default-rtdb.asia-southeast1.firebasedatabase.app/").reference
        database.child("Users").child(userId).child("Name").setValue(newName)
    }
}