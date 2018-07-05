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
        seek1.emojiHelper = emojiHelper
        seek1.sliderParticleSystem = slider_particle_system

        seek2.gradientColors(1, 2)

        seek2.emojiHelper = emojiHelper
        seek2.sliderParticleSystem = slider_particle_system

        seek3.emojiHelper = emojiHelper
        seek3.sliderParticleSystem = slider_particle_system

        seek4.emojiHelper = emojiHelper
        seek4.sliderParticleSystem = slider_particle_system

        seek5.emojiHelper = emojiHelper
        seek5.sliderParticleSystem = slider_particle_system
    }
}
