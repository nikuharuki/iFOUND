package com.example.ifound

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.ifound.databinding.ActivityLogsAdminBinding
import com.example.ifound.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth

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

    private lateinit var binding : FragmentProfileBinding

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
            val intent = Intent(this@ProfileFragment.requireContext(), LogsAdminActivity::class.java)
            intent.putExtra("PageMode", LogsAdminActivity.PageMode.LOGS)
            startActivity(intent)
        }

        binding.btnSignOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            val intent = Intent(this@ProfileFragment.requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish() //requireActivity?? so main ang mafifinish
        }

        return binding.root
    }

    private fun isUserAnAdmin() : Boolean{
        val firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser

        if (currentUser?.email == "202101382@iacademy.edu.ph") {
            return true
        }

        return false
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment fragment_profile.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}