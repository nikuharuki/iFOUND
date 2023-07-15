package com.example.ifound

import android.content.Intent
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
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
    private lateinit var foundItemAdapter: FoundItemAdapter

    private lateinit var recyclerView: RecyclerView

//    private val lostItemList = ArrayList<LostItemData>()
//    private val foundItemList = ArrayList<FoundItemData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLostAndFoundItemFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pageMode = intent.getSerializableExtra("PageMode") as PageMode

        // Checks kung anong cinlick na recycler view from HomeFragment
        if (pageMode == PageMode.FOUND) {
            recyclerView = binding.recyclerView
            recyclerView.layoutManager = GridLayoutManager(this,2, GridLayoutManager.VERTICAL,false)
            foundItemAdapter = FoundItemAdapter(this, foundItemList)
            recyclerView.adapter = foundItemAdapter

            foundItemAdapter.onItemClick = {
                val intent = Intent(this@LostAndFoundItemFeed, FoundItemPageActivity::class.java)
                intent.putExtra("FoundItemData", it)
                startActivity(intent)
            }

        } else if (pageMode == PageMode.LOST){
            recyclerView = binding.recyclerView
            recyclerView.layoutManager = GridLayoutManager(this,2, GridLayoutManager.VERTICAL,false)
            lostItemAdapter = LostItemAdapter(this, lostItemList)
            recyclerView.adapter = lostItemAdapter

            lostItemAdapter.onItemClick = {
                val intent = Intent(this@LostAndFoundItemFeed, LostItemPageActivity::class.java)
                intent.putExtra("LostItemData", it)
                startActivity(intent)
            }


        }
    }
}