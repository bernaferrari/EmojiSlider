package com.bernaferrari.emojislider

import android.content.Context
import android.text.SpannableString


fun generateThumb(context: Context, text: String, size: Int): textDrawable {
    return textDrawable(
        context,
        context.getWidthPixels()
    ).apply {
        setSpannable(SpannableString(text))
        setTextSize(context.resources.getDimensionPixelSize(size).toFloat())
    }
}

fun Context.getWidthPixels(): Int = this.resources.displayMetrics.widthPixels
