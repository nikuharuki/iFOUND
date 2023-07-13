package com.example.ifound

import android.Manifest
import android.R
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.ifound.databinding.ActivityFoundItemFormBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.io.ByteArrayOutputStream
import java.util.Calendar

class FoundItemFormActivity : AppCompatActivity() {
    var sImage: String = ""
    private lateinit var binding: ActivityFoundItemFormBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var datePickerDialog: DatePickerDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFoundItemFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnDatePicker.setOnClickListener {
            initDatePicker()
            binding.btnDatePicker.text = getTodaysDate()
            datePickerDialog.show()
        }

        val galleryImage = registerForActivityResult(
            ActivityResultContracts.GetContent(),
            ActivityResultCallback {
                binding.ivPhoto.setImageURI(it)
                if (it != null) {
              //      uri = it
                }
            }
        )

//        val cameraIntentLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), ActivityResultCallback {
//            if(it.resultCode == RESULT_OK){
//                val photo : Bitmap = it.data?.extras?.get("data") as Bitmap
//                binding.ivPhoto.setImageBitmap(photo)
//            }
//        })
//
//        val galleryIntentLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), ActivityResultCallback {
//            if(it.resultCode == Activity.RESULT_OK){
//                val imageUri = it.data?.data
//                val imageStream = imageUri?.let { it1 -> contentResolver.openInputStream(it1) }
//                val photo = BitmapFactory.decodeStream(imageStream)
//                binding.ivPhoto.setImageBitmap(photo)
//            }
//        })
//
//        binding.btnAddPhoto.setOnClickListener {
//                val permissions = arrayOf(Manifest.permission.CAMERA)
//                if (ContextCompat.checkSelfPermission(this@FoundItemFormActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                    ActivityCompat.requestPermissions(this@FoundItemFormActivity, permissions, 1)
//                }else{
////                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
////                cameraIntentLauncher.launch(intent)
//
//                    val intent = Intent(Intent.ACTION_GET_CONTENT)
//                    intent.type = "image/*"
//                    ActivityResultLauncher.launch(intent)
//                }
//        }
//
//        binding.btnSubmit.setOnClickListener {
//            val item = binding.etWhatHaveYouFoundFoundItemForm.text.toString()
//            val addInfo = binding.etAnyAdditionalInfoFoundItemForm.toString()
//            val whereFound = binding.etILostMyLostForm.toString()
//            val whatRoom = binding.etRoomFoundItemForm.toString()
//            val name = binding.etNameFoundItemForm.toString()
//            val phoneNo = binding.etPhoneFoundItemForm.text.toString()
//            val email = binding.etEmailFoundItemForm.toString()
//
//            databaseReference = FirebaseDatabase.getInstance().getReference("Found Items")
//            val foundItemData = FoundItemData(item, addInfo, whereFound, whatRoom, name, phoneNo, email, sImage)
//            databaseReference.child(item).setValue(foundItemData).addOnSuccessListener {
//                binding.etWhatHaveYouFoundFoundItemForm.text.clear()
//                binding.etAnyAdditionalInfoFoundItemForm.text.clear()
//                binding.etILostMyLostForm.text.clear()
//                binding.etRoomFoundItemForm.text.clear()
//                binding.etNameFoundItemForm.text.clear()
//                binding.etPhoneFoundItemForm.text.clear()
//                binding.etEmailFoundItemForm.text.clear()
//                sImage = ""
//
//                Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
//
//                val intent = Intent(this@FoundItemFormActivity,MainActivity::class.java )
//                startActivity(intent)
//                finish()
//            }.addOnFailureListener{
//                Toast.makeText(this,"Failed", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//    private val ActivityResultLauncher = registerForActivityResult<Intent, ActivityResult>(
//        ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
//        if (result.resultCode == RESULT_OK) {
//            val uri = result.data!!.data
//            try {
//                val inputStream = contentResolver.openInputStream(uri!!)
//                val bitmap = BitmapFactory.decodeStream(inputStream)
//                val stream = ByteArrayOutputStream()
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
//                val bytes = stream.toByteArray()
//                sImage = Base64.encodeToString(bytes, Base64.DEFAULT)
//                binding.ivPhoto.setImageBitmap(bitmap)
//                inputStream!!.close()
//
//            } catch (e: Exception) {
//                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
    }


    private fun getTodaysDate(): String {
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH) + 1
        val day = cal.get(Calendar.DAY_OF_MONTH)
        return makeDateString(day, month, year)
    }

    private fun initDatePicker() {
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                val formattedDate = makeDateString(dayOfMonth, monthOfYear + 1, year)
                binding.btnDatePicker.text = formattedDate
            }

        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)

        val style = R.style.Theme_Holo_Light_Dialog

        datePickerDialog = DatePickerDialog(this, style, dateSetListener, year, month, day)
    }

    private fun makeDateString(day: Int, month: Int, year: Int): String {
        return getMonthFormat(month) + " " + day + " " + year
    }

    private fun getMonthFormat(month: Int): String {
        return when (month) {
            1 -> "JAN"
            2 -> "FEB"
            3 -> "MAR"
            4 -> "APR"
            5 -> "MAY"
            6 -> "JUN"
            7 -> "JUL"
            8 -> "AUG"
            9 -> "SEP"
            10 -> "OCT"
            11 -> "NOV"
            12 -> "DEC"
            else -> "JAN"
        }
    }

    fun openDatePicker(view: View) {
        datePickerDialog.show()
    }

}