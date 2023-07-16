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
            checkFirstLogin()
        }
    }

    private fun checkFirstLogin() {
        val sharedPreferences : SharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val isFirstLogin = sharedPreferences.getBoolean(KEY_FIRST_LOGIN, true)

        if (isFirstLogin) {
            val intent = Intent(this@SplashActivity, StartUpTutorialActivity::class.java)
            startActivity(intent)
            sharedPreferences.edit().putBoolean(KEY_FIRST_LOGIN, false).apply()
            finish()
        } else {
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