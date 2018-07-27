package com.bernaferrari.emojislider.drawables

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable

class BitmapDrawable : GenericDrawableCallback() {

    var drawable: Drawable? = null
    internal var diameter: Float = 0f

    internal fun generateDrawable(bitmap: Bitmap) {
        this.drawable = UnscaledBitmapDrawable(bitmap)
        this.drawable!!.callback = this
        this.drawable!!.setBounds(0, 0, bitmap.width, bitmap.height)
        invalidateSelf()
    }

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
