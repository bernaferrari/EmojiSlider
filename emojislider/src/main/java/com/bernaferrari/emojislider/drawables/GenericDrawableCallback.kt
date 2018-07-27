package com.bernaferrari.emojislider.drawables

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.graphics.drawable.Drawable.Callback

open class GenericDrawableCallback : Drawable(), Callback {

    override fun draw(canvas: Canvas) = Unit

    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT

    override fun scheduleDrawable(drawable: Drawable, runnable: Runnable, j: Long) = Unit

    override fun setAlpha(alpha: Int) = Unit

    override fun setColorFilter(colorFilter: ColorFilter) = Unit

    override fun unscheduleDrawable(drawable: Drawable, runnable: Runnable) = Unit

    override fun invalidateDrawable(drawable: Drawable) = invalidateSelf()

}
