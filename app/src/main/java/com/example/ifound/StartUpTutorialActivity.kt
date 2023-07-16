package com.example.ifound

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.example.ifound.databinding.ActivityStartUpTutorialBinding
import com.google.firebase.auth.FirebaseAuth

class StartUpTutorialActivity : AppCompatActivity(), ViewPager.OnPageChangeListener{

    private lateinit var binding : ActivityStartUpTutorialBinding

    private lateinit var viewPager : ViewPager
    private lateinit var dotsLayout: LinearLayout
    private lateinit var dots : Array<TextView?>
    private lateinit var animation : Animation

    private var currentPosition : Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartUpTutorialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewPager = binding.slider
        dotsLayout = binding.dots
        val images = listOf(R.drawable.lost_tutorial, R.drawable.found_tutorial)


        val sliderAdapter = SliderAdapter(this, images)
        viewPager.adapter = sliderAdapter

        addDots(0)
        viewPager.addOnPageChangeListener(this)

        binding.btnStart.visibility = View.GONE

        binding.btnStart.setOnClickListener {
            val firebaseAuth = FirebaseAuth.getInstance()
            val user = firebaseAuth.currentUser
            intent = Intent(this@StartUpTutorialActivity, MainActivity::class.java)
            intent.putExtra("user", user)
            startActivity(intent)
            finish()
        }
    }

    private fun addDots(position : Int) {
        dotsLayout.removeAllViews()
        dots = arrayOfNulls<TextView?>(2)

        for (i in dots.indices) {
            dots[i] = TextView(this)
            dots[i]?.text = Html.fromHtml("&#8226;")
            dots[i]?.textSize = 35f
            dotsLayout.addView(dots[i])
        }

        if (dots.isNotEmpty()) {
            for (i in dots.indices) {
                if (i == position) {
                    dots[i]?.setTextColor(ContextCompat.getColor(this@StartUpTutorialActivity, R.color.blue))
                } else {
                    dots[i]?.setTextColor(ContextCompat.getColor(this@StartUpTutorialActivity, R.color.light_blue_600))
                }
            }
        }
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        // Implement your custom logic here, if needed
    }

    override fun onPageSelected(position: Int) {
        addDots(position)
        currentPosition = position

        if (position == 1) {
            animation = AnimationUtils.loadAnimation(this, R.anim.bottom_anim)
            binding.btnStart.animation = animation
            binding.btnStart.visibility = View.VISIBLE
        } else {
            binding.btnStart.visibility = View.GONE
        }
    }

    override fun onPageScrollStateChanged(state: Int) {
        // Implement your custom logic here, if needed
    }

}