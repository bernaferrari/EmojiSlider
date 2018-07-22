package com.bernaferrari.emojislider_sample

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable

class AverageCircleDrawable(context: Context) : Drawable() {
    private val ringThickness: Int =
        context.resources.getDimensionPixelSize(R.dimen.slider_handle_ring_thickness)
    private val myPaint = Paint(1)
    internal var radius = 0f
    internal var color_f20902b = -1
    internal var color_f20903c = 0

    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT

    override fun draw(canvas: Canvas) {
        val exactCenterX = bounds.exactCenterX()
        val exactCenterY = bounds.exactCenterY()

        this.myPaint.color = this.color_f20903c
        canvas.drawCircle(
            exactCenterX,
            exactCenterY,
            this.radius,
            this.myPaint
        )

        this.myPaint.color = this.color_f20902b
        canvas.drawCircle(
            exactCenterX,
            exactCenterY,
            this.radius - this.ringThickness.toFloat(),
            this.myPaint
        )
    }

    override fun getIntrinsicHeight(): Int = (this.radius * 2.0f).toInt()

    override fun getIntrinsicWidth(): Int = (this.radius * 2.0f).toInt()

    override fun setAlpha(i: Int) {
        myPaint.alpha = i
    }

    override fun setColorFilter(colorFilter: ColorFilter) {
        myPaint.colorFilter = colorFilter
    }
}