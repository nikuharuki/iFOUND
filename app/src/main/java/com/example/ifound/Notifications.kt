package com.example.ifound

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ifound.databinding.ActivityNotificationsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class Notifications : AppCompatActivity() {

    private lateinit var binding : ActivityNotificationsBinding
    private lateinit var notificationReference: DatabaseReference
    private lateinit var claimReference: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NotificationsAdapter
    private lateinit var foundReference: DatabaseReference
    private lateinit var firebaseAuth : FirebaseAuth

    private var notifList = ArrayList<NotificationsModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationsBinding.inflate(layoutInflater)
        setContentView(binding.root)




        //init
        firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser?.uid

        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this@Notifications, LinearLayoutManager.VERTICAL, true)
        recyclerView.setHasFixedSize(true)
        adapter = NotificationsAdapter(this@Notifications, notifList)
        recyclerView.adapter = adapter

        claimReference = FirebaseDatabase.getInstance("https://ifound-731c1-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Claim Requests")
        notificationReference = FirebaseDatabase.getInstance("https://ifound-731c1-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Notifications")

        //get 'name' in Found Items


        //get all notifications
        notificationReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                notifList.clear()

                for (notifSnapshot in snapshot.children) {
                    val notif = notifSnapshot.getValue(NotificationsModel::class.java)
                    if (notif != null && notif.sentTo == currentUser) {
                        notifList.add(notif)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error case if needed
            }
        })
    }

}