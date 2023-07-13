package com.example.ifound

import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ifound.databinding.ActivityLostAndFoundItemFeedBinding

class LostAndFoundItemFeed : AppCompatActivity() {

    enum class PageMode {
        FOUND,
        LOST
    }

    private lateinit var binding : ActivityLostAndFoundItemFeedBinding
    private lateinit var lostItemAdapter: LostItemAdapter
    private lateinit var lostRv: RecyclerView
    private lateinit var foundRv: RecyclerView
    private val lostItemList = ArrayList<LostItemData>()
    private val foundItemList = ArrayList<FoundItemData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLostAndFoundItemFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pageMode = intent.getSerializableExtra("PageMode") as PageMode

        // Checks kung anong cinlick na recycler view from HomeFragment
        if (pageMode == PageMode.FOUND) {
            binding.rvFoundItems.visibility = View.VISIBLE
            binding.rvLostItems.visibility = View.GONE
            foundRv = binding.rvFoundItems




        } else if (pageMode == PageMode.LOST){
            binding.rvFoundItems.visibility = View.GONE
            binding.rvLostItems.visibility = View.VISIBLE
            lostRv = binding.rvLostItems
            lostRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            lostRv.setHasFixedSize(true)
            lostItemAdapter = LostItemAdapter(this, lostItemList)
            lostRv.adapter = lostItemAdapter

        }


    }
}