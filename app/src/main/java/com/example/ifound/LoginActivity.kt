package com.example.ifound

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.ifound.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth


class   LoginActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLoginBinding
    private lateinit var firebaseAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.tvSignUpLogin.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        binding.btLogin.setOnClickListener {
            val email = binding.etEmailLogin.text.toString()
            val password = binding.etPwLogin.text.toString()

            if (areFieldsNotEmpty(email, password)) {
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener{
                    if (it.isSuccessful) {
                        val isVerified = firebaseAuth.currentUser?.isEmailVerified

                        if (isVerified == true) {
                            // might need to use intent
                            val user = firebaseAuth.currentUser

                            val intent = Intent(this, MainActivity::class.java)
                            intent.putExtra("user", user)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, "Please verify your Email", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Empty fields are not allowed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun areFieldsNotEmpty(email : String, password : String) : Boolean{
        if (email.isNotEmpty() && password.isNotEmpty()) {
            return true
        }

        return false
    }
}