package com.bernaferrari.emojislider.drawables

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.graphics.drawable.Drawable.Callback

class BitmapDrawable : Drawable(), Callback {

    var drawable: Drawable? = null
    internal var diameter: Float = 0f

    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT

    override fun scheduleDrawable(drawable: Drawable, runnable: Runnable, j: Long) = Unit

    override fun setAlpha(i: Int) = Unit

    override fun setColorFilter(colorFilter: ColorFilter?) = Unit

    override fun unscheduleDrawable(drawable: Drawable, runnable: Runnable) = Unit

    internal fun generateDrawable(bitmap: Bitmap) {
        this.drawable = UnscaledBitmapDrawable(bitmap)
        this.drawable!!.callback = this
        this.drawable!!.setBounds(0, 0, bitmap.width, bitmap.height)
        invalidateSelf()
    }

    override fun invalidateDrawable(drawable: Drawable) = invalidateSelf()

    override fun draw(canvas: Canvas) {

        if (this.drawable != null && this.diameter > 0.0f) {
            val max = Math.max(
                this.diameter / this.drawable!!.intrinsicWidth,
                this.diameter / this.drawable!!.intrinsicHeight
            )

            canvas.save()
            canvas.translate(bounds.left.toFloat(), bounds.top.toFloat())
            canvas.scale(max, max)
            this.drawable!!.draw(canvas)
            canvas.restore()
        }
    }

    override fun getIntrinsicHeight(): Int = diameter.toInt()

    override fun getIntrinsicWidth(): Int = diameter.toInt()
}