package com.example.ifound

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ifound.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.snapshots
import com.google.firebase.ktx.Firebase
import kotlin.math.log

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [homeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */


class homeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit  var binding: FragmentHomeBinding
    private lateinit var lostItemAdapter: LostItemAdapter
    private val lostItemList = ArrayList<LostItemData>()

    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var database : DatabaseReference

    private lateinit var listener : ValueEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)

        }

        // need to figure out how to use the currentUser's info (name, etc.) para sa display like "Hello, currentUser's name"

//        firebaseAuth = FirebaseAuth.getInstance()
//
//        val user = firebaseAuth.currentUser?.let {
//            val name = it.displayName.toString()
//        }
//
//        binding.tvHelloUser.text = "Hello, $user"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.lostitemsrecycler.layoutManager = LinearLayoutManager(requireContext())
        lostItemAdapter = LostItemAdapter(requireContext(), lostItemList)
        binding.lostitemsrecycler.adapter = lostItemAdapter

        // Setting up database reference
        firebaseAuth = FirebaseAuth.getInstance()

        val userId = firebaseAuth.currentUser?.uid
        database = FirebaseDatabase.getInstance().reference
        database = database.child("Users Info").child("users").child(userId.toString()).child("username")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val username = snapshot.value as String

                binding.tvHelloUser.text = "Hello $username"
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }

        database.addValueEventListener(listener)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Remove the value event listener
        database.removeEventListener(listener)
    }

    private fun getUserNameForUser() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

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
            homeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}