package com.bernaferrari.emojislider_sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import com.cpiz.android.bubbleview.BubblePopupWindow
import com.cpiz.android.bubbleview.BubbleStyle
import com.cpiz.android.bubbleview.BubbleTextView
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Logger.addLogAdapter(AndroidLogAdapter())

        setContentView(R.layout.activity_main)

        val displayMetrics = resources.displayMetrics
        Logger.d("Density is: " + displayMetrics.density)

        spring.sliderParticleSystem = slider_particle_system
        seek2.sliderParticleSystem = slider_particle_system


        bubbleTextView.setOnClickListener {
            val rootView = LayoutInflater.from(this).inflate(R.layout.test, null) as BubbleTextView
            val window = BubblePopupWindow(rootView, rootView)
            window.setCancelOnTouch(true)
            window.setCancelOnTouchOutside(true)
            window.setCancelOnLater(2500)
            if (hasWindowFocus()) {
                window.showArrowTo(spring, BubbleStyle.ArrowDirection.Up, -66)
            }
        }
    }
}
