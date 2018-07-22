package com.bernaferrari.emojislider

interface TrackingTouch {
    fun onStartTrackingTouch()
    fun onStopTrackingTouch()
    fun onProgressChanged(progress: Int)
    fun showPopupWindow(finalPosition: Int)
}