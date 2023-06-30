package com.example.ifound

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.ifound.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SignupActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySignupBinding
    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var database : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        database = Firebase.database.reference

        binding.btSignup.setOnClickListener {
            val name = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPw.text.toString()
            val rePassword = binding.etRepw.text.toString()

            if (areFieldsNotEmpty(name, email, password, rePassword)) {
                if (arePasswordsTheSame(password, rePassword)) {
                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                        if (it.isSuccessful) {
                            firebaseAuth.currentUser?.sendEmailVerification()?.addOnSuccessListener {
                                Toast.makeText(this, "Please verify your email", Toast.LENGTH_SHORT).show()

                                database = FirebaseDatabase.getInstance("https://ifound-731c1-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users Info")

                                // Storing user name to the database
                                val user = firebaseAuth.currentUser
                                if (user != null) {
                                    val userId = user.uid
                                    val userName = hashMapOf(
                                        "username" to name
                                    )

                                    database.child("users").child(userId)
                                        .setValue(userName)
                                }
                            }

                            ?.addOnFailureListener {
                                Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
                            }

                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Empty fields are not allowed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun areFieldsNotEmpty(name : String, email : String, password : String, rePassword: String) : Boolean{
        if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && rePassword.isNotEmpty()) {
            return true
        }

        return false
    }

    private fun arePasswordsTheSame (password : String, rePassword : String ) : Boolean {
        if (password == rePassword) {
            return true
        }

        return false
    }
}