package com.bernaferrari.emojislider

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Logger.addLogAdapter(AndroidLogAdapter())

        setContentView(R.layout.activity_main)

        val emojiHelper = EmojiHelper(this)

        seek1.setBackgroundView(slider_particle_system, emojiHelper)

        seek2.gradientColors(0xffff00ff.toInt(), 0xff00ff00.toInt())

        seek2.emojiHelper = emojiHelper
        seek2.sliderParticleSystem = slider_particle_system

        seek3.emojiHelper = emojiHelper
        seek3.sliderParticleSystem = slider_particle_system

        seek4.setBackgroundView(slider_particle_system, emojiHelper)

        seek5.emojiHelper = emojiHelper
        seek5.sliderParticleSystem = slider_particle_system
    }
}
