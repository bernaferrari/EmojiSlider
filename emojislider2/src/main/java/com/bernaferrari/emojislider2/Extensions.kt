package com.bernaferrari.emojislider2

import android.content.Context
import android.graphics.Color
import android.text.SpannableString
import com.facebook.rebound.Spring
import com.facebook.rebound.SpringConfig

fun Spring.origamiConfig(tension: Double, friction: Double): Spring =
    this.setSpringConfig(SpringConfig.fromOrigamiTensionAndFriction(tension, friction))

fun DpToPx(context: Context, x: Float): Int {
    val scale = context.resources.displayMetrics.density
    return (x * scale + 0.5f).toInt()
}

fun Context.getWidthPixels(): Int = this.resources.displayMetrics.widthPixels

fun generateThumb(context: Context, text: String, size: Int): textDrawable {
    return textDrawable(
        context,
        context.getWidthPixels()
    ).apply {
        setSpannable(SpannableString(text))
        setTextSize(context.resources.getDimensionPixelSize(size).toFloat())
    }
}

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