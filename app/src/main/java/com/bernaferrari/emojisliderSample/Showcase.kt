package com.bernaferrari.emojisliderSample

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.frag_main.*

class Showcase : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.frag_main, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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