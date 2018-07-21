package com.bernaferrari.emojislider2

interface TrackingTouch {
    fun onStartTrackingTouch()
    fun onStopTrackingTouch()
    fun onProgressChanged(progress: Int)
}