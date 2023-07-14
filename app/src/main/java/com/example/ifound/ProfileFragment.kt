package com.example.ifound

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.ifound.databinding.ActivityChangeEmailBinding
import com.example.ifound.databinding.ActivityChangePasswordBinding
import com.example.ifound.databinding.ActivityChangeUsernameBinding
import com.example.ifound.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
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


    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        requireActivity().menuInflater.inflate(R.menu.settings_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return handleMenuItemClick(item)
    }
}
