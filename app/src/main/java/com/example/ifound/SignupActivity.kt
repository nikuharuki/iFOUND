package com.example.ifound

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.ifound.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth

class SignupActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySignupBinding
    private lateinit var firebaseAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.btSignup.setOnClickListener {
            val name = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPw.text.toString()
            val rePassword = binding.etRepw.text.toString()

            if (areFieldsNotEmpty(name, email, password, rePassword)) {
                if (arePasswordsTheSame(password, rePassword)) {
                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                        if (it.isSuccessful) {
                            val intent = Intent(this, LoginActivity::class.java)
                            // add putExtra para mapasa yung info ni user?
                            // or mangyayari na ba yon through database?
                            startActivity(intent)
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