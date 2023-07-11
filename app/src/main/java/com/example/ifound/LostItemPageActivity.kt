package com.example.ifound

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.ifound.databinding.ActivityLostItemPageBinding

// FoundItemPage =/= FoundItemFeed

class LostItemPageActivity() : AppCompatActivity() {

    private lateinit var binding: ActivityLostItemPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLostItemPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val lostItem = intent.getParcelableExtra("LostItemData") as LostItemData?

        if (lostItem != null) {
            binding.tvItemNameLostItem.text = lostItem.name
            binding.tvWhereLostItem.text = lostItem.date
            binding.tvRoomNumOrFloorLostItem.text = lostItem.location
            binding.tvNameLostItem.text = lostItem.submittedBy
            binding.tvEmailLostItem.text = lostItem.contact

            Glide.with(this)
                .load(lostItem.image)
                .into(binding.ivItemImg)
        }

        binding.btnEditButtonLostItem.setOnClickListener {
            val intent = Intent(this, LostItemFormActivity::class.java)
            intent.putExtra("LostItemData", lostItem)
            intent.putExtra("PageMode", LostItemFormActivity.PageMode.EDIT)
            startActivity(intent)
        }
    }
}