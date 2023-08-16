package com.example.ifound

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import java.util.Timer
import kotlin.concurrent.schedule

private const val PREFS_NAME = "MyPrefs"
private const val KEY_FIRST_LOGIN = "firstLogin"


class SplashActivity : AppCompatActivity() {

    private lateinit var firebaseAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        firebaseAuth = FirebaseAuth.getInstance()



        Timer().schedule(3000) {
            val currentUser = firebaseAuth.currentUser

            if (currentUser != null) {
                navigateToMainScreen()
            } else {
                navigateToLoginScreen()
            }
        }
    }

    private fun navigateToLoginScreen() {
        startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
        finish()
    }

    private fun navigateToMainScreen() {
        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
        finish()
    }
}