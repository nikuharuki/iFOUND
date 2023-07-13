package com.example.ifound

import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.ifound.databinding.ActivityLostAndFoundItemFeedBinding

class LostAndFoundItemFeed : AppCompatActivity() {

    enum class PageMode {
        FOUND,
        LOST
    }

    private lateinit var binding : ActivityLostAndFoundItemFeedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLostAndFoundItemFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pageMode = intent.getSerializableExtra("PageMode") as PageMode

        // Checks kung anong cinlick na recycler view from HomeFragment
        if (pageMode == PageMode.FOUND) {
            binding.rvFoundItems.visibility = View.VISIBLE
            binding.rvLostItems.visibility = View.GONE
        } else if (pageMode == PageMode.LOST){
            binding.rvFoundItems.visibility = View.GONE
            binding.rvLostItems.visibility = View.VISIBLE
        }


    }
}