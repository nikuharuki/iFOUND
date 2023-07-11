package com.example.ifound

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ifound.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */


class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentHomeBinding
    private lateinit var lostItemAdapter: LostItemAdapter
    private lateinit var recyclerView: RecyclerView

//    private val lostItemList = ArrayList<LostItemData>()

    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var usernameReference : DatabaseReference

    private lateinit var databaseReference: DatabaseReference
    private val lostItemList = ArrayList<LostItemData>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)

        }



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {
        // Inflate the layout for this fragment

        binding = FragmentHomeBinding.inflate(inflater, container, false)

        recyclerView = binding.lostitemsrecycler


        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.setHasFixedSize(true)
        lostItemAdapter = LostItemAdapter(requireContext(), lostItemList)
        recyclerView.adapter = lostItemAdapter

        lostItemList.clear()
        getUserData()

//      recyclerView.adapter = lostItemAdapter

        lostItemAdapter.onItemClick = {
            Log.d("TAG", "Item clicked")
            val intent = Intent(requireContext(), LostItemPageActivity::class.java)
            intent.putExtra("LostItemData", it)
            startActivity(intent)
        }

        binding.tvTest.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            val intent = Intent(this@HomeFragment.requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish() //requireActivity?? so main ang mafifinish
        }


        getUserName()


        return binding.root
    }

    private fun getUserName() {
        firebaseAuth = FirebaseAuth.getInstance()
        val userId = firebaseAuth.currentUser?.uid
        val databaseReference = FirebaseDatabase.getInstance("https://ifound-731c1-default-rtdb.asia-southeast1.firebasedatabase.app/").reference
        usernameReference = databaseReference.child("Users").child(userId!!).child("Name")

        usernameReference.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val username = snapshot.getValue(String::class.java)

                    binding.tvHelloUser.text = "Hello, $username"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
    private fun getUserData() {
        databaseReference = FirebaseDatabase.getInstance("https://ifound-731c1-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Lost Items")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                lostItemList.clear()
                if (isAdded) {
                    if (snapshot.exists()) {
                        for (itemSnapshot in snapshot.children) {
                            val lostItem = itemSnapshot.getValue(LostItemData::class.java)
                            lostItem?.let {
                                lostItemList.add(it)
                            }
                        }
                        binding.lostitemsrecycler.adapter = LostItemAdapter(requireContext(),lostItemList)
                        binding.lostitemsrecycler.adapter = lostItemAdapter
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    companion object {

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment homeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        updateItemList()
//        Log.d("TAG", "Resumed")
//    }
//
//    private fun updateItemList() {
//        lostItemAdapter.notifyDataSetChanged()
//    }
}