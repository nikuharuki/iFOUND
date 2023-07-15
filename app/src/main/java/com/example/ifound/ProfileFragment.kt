package com.example.ifound

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ifound.databinding.FragmentHomeBinding
import com.example.ifound.databinding.FragmentProfileBinding
import com.example.ifound.databinding.ActivityLogsAdminBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import android.view.*
import android.widget.PopupMenu
import androidx.cardview.widget.CardView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentProfileBinding

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
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        getUserName()

        val isUserAnAdmin = isUserAnAdmin()
        if (isUserAnAdmin) {
            binding.btnLogsAdmin.visibility = View.VISIBLE
            binding.btnClaimRequest.visibility = View.VISIBLE
            binding.btnApproval.visibility = View.VISIBLE
        } else {
            binding.btnLogsAdmin.visibility = View.GONE
            binding.btnClaimRequest.visibility = View.GONE
            binding.btnApproval.visibility = View.GONE
        }


        binding.btnLogsAdmin.setOnClickListener {
            val intent =
                Intent(this@ProfileFragment.requireContext(), LogsAdminActivity::class.java)
            intent.putExtra("PageMode", LogsAdminActivity.PageMode.LOGS)
            startActivity(intent)
        }

        binding.btnClaimRequest.setOnClickListener {
            val intent = Intent(this@ProfileFragment.requireContext(), LogsAdminActivity::class.java)
            intent.putExtra("PageMode", LogsAdminActivity.PageMode.CLAIM)
            startActivity(intent)
        }

        binding.btnSignOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            val intent = Intent(this@ProfileFragment.requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish() //requireActivity?? so main ang mafifinish
        }
        binding.cvStarPoints.setOnClickListener {
            val intent = Intent(this@ProfileFragment.requireActivity(), RewardsActivity::class.java)
            startActivity(intent)
        }

        return binding.root

    }


    private fun getUserName() {
        val firebaseAuth = FirebaseAuth.getInstance()
        val userId = firebaseAuth.currentUser?.uid
        val databaseReference =
            FirebaseDatabase.getInstance("https://ifound-731c1-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Users").child(userId!!)

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val userName = snapshot.child("Name").getValue(String::class.java)
                    val email = snapshot.child("Email").getValue(String::class.java)

                    binding.tvUsername.text = userName
                    binding.tvEmailUser.text = email
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun isUserAnAdmin(): Boolean {
        val firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser

        if (currentUser?.email == "202101382@iacademy.edu.ph") {
            return true
        }
        return false
    }

    private fun handleMenuItemClick(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.i_username -> {
                val intent = Intent(requireContext(), ChangeUsernameActivity::class.java)
                startActivity(intent)
                true
            }

            R.id.i_email -> {
                val intent = Intent(requireContext(), ChangeEmailActivity::class.java)
                startActivity(intent)
                true
            }

            R.id.i_password -> {
                val intent = Intent(requireContext(), ChangePasswordActivity::class.java)
                startActivity(intent)
                true
            }

            else -> false
        }
    }


    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        requireActivity().menuInflater.inflate(R.menu.settings_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return handleMenuItemClick(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerForContextMenu(binding.ibtnSettings) // Register the ImageButton for the context menu

        binding.ibtnSettings.setOnClickListener {
            showPopupMenu(it)
        }
    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.menuInflater.inflate(R.menu.settings_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item ->
            handleMenuItemClick(item)
        }
        popupMenu.show()
    }
}