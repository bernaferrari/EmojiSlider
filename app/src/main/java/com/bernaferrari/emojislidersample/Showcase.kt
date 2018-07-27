package com.bernaferrari.emojislidersample

import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.android.synthetic.main.frag_main.*


class Showcase : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.frag_main, container, false)

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

        Glide.with(this)
            .asBitmap()
            .load(
                "https://scontent.fbfh2-1.fna.fbcdn.net" +
                        "/v/t1.0-9/14563570_10205302598764315_2795817981757247033_n.jpg" +
                        "?_nc_cat=0&oh=e5e866251c1ce98e9a3299bb30ed8d6d&oe=5BD56A64"
            )
            .into(object : SimpleTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    slider_ig.setResultDrawable(resource)
                    sliderv2.setResultDrawable(resource)
                    sliderv6.setResultDrawable(resource)
                }
            })
    }
}
