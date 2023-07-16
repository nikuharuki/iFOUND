package com.example.ifound

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
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

    enum class PageMode {
        NOTIFICATIONS,
        TICKETS
    }

    private lateinit var binding : ActivityNotificationsBinding
    private lateinit var notificationReference: DatabaseReference
    private lateinit var claimReference: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NotificationsAdapter
    private lateinit var ticketAdapter: TicketAdapter
    private lateinit var foundReference: DatabaseReference
    private lateinit var firebaseAuth : FirebaseAuth

    private var notifList = ArrayList<NotificationsModel>()
    private var claimList = ArrayList<ClaimRequestData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pageMode = intent.getSerializableExtra("PageMode") as PageMode




        //init
        firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser?.uid

        recyclerView = binding.recyclerView


        claimReference = FirebaseDatabase.getInstance("https://ifound-731c1-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Claim Requests")
        notificationReference = FirebaseDatabase.getInstance("https://ifound-731c1-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Notifications")

        //get 'name' in Found Items
        if (pageMode == PageMode.NOTIFICATIONS) {
            getNotifications(currentUser)
        } else {
            getTickets(currentUser)
        }

    }

    private fun getTickets(currentUser: String?) {
        //init
        binding.textView.text = "Tickets"


        binding.recyclerView.setPadding(30, 0, 20, 0)
//        android:scaleY="1.1"
//        android:scaleX="1.1"
//        android:paddingHorizontal="20dp"
//        android:paddingEnd="20dp"
//        android:paddingStart="27dp"
        recyclerView.layoutManager = GridLayoutManager(this@Notifications, 2)
        recyclerView.setHasFixedSize(true)
        ticketAdapter = TicketAdapter(this@Notifications, claimList)
        recyclerView.adapter = ticketAdapter


        //get all notifications
        claimReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                claimList.clear()

                for (claimSnapshot in snapshot.children) {
                    val claim = claimSnapshot.getValue(ClaimRequestData::class.java)
                    if (claim != null && claim.claimer == currentUser) {
                        claimList.add(claim)
                    } else if (claim != null && claim.foundItemSubmitter == currentUser) {
                        claimList.add(claim)
                    }
                }
                ticketAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error case if needed
            }
        })

//        val notificationsReference = FirebaseDatabase.getInstance("https://ifound-731c1-default-rtdb.asia-southeast1.firebasedatabase.app")
//            .getReference("Notifications")
//
//
//        notificationsReference.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                for (notificationSnapshot in dataSnapshot.children) {
//                    val sentTo = notificationSnapshot.child("sentTo").value
//
//                    if (sentTo == currentUser) {
//                        ticketAdapter.onItemClick = {
//                            val intent = Intent(this@Notifications, FoundItemPageActivity::class.java)
//                            intent.putExtra("ClaimRequest", it)
//                            intent.putExtra("PageMode", FoundItemPageActivity.PageMode.TICKET)
//                            startActivity(intent)
//                        }
//                    }
//
//                }
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//                // Handle the error
//            }
//        })
            ticketAdapter.onItemClick = {
                if (it.claimer == currentUser) {
                    val intent = Intent(this@Notifications, FoundItemPageActivity::class.java)
                    intent.putExtra("ClaimRequest", it)
                    intent.putExtra("PageMode", FoundItemPageActivity.PageMode.TICKET)
                    intent.putExtra("UserTypes", FoundItemPageActivity.UserTypes.CLAIMER)
                    startActivity(intent)
                }else if (it.foundItemSubmitter == currentUser) {
                    val intent = Intent(this@Notifications, FoundItemPageActivity::class.java)
                    intent.putExtra("ClaimRequest", it)
                    intent.putExtra("PageMode", FoundItemPageActivity.PageMode.TICKET)
                    intent.putExtra("UserTypes", FoundItemPageActivity.UserTypes.FOUNDER)
                    startActivity(intent)
                }

            }

    }

    private fun getNotifications(currentUser: String?) {
        //init
        recyclerView.layoutManager = LinearLayoutManager(this@Notifications, LinearLayoutManager.VERTICAL, true)
        recyclerView.setHasFixedSize(true)
        adapter = NotificationsAdapter(this@Notifications, notifList)
        recyclerView.adapter = adapter
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