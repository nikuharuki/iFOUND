package com.example.ifound

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ifound.databinding.ActivityMyReportsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MyReportsActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMyReportsBinding
    private lateinit var databaseReference: FirebaseDatabase
    private lateinit var lostItemRv: RecyclerView
    private lateinit var foundItemRv: RecyclerView
    private lateinit var lostAdapter: LostItemAdapter
    private lateinit var foundAdapter: FoundItemAdapter
    private lateinit var firebaseAuth: FirebaseAuth

    private val lostList = ArrayList<LostItemData>()
    private val foundList = ArrayList<FoundItemData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyReportsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser?.uid


        lostItemRv = binding.lostitemsrecycler
        foundItemRv = binding.founditemsrecycler

        lostItemRv.layoutManager = LinearLayoutManager(this@MyReportsActivity, LinearLayoutManager.HORIZONTAL, false)
        lostItemRv.setHasFixedSize(true)
        lostAdapter = LostItemAdapter(this@MyReportsActivity, lostList)
        lostItemRv.adapter = lostAdapter

        lostList.clear()


        //add all the lost items where submittedBy == current user
        databaseReference = FirebaseDatabase.getInstance("https://ifound-731c1-default-rtdb.asia-southeast1.firebasedatabase.app/")
        databaseReference.getReference("Lost Items").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot in snapshot.children) {
                    val lostItemData = dataSnapshot.getValue(LostItemData::class.java)
                    if (lostItemData!!.submittedBy == currentUser) {
                        lostList.add(lostItemData)
                    }
                }
                binding.lostitemsrecycler.adapter = LostItemAdapter(this@MyReportsActivity,lostList)
                binding.lostitemsrecycler.adapter = lostAdapter

                lostAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MyReportsActivity, "Error fetching data", Toast.LENGTH_SHORT).show()
            }
        })

        foundItemRv.layoutManager = LinearLayoutManager(this@MyReportsActivity, LinearLayoutManager.HORIZONTAL, false)
        foundItemRv.setHasFixedSize(true)
        foundAdapter = FoundItemAdapter(this@MyReportsActivity, foundList)
        foundItemRv.adapter = foundAdapter

        foundList.clear()


        //add all the found items where submittedBy == current user
        databaseReference = FirebaseDatabase.getInstance("https://ifound-731c1-default-rtdb.asia-southeast1.firebasedatabase.app/")
        databaseReference.getReference("Found Items").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot in snapshot.children) {
                    val foundItemData = dataSnapshot.getValue(FoundItemData::class.java)
                    if (foundItemData!!.submittedBy == currentUser) {
                        foundList.add(foundItemData)
                    }
                }
                binding.founditemsrecycler.adapter = FoundItemAdapter(this@MyReportsActivity,foundList)
                binding.founditemsrecycler.adapter = foundAdapter
                foundAdapter.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MyReportsActivity, "Error fetching data", Toast.LENGTH_SHORT).show()
            }
        })

        lostAdapter.onItemClick = {
            Toast.makeText(this, "Item Clicked", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LostItemPageActivity::class.java)
            intent.putExtra("PageMode", FoundItemPageActivity.PageMode.NORMAL)
            intent.putExtra("LostItemData", it)
            startActivity(intent)
        }
        foundAdapter.onItemClick = {
            Toast.makeText(this, "Item Clicked", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, FoundItemPageActivity::class.java)
            intent.putExtra("PageMode", FoundItemPageActivity.PageMode.NORMAL)
            intent.putExtra("FoundItemData", it)
            startActivity(intent)
        }
    }
}