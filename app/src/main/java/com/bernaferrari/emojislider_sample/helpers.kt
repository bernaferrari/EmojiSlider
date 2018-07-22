package com.bernaferrari.emojislider_sample

import android.content.Context
import android.text.SpannableString

fun generateThumb(context: Context, text: String, size: Int): textDrawable {
    return textDrawable(
        context,
        context.getWidthPixels()
    ).apply {
        this.setSpannable(SpannableString(text))
        this.setTextSize(context.resources.getDimensionPixelSize(size).toFloat())
    }
}


fun Context.getWidthPixels(): Int = this.resources.displayMetrics.widthPixels