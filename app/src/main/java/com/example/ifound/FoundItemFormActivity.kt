package com.example.ifound

import android.R
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.ifound.databinding.ActivityFoundItemFormBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.util.Calendar
import java.util.UUID

class FoundItemFormActivity : AppCompatActivity() {

    enum class PageMode {
        CREATE,
        EDIT
    }

    private lateinit var binding: ActivityFoundItemFormBinding
    private lateinit var database: DatabaseReference
    private lateinit var datePickerDialog: DatePickerDialog
    private var storageRef = Firebase.storage
    private lateinit var uri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFoundItemFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = Firebase.database.reference
        storageRef = FirebaseStorage.getInstance()

        val foundItem = intent.getParcelableExtra("FoundItemData") as FoundItemData?
        val pageMode = intent.getSerializableExtra("PageMode") as FoundItemFormActivity.PageMode

        foundItem?.let {
            setFoundItemDetails(
                it.name.toString(),
                it.location.toString(),
                it.date.toString(),
                it.description.toString(),
                it.category.toString(),
                it.image.toString()
            )
//            oldName = it.name.toString()
        }

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

        binding.btnAddPhoto.setOnClickListener {
            galleryImage.launch("image/*")
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }
        binding.btnSubmit.setOnClickListener {
            if (pageMode == FoundItemFormActivity.PageMode.CREATE){
                Log.d("FoundItemFormActivity", "createFoundItem")
                createFoundItem()
            } else {
                Log.d("FoundItemFormActivity", "editFoundItem")
                editFoundItem(foundItem?.childUid, foundItem?.image)
            }
        }
    }

    private fun editFoundItem(childUid: String?, imageUrl: String?) {
        val name = binding.etFoundItem.text.toString()
        val location = binding.etFoundItemLocation.text.toString()
        val date = binding.btnDatePicker.text.toString()
        val description = binding.etAnySpecifics.text.toString()
        val radioGroup = binding.rgSpecifics
        val category = radioGroup.findViewById<RadioButton>(radioGroup.checkedRadioButtonId).text.toString()

        Log.d("FoundItemFormActivity", "ImageURL: $imageUrl")
//        Log.d("FoundItemFormActivity", "newPhotoURL: ${newPhotoUri.toString()}")

        if (::uri.isInitialized) {
            val newPhotoUri = uri
            if (childUid != null && imageUrl != null) {
                val currentPhotoRef = storageRef.getReferenceFromUrl(imageUrl)
                currentPhotoRef.delete().addOnSuccessListener {
                    // Current photo deleted successfully, now upload the new photo and update the data
                    storageRef.getReference("Found Items").child(System.currentTimeMillis().toString())
                        .putFile(newPhotoUri)
                        .addOnSuccessListener { task ->
                            task.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                                UpdateData(name, location, date, description, category, it.toString(), childUid)
                            }
                        }
                }.addOnFailureListener { exception ->
                    // Handle any errors that occur during photo deletion
                    Toast.makeText(this, "Failed to delete current photo: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            UpdateData(name, location, date, description, category, imageUrl.toString(), childUid.toString())
        }
    }

    private fun createFoundItem() {
        val childUid = UUID.randomUUID().toString()
        val name = binding.etFoundItem.text.toString()
        val location = binding.etFoundItemLocation.text.toString()
        val date = binding.btnDatePicker.text.toString()
        val description = binding.etAnySpecifics.text.toString()

        val radioGroup = binding.rgSpecifics

        // Get the selected radio button text and store it in description
        val category = radioGroup.findViewById<RadioButton>(radioGroup.checkedRadioButtonId).text.toString()

        val user = FirebaseAuth.getInstance().currentUser
        val submittedBy = user?.uid
        val contact = user?.email

        storageRef.getReference("Found Items").child(System.currentTimeMillis().toString())
            .putFile(uri)
            .addOnSuccessListener { task ->
                task.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                    val mapImage = mapOf(
                        "url" to it.toString()
                    )


                    database =
                        FirebaseDatabase.getInstance("https://ifound-731c1-default-rtdb.asia-southeast1.firebasedatabase.app/")
                            .getReference("Found Items")

                    val foundItemData = FoundItemData(name, category, description, location, date, submittedBy, mapImage["url"], childUid)
                    database.child(childUid).setValue(foundItemData).addOnSuccessListener {
                        binding.etFoundItem.setText("")
                        binding.etAnySpecifics.setText("")
                        binding.etFoundItemLocation.setText("")
                        binding.btnDatePicker.setText("")
//                      binding.rgSpecifics.clearCheck()

                        Toast.makeText(this, "Submitted", Toast.LENGTH_SHORT).show()
                        // might need to modify this to be just finish()
                        // val intent = Intent(this@FoundItemFormActivity, MainActivity::class.java)
                        // startActivity(intent)
                        finish()

                    }.addOnFailureListener {
                        Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                    }
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
        date: String,
        description: String,
        category: String,
        image: String,
        childUid: String
    ) {
        database = FirebaseDatabase.getInstance(
            "https://ifound-731c1-default-rtdb.asia-southeast1.firebasedatabase.app/"
        ).getReference("Found Items")

        val user = mapOf(
            "name" to name,
            "location" to location,
            "date" to date,
            "description" to description,
            "category" to category,
            "image" to image
        )

        database.child(childUid).updateChildren(user).addOnSuccessListener {
            binding.etFoundItem.setText("")
            binding.etAnySpecifics.setText("")
            binding.etFoundItemLocation.setText("")
            binding.btnDatePicker.setText("")
//          binding.rgSpecifics.clearCheck()

            Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@FoundItemFormActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }.addOnFailureListener {
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setFoundItemDetails(name: String, location: String, date: String, description: String, category: String, imageUrl: String) {
        binding.btnSubmit.text = "Save"
        binding.etFoundItem.setText(name)
        binding.etFoundItemLocation.setText(location)
        binding.btnDatePicker.setText(date)
        binding.etAnySpecifics.setText(description)
        binding.rgSpecifics.check(when (category) {
            "Electronics" -> binding.rgSpecifics.findViewById<RadioButton>(binding.rbtnElectronics.id).id
            "Clothing" -> binding.rgSpecifics.findViewById<RadioButton>(binding.rbtnClothing.id).id
            "Documents" -> binding.rgSpecifics.findViewById<RadioButton>(binding.rbtnDocuments.id).id
            "Others" -> binding.rgSpecifics.findViewById<RadioButton>(binding.rbtnOthers.id).id
            else -> binding.rbtnOthers.id
        })



        Glide.with(this).load(imageUrl).into(binding.ivPhoto)

        binding.btnSubmit.setOnClickListener {
            Log.d("FoundItemFormActivity", "EditBtn clicked")
        }
    }

}