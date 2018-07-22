package com.bernaferrari.emojislider_sample

import android.graphics.Color

object ColorHelper {

    fun getCorrectColor(color0: Int, color1: Int, percentage: Float): Int {
        val red = Color.red(color0)
        val green = Color.green(color0)
        val blue = Color.blue(color0)
        return Color.rgb(
            red + ((Color.red(color1) - red).toFloat() * percentage).toInt(),
            green + ((Color.green(color1) - green).toFloat() * percentage).toInt(),
            blue + ((Color.blue(color1) - blue).toFloat() * percentage).toInt()
        )
    }
}
