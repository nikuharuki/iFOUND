package com.example.ifound

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ifound.databinding.ActivityMainBinding
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable


class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private val rotateOpen: Animation by lazy {AnimationUtils.loadAnimation(this, R.anim.rotate_open_animation)  }
    private val rotateClose: Animation by lazy {AnimationUtils.loadAnimation(this, R.anim.rotate_close_animation)  }
    private val fromBottom: Animation by lazy {AnimationUtils.loadAnimation(this, R.anim.from_bottom_animation)  }
    private val toBottom: Animation by lazy {AnimationUtils.loadAnimation(this, R.anim.to_bottom_animation)  }

    private var clicked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bottomNavView = findViewById<BottomNavigationView>(R.id.bottom_nav_view)
        bottomNavView.menu.getItem(1).isEnabled = false

        binding.floatingActionButton.setOnClickListener {

            onAddButtonClicked()
        }
        binding.fabLostForm.setOnClickListener {
            val intent = Intent(this@MainActivity, LostItemFormActivity::class.java)
            startActivity(intent)

        }
        binding.fabFoundForm.setOnClickListener {
            val intent = Intent(this@MainActivity, FoundItemFormActivity::class.java)
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
    private fun onAddButtonClicked(){
        setVisibility(clicked)
        setAnimation(clicked)
        clicked = !clicked
    }
    private fun setVisibility(clicked: Boolean){
        if (!clicked){
            binding.fabLostForm.visibility = View.VISIBLE
            binding.fabFoundForm.visibility = View.VISIBLE
        }else{
            binding.fabLostForm.visibility = View.INVISIBLE
            binding.fabFoundForm.visibility = View.INVISIBLE
        }

    }
    private fun setAnimation(clicked: Boolean){
        if(!clicked){
            binding.fabLostForm.startAnimation(fromBottom)
            binding.fabFoundForm.startAnimation(fromBottom)
            binding.floatingActionButton.startAnimation(rotateOpen)
        }else{
            binding.fabLostForm.startAnimation(toBottom)
            binding.fabFoundForm.startAnimation(toBottom)
            binding.floatingActionButton.startAnimation(rotateClose)
        }
    }
    private fun setClickable(clicked: Boolean){
        if(!clicked){
            binding.fabLostForm.isClickable = false
            binding.fabFoundForm.isClickable = false
        }else{
            binding.fabLostForm.isClickable = true
            binding.fabFoundForm.isClickable = true
        }
    }
}