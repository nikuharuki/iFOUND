package com.example.ifound

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.ifound.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task

class   LoginActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLoginBinding

    private lateinit var gso : GoogleSignInOptions
    private lateinit var gsc : GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN).requestEmail().build()
        gsc = GoogleSignIn.getClient(this, gso)

        binding.btLoginWG.setOnClickListener {
            logInViaGoogle()
        }
    }

    private fun logInViaGoogle() {
        val intent = gsc.signInIntent
        startActivityForResult(intent, 1004)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1004) {
//          val task : Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)

            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                task.getResult(ApiException::class.java)
                navigateToMainActivity()
            } catch (e : ApiException) {
                Log.e("Google Sign-In", "Sign-In failed", e)
//              Toast.makeText(this, "Google Sign In Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

                        // is MainActivity the next Activity?
    private fun navigateToMainActivity() {
        finish()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun handleLogInResult(completedTask: Task<GoogleSignInAccount>) {

    }
}