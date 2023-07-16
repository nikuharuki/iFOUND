package com.example.ifound

import android.content.Intent
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import android.widget.SearchView.OnQueryTextListener
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ifound.databinding.ActivityLostAndFoundItemFeedBinding
import java.util.Locale

class LostAndFoundItemFeed : AppCompatActivity() {

    enum class PageMode {
        FOUND,
        LOST
    }

    private lateinit var binding : ActivityLostAndFoundItemFeedBinding
    private lateinit var lostItemAdapter: LostItemAdapter
    private lateinit var foundItemAdapter: FoundItemAdapter

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
//    private val lostItemList = ArrayList<LostItemData>()
//    private val foundItemList = ArrayList<FoundItemData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLostAndFoundItemFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pageMode = intent.getSerializableExtra("PageMode") as PageMode
        foundItemAdapter = FoundItemAdapter(this, foundItemList)
        lostItemAdapter = LostItemAdapter(this, lostItemList)

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
        searchView = binding.searchBar
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
               filterList(newText)
                return true
            }
        })
    }

    private fun filterList(query: String?) {
        if (query != null) {
            val lostItemFilteredList = ArrayList<LostItemData>()
            val foundItemFilteredList = ArrayList<FoundItemData>()

            for (i in lostItemList) {
                if (i.name!!.lowercase(Locale.ROOT).contains(query.lowercase(Locale.ROOT))) {
                    lostItemFilteredList.add(i)
                }
            }

            for (i in foundItemList) {
                if (i.name!!.lowercase(Locale.ROOT).contains(query.lowercase(Locale.ROOT))) {
                    foundItemFilteredList.add(i)
                }
            }

            if (lostItemFilteredList.isEmpty()) {
                Toast.makeText(this, "No Data found", Toast.LENGTH_SHORT).show()
            } else {
                lostItemAdapter.setFilteredList(lostItemFilteredList)
            }

            if (foundItemFilteredList.isEmpty()) {
                Toast.makeText(this, "No Data found", Toast.LENGTH_SHORT).show()
            } else {
                foundItemAdapter.setFilteredList(foundItemFilteredList)
            }
        }
    }

}