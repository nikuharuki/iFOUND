package com.example.ifound

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.ifound.databinding.ActivityChangePasswordBinding
import com.google.firebase.auth.EmailAuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding : ActivityChangePasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.etNewPw.visibility = View.GONE
        binding.etReEnterPw.visibility = View.GONE
        binding.tv2.visibility = View.GONE
        binding.tv3.visibility = View.GONE

        binding.btnCancel.setOnClickListener {
            finish()
        }

        binding.btnSubmit.setOnClickListener {
            val firebaseAuth = FirebaseAuth.getInstance()
            val user = firebaseAuth.currentUser
            val email = user?.email
            val password = binding.etOldPw.text.toString()
            val credential = EmailAuthProvider.getCredential(email!!, password)

            if (binding.etNewPw.visibility == View.GONE && binding.etReEnterPw.visibility == View.GONE) {
                user.reauthenticate(credential).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        binding.tv2.visibility = View.VISIBLE
                        binding.tv3.visibility = View.VISIBLE
                        binding.etNewPw.visibility = View.VISIBLE
                        binding.etReEnterPw.visibility = View.VISIBLE
                    } else {
                        binding.etOldPw.error = "You entered the wrong password!"
                    }
                }
            } else {
                if (checkIfPasswordsAreTheSame()) {
                    user.updatePassword(binding.etNewPw.text.toString()).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Password changed", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }
                } else {
                    binding.etReEnterPw.error = "Your passwords do not match!"
                }
            }
        }
    }

    private fun checkIfPasswordsAreTheSame() : Boolean {
        val newPassword = binding.etNewPw.text.toString()
        val reNewPassword = binding.etReEnterPw.text.toString()

        if(newPassword == reNewPassword) {
            return true
        }

        return false
    }
}