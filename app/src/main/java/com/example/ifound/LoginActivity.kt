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

            if (areFieldsNotEmpty(email, password)) {https://github.com/nikuharuki/iFOUND/pull/20/conflict?name=app%252Fsrc%252Fmain%252Fjava%252Fcom%252Fexample%252Fifound%252FLoginActivity.kt&ancestor_oid=725e42fd1cdb03d57e1b9f226dd23fd7bddbdd57&base_oid=3e3fa3d48ee49e22d5fcba72b092a74c6df7f145&head_oid=bf9d3e18e54d2b19097f38e09394c50a4fddb2d2
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener{
                    if (it.isSuccessful) {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Empty fields are not allowed", Toast.LENGTH_SHORT).show()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1004) {

//          val task : Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)

            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            val task : Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                task.getResult(ApiException::class.java)
                navigateToMainActivity()
            } catch (e : ApiException) {
                Log.e("Google Sign-In", "Sign-In failed", e)
//              Toast.makeText(this, "Google Sign In Failed", Toast.LENGTH_SHORT).show()

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