package com.bernaferrari.emojislider

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.bernaferrari.emojislider2.EmojiHelper
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

        val emojiHelper = EmojiHelper(this)

//        spring.emojiHelper = emojiHelper

        spring.sliderParticleSystem = slider_particle_system

        seek2.emojiHelper = emojiHelper
        seek2.sliderParticleSystem = slider_particle_system

//        seek5.emojiHelper = emojiHelper
//        seek5.sliderParticleSystem = slider_particle_system
    }
}
