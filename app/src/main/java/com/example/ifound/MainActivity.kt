package com.example.ifound

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.ifound.databinding.ActivityMainBinding
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable


class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bottomNavView = findViewById<BottomNavigationView>(R.id.bottom_nav_view)
        bottomNavView.menu.getItem(1).isEnabled = false

        binding.floatingActionButton.setOnClickListener {
            val intent = Intent(this@MainActivity, LostItemFormActivity::class.java)
            startActivity(intent)
        }


        //Corner radius
//        val radius = resources.getDimension(R.dimen.default_corner_radius)
//        val bottomAppBar = findViewById<BottomAppBar>(R.id.bottom_app_bar)
//
//        val bottomBarBackground = bottomAppBar.background as MaterialShapeDrawable
//        bottomBarBackground.shapeAppearanceModel = bottomBarBackground.shapeAppearanceModel
//            .toBuilder()
//            .setTopRightCorner(CornerFamily.ROUNDED, radius)
//            .setTopLeftCorner(CornerFamily.ROUNDED, radius)
//            .build()


    }
}