package com.example.ifound

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.ifound.databinding.ActivityFoundItemPageBinding

class FoundItemPageActivity : AppCompatActivity() {

    private lateinit var binding : ActivityFoundItemPageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFoundItemPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val foundItem = intent.getParcelableExtra("FoundItemData") as FoundItemData?

        if (foundItem != null) {
            binding.tvItemNameFoundItem.text = foundItem.name
            binding.tvDateFound.text = foundItem.date
            binding.tvItemType.text = foundItem.category

            Glide.with(this)
                .load(foundItem.image)
                .into(binding.ivItemImg)
        }

        binding.btnEdit.setOnClickListener {
            val intent = Intent(this, FoundItemFormActivity::class.java)
            intent.putExtra("FoundItemData", foundItem)
            intent.putExtra("PageMode", FoundItemFormActivity.PageMode.EDIT)
            startActivity(intent)
        }


        binding.btnAddPhoto.setOnClickListener {
        ClaimRequestFragment(this, foundItem).show(supportFragmentManager, "ClaimingVerification")
        }

    }
}