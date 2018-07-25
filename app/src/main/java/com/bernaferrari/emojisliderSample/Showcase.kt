package com.bernaferrari.emojisliderSample

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.frag_main.*

class Showcase : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.frag_main, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sliderv1.sliderParticleSystem = slider_particle_system
        sliderv2.sliderParticleSystem = slider_particle_system
        sliderv3.sliderParticleSystem = slider_particle_system
        sliderv4.sliderParticleSystem = slider_particle_system
        sliderv5.sliderParticleSystem = slider_particle_system
        sliderv6.sliderParticleSystem = slider_particle_system

        sliderh1.sliderParticleSystem = slider_particle_system
        sliderh2.sliderParticleSystem = slider_particle_system
        sliderh3.sliderParticleSystem = slider_particle_system
        sliderh4.sliderParticleSystem = slider_particle_system

        slider_ig.sliderParticleSystem = slider_particle_system
        slider_seekbar.sliderParticleSystem = slider_particle_system
    }
}