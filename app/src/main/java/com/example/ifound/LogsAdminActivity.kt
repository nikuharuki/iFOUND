package com.example.ifound

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ifound.databinding.ActivityLogsAdminBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.math.log

class LogsAdminActivity : AppCompatActivity() {

    enum class PageMode {
        CLAIM,
        RESOLVE,
        LOGS
    }

    private lateinit var binding : ActivityLogsAdminBinding

    private lateinit var recyclerView: RecyclerView
    private lateinit var logAdapter : LogAdapter
    private val logsList = ArrayList<LogModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogsAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recyclerView = binding.rvLogs

        val pageMode = intent.getSerializableExtra("PageMode") as PageMode

        if (pageMode == PageMode.LOGS) {
            readFromLogsDatabase()
        }
    }

    private fun readFromLogsDatabase() {
        logsList.clear()

        val databaseReference = FirebaseDatabase.getInstance("https://ifound-731c1-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Logs")
        databaseReference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (logSnapshot in snapshot.children) {
                        val logMessage = logSnapshot.child("Log Message").getValue(String::class.java)
                        val logModel = LogModel(logMessage)
                        logsList.add(logModel)
                        initLogsRecyclerView()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun initLogsRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this@LogsAdminActivity, LinearLayoutManager.VERTICAL, false)
        recyclerView.setHasFixedSize(true)
        logAdapter = LogAdapter(this@LogsAdminActivity, logsList)
        recyclerView.adapter = logAdapter
    }
}


