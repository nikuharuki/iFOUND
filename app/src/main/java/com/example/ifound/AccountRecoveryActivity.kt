package com.example.ifound

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.ifound.databinding.ActivityAccountRecoveryBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AccountRecoveryActivity : AppCompatActivity() {

    private lateinit var binding : ActivityAccountRecoveryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountRecoveryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSubmit.setOnClickListener {
            val email = binding.etEmail.text.toString()

            Firebase.auth.sendPasswordResetEmail(email)
                .addOnCompleteListener{ task ->
                    if (task.isSuccessful) {
                        setContentView(R.layout.email_verification_message)

                        val button = findViewById<Button>(R.id.btn_back_to_login_email_verification)

                        button.setOnClickListener {
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                        }
                    }
                }
        }
    }
}