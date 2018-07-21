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

        val displayMetrics = resources.displayMetrics
        Logger.d("Density is: " + displayMetrics.density)

        val emojiHelper = EmojiHelper(this)

//        spring.emojiHelper = emojiHelper
        spring.sliderParticleSystem = slider_particle_system

//        val materialdialog = MaterialDialog.Builder(this)
//            .customView(R.layout.activity_main, false)
//            .build()
//        materialdialog.show()
//
//        val emojiHelper2 = EmojiHelper(this)
//
//        materialdialog.seek1.setBackgroundView(materialdialog.slider_particle_system, emojiHelper2)
//
//        materialdialog.seek2.emojiHelper = emojiHelper2
//        materialdialog.seek2.sliderParticleSystem = materialdialog.slider_particle_system
//
//        materialdialog.seek3.emojiHelper = emojiHelper2
//        materialdialog.seek3.sliderParticleSystem = materialdialog.slider_particle_system
//
//        materialdialog.seek4.emojiHelper = emojiHelper2
//        materialdialog.seek4.sliderParticleSystem = materialdialog.slider_particle_system
//
//        materialdialog.seek5.emojiHelper = emojiHelper2
//        materialdialog.seek5.sliderParticleSystem = materialdialog.slider_particle_system

//        seek1.emojiHelper = emojiHelper
//        seek1.sliderParticleSystem = slider_particle_system

//        seek2.gradientColors(1, 2)
        seek2.emojiHelper = emojiHelper
        seek2.sliderParticleSystem = slider_particle_system

        seek5.emojiHelper = emojiHelper
        seek5.sliderParticleSystem = slider_particle_system
    }
}
