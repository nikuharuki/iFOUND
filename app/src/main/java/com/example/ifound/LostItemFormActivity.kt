package com.example.ifound

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.ifound.databinding.ActivityLostItemFormBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn.hasPermissions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.UUID
import androidx.core.content.ContextCompat.checkSelfPermission
import java.io.ByteArrayOutputStream
import android.util.Base64

class LostItemFormActivity : AppCompatActivity() {
    var sImage : String = ""
    private lateinit var binding : ActivityLostItemFormBinding
    private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLostItemFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = Firebase.database.reference

        val cameraIntentLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), ActivityResultCallback {
            if(it.resultCode == RESULT_OK){
                val photo : Bitmap = it.data?.extras?.get("data") as Bitmap
                binding.ivPhoto.setImageBitmap(photo)
            }
        })

        val galleryIntentLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), ActivityResultCallback {
            if(it.resultCode == Activity.RESULT_OK){
                val imageUri = it.data?.data
                val imageStream = imageUri?.let { it1 -> contentResolver.openInputStream(it1) }
                val photo = BitmapFactory.decodeStream(imageStream)
                binding.ivPhoto.setImageBitmap(photo)
            }
        })

        binding.btnAddphoto.setOnClickListener {
            val permissions = arrayOf(Manifest.permission.CAMERA)
            if (ContextCompat.checkSelfPermission(this@LostItemFormActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this@LostItemFormActivity, permissions, 1)
            }else{
//                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//                cameraIntentLauncher.launch(intent)

                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "image/*"
                ActivityResultLauncher.launch(intent)
            }
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }

        binding.btnSubmit.setOnClickListener {
            val name = binding.etItemName.text.toString()
            val location = binding.etItemLocation.text.toString()
            val date = binding.etItemDate.text.toString()
            val description = binding.etItemSpecifics.text.toString()

            val user = FirebaseAuth.getInstance().currentUser
            val submittedBy = user?.displayName ?: "" // Replace with the appropriate user information


            database = FirebaseDatabase.getInstance("https://ifound-731c1-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Lost Items")

            val lostItemData = LostItemData(name, location, date, description, submittedBy, sImage)
            database.child(name).setValue(lostItemData).addOnSuccessListener{
                binding.etItemName.setText("")
                binding.etItemLocation.setText("")
                binding.etItemDate.setText("")
                binding.etItemSpecifics.setText("")
                sImage = ""

                Toast.makeText(this, "Submitted", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@LostItemFormActivity, MainActivity::class.java)
                startActivity(intent)
                finish()

            }.addOnFailureListener {
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private val ActivityResultLauncher = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()) { result:ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            val uri = result.data!!.data
            try {
                val inputStream = contentResolver.openInputStream(uri!!)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                val bytes = stream.toByteArray()
                sImage = Base64.encodeToString(bytes, Base64.DEFAULT)
                binding.ivPhoto.setImageBitmap(bitmap)
                inputStream!!.close()

            } catch (e: Exception) {
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}