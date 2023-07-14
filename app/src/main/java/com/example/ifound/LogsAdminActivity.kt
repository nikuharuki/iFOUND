package com.example.ifound

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ifound.databinding.ActivityLogsAdminBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LogsAdminActivity : AppCompatActivity() {
    enum class PageMode {
        CLAIM,
        LOGS
    }
    private lateinit var binding : ActivityLogsAdminBinding
    private lateinit var claimRecyclerView: RecyclerView
    private lateinit var claimRequestAdapter: ClaimRequestAdapter
    private var claimRequestList = ArrayList<ClaimRequestData>()
    private lateinit var claimRequestRef: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogsAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        claimRecyclerView = binding.rvLogs


        claimRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        claimRecyclerView.setHasFixedSize(true)
        claimRequestAdapter = ClaimRequestAdapter(this, claimRequestList)
        claimRecyclerView.adapter = claimRequestAdapter



        claim()

    }

    private fun claim() {
        claimRequestRef = FirebaseDatabase.getInstance("https://ifound-731c1-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Claim Requests")

        claimRequestRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                claimRequestList.clear() //bat dalawa toh
                    if (snapshot.exists()) {
                        for (itemSnapshot in snapshot.children) {
                            val claimRequest = itemSnapshot.getValue(ClaimRequestData::class.java)
                            claimRequest?.let {
                                claimRequestList.add(it)
                            }
                        }
                        binding.rvLogs.adapter = ClaimRequestAdapter(this@LogsAdminActivity,claimRequestList)
                        binding.rvLogs.adapter = claimRequestAdapter
                    }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}