package com.bernaferrari.emojisliderSample

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

        spring.sliderParticleSystem = slider_particle_system
        spring1.sliderParticleSystem = slider_particle_system
        spring2.sliderParticleSystem = slider_particle_system
        spring3.sliderParticleSystem = slider_particle_system
        spring4.sliderParticleSystem = slider_particle_system
        spring5.sliderParticleSystem = slider_particle_system
        seek2.sliderParticleSystem = slider_particle_system
    }
}
