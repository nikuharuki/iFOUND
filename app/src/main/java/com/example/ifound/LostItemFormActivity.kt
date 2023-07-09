package com.example.ifound

import android.R
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import com.example.ifound.databinding.ActivityLostItemFormBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.io.ByteArrayOutputStream
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Button
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.util.Calendar
import java.util.UUID

class LostItemFormActivity() : AppCompatActivity() {
    enum class PageMode {
        CREATE,
        EDIT
    }

    var sImage: String = ""
    private lateinit var binding: ActivityLostItemFormBinding
    private lateinit var database: DatabaseReference
    private lateinit var datePickerDialog: DatePickerDialog
    private lateinit var dateButton: Button
    private var storageRef = Firebase.storage
    private lateinit var uri: Uri
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLostItemFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val lostitem = intent.getParcelableExtra("LostItemData") as LostItemData?
        val pageMode = intent.getSerializableExtra("PageMode") as PageMode

//        var oldName: String? = null
        lostitem?.let {
            setLostItemDetails(
                it.name.toString(),
                it.location.toString(),
                it.date.toString(),
                it.description.toString(),
                it.image.toString()
            )
//            oldName = it.name.toString()
        }

        database = Firebase.database.reference
        storageRef = FirebaseStorage.getInstance()

        val cameraIntentLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback {
                if (it.resultCode == RESULT_OK) {
                    val photo: Bitmap = it.data?.extras?.get("data") as Bitmap
                    binding.ivPhoto.setImageBitmap(photo)
                }
            })

        val galleryIntentLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback {
                if (it.resultCode == Activity.RESULT_OK) {
                    val imageUri = it.data?.data
                    val imageStream = imageUri?.let { it1 -> contentResolver.openInputStream(it1) }
                    val photo = BitmapFactory.decodeStream(imageStream)
                    binding.ivPhoto.setImageBitmap(photo)
                }
            })

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
                    uri = it
                }
            }
        )


        binding.btnAddphoto.setOnClickListener {
//            val permissions = arrayOf(Manifest.permission.CAMERA)
//            if (ContextCompat.checkSelfPermission(
//                    this@LostItemFormActivity,
//                    Manifest.permission.CAMERA
//                ) != PackageManager.PERMISSION_GRANTED
//            ) {
//                ActivityCompat.requestPermissions(this@LostItemFormActivity, permissions, 1)
//            } else {
////                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
////                cameraIntentLauncher.launch(intent)
//
//                val intent = Intent(Intent.ACTION_GET_CONTENT)
//                intent.type = "image/*"
//                ActivityResultLauncher.launch(intent)
//            }
            galleryImage.launch("image/*")


        }

        binding.btnCancel.setOnClickListener {
            finish()
        }

        binding.btnSubmit.setOnClickListener {
            if (pageMode == PageMode.CREATE){
                Log.d("LostItemFormActivity", "createLostItem")
                createLostItem()
            } else {
                Log.d("LostItemFormActivity", "editLostItem")
                editLostItem(lostitem?.childUid, lostitem?.image)
            }
        }
    }

    private fun editLostItem(childUid: String?, imageUrl: String?) {
        val name = binding.etItemName.text.toString()
        val location = binding.etItemLocation.text.toString()
        val date = binding.btnDatePicker.text.toString()
        val description = binding.etItemSpecifics.text.toString()
        val newPhotoUri = uri

        if (childUid != null && imageUrl != null) {
            if (imageUrl != newPhotoUri.toString()){
                val currentPhotoRef = storageRef.getReferenceFromUrl(imageUrl)
                currentPhotoRef.delete().addOnSuccessListener {
                    // Current photo deleted successfully, now upload the new photo and update the data
                    storageRef.getReference("Lost Items").child(System.currentTimeMillis().toString())
                        .putFile(newPhotoUri)
                        .addOnSuccessListener { task ->
                            task.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                                UpdateData(name, location, description, date, it.toString(), childUid)
                            }
                        }
                }.addOnFailureListener { exception ->
                    // Handle any errors that occur during photo deletion
                    Toast.makeText(this, "Failed to delete current photo: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
            } else{
                UpdateData(name, location, description, date, imageUrl, childUid)
            }

        }
    }

    private fun createLostItem() {
        val childUid = UUID.randomUUID().toString()
        val name = binding.etItemName.text.toString()
        val location = binding.etItemLocation.text.toString()
        val date = binding.btnDatePicker.text.toString()
        val description = binding.etItemSpecifics.text.toString()

        val user = FirebaseAuth.getInstance().currentUser

        val submittedBy = user?.displayName
        val contact = user?.email

        storageRef.getReference("Lost Items").child(System.currentTimeMillis().toString())
            .putFile(uri)
            .addOnSuccessListener { task ->
                task.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                    val mapImage = mapOf(
                        "url" to it.toString()
                    )


                    database =
                        FirebaseDatabase.getInstance("https://ifound-731c1-default-rtdb.asia-southeast1.firebasedatabase.app/")
                            .getReference("Lost Items")

                    val lostItemData = LostItemData(name, location, description, date, submittedBy, contact, mapImage["url"], childUid)
                    database.child(childUid).setValue(lostItemData).addOnSuccessListener {
                        binding.etItemName.setText("")
                        binding.etItemLocation.setText("")
                        binding.btnDatePicker.setText("")
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


    }

    private val ActivityResultLauncher = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
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

    private fun UpdateData(
        name: String,
        location: String,
        description: String,
        date: String,
        image: String,
        childUid: String
    ) {
        database = FirebaseDatabase.getInstance(
            "https://ifound-731c1-default-rtdb.asia-southeast1.firebasedatabase.app/"
        ).getReference("Lost Items")

        val user = mapOf(
            "name" to name,
            "location" to location,
            "date" to date,
            "description" to description,
            "image" to image
        )

        database.child(childUid).updateChildren(user).addOnSuccessListener {
            binding.etItemName.setText("")
            binding.etItemLocation.setText("")
            binding.btnDatePicker.setText("")
            binding.etItemSpecifics.setText("")

            Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@LostItemFormActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }.addOnFailureListener {
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
        }
    }
    private fun setLostItemDetails(name: String, location: String, date: String, description: String, imageUrl: String) {
        binding.btnSubmit.text = "Save"
        binding.etItemName.setText(name)
        binding.etItemLocation.setText(location)
        binding.btnDatePicker.setText(date)
        binding.etItemSpecifics.setText(description)
        Glide.with(this).load(imageUrl).into(binding.ivPhoto)

        binding.btnSubmit.setOnClickListener {
            Log.d("LostItemFormActivity", "EditBtn clicked")
        }
    }
}