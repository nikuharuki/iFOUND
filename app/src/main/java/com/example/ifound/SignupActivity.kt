package com.example.ifound

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts.StartIntentSenderForResult
import androidx.appcompat.app.AppCompatActivity
import com.example.ifound.databinding.ActivitySignupBinding
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class SignupActivity : AppCompatActivity() {
    private lateinit var oneTapClient: SignInClient
    private lateinit var signUpRequest: BeginSignInRequest
    private lateinit var binding : ActivitySignupBinding

    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var database : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //One Tap Google Sign In
        oneTapClient = Identity.getSignInClient(this)
        signUpRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId(getString(R.string.web_client_id))
                    // Show all accounts on the device.
                    .setFilterByAuthorizedAccounts(false)
                    .build())
            .build()

        val activityResultLauncher: ActivityResultLauncher<IntentSenderRequest> = registerForActivityResult(
            StartIntentSenderForResult()
        ) { result: ActivityResult ->
            if(result.resultCode == Activity.RESULT_OK){
                try {
                    val credential = oneTapClient.getSignInCredentialFromIntent(result.data)
                    val idToken = credential.googleIdToken
                    if (idToken != null) {
                        val email: String = credential.id
                        Toast.makeText(applicationContext,"Email: "+email, Toast.LENGTH_SHORT).show()
                    }
                } catch (e: ApiException) {
                    e.printStackTrace()
                }
            }
        }


        binding.googleBtn.setOnClickListener {
            oneTapClient.beginSignIn(signUpRequest)
                .addOnSuccessListener(this) { result ->
                    val intentSenderRequest = IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                    activityResultLauncher.launch(intentSenderRequest)
                }
                .addOnFailureListener(this) { e ->
                    // No Google Accounts found. Just continue presenting the signed-out UI.
                    Log.d(TAG, e.localizedMessage as String)
                }
        }

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
                                database = FirebaseDatabase.getInstance("https://ifound-731c1-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users")

                                // Storing user name to the database
                                val user = firebaseAuth.currentUser
                                if (user != null) {
                                    val userId = user.uid
                                    database.child(userId).child("Name").setValue(name)
                                    database.child(userId).child("Email").setValue(email)
                                    database.child(userId).child("stars").setValue(0)
                                }

                                setContentView(R.layout.email_verification_message)
                                val button = findViewById<Button>(R.id.btn_back_to_login_email_verification)
                                FirebaseAuth.getInstance().signOut()
                                button.setOnClickListener{
                                    finish()
                                }
                            }
                            ?.addOnFailureListener {
                                Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
                            }
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