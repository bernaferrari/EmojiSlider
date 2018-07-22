package com.bernaferrari.emojislider_sample

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable

class AverageCircle(context: Context) : Drawable() {
    private val ringThickness: Int =
        context.resources.getDimensionPixelSize(R.dimen.slider_handle_ring_thickness)
    private val averagePaint = Paint(1)
    internal var radius: Float = 0.toFloat()
    internal var innerColor = -1
    internal var outerColor = 0

    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT

    override fun draw(canvas: Canvas) {
        val exactCenterX = bounds.exactCenterX()
        val exactCenterY = bounds.exactCenterY()
        this.averagePaint.color = this.outerColor
        canvas.drawCircle(exactCenterX, exactCenterY, this.radius, this.averagePaint)
        this.averagePaint.color = this.innerColor
        canvas.drawCircle(
            exactCenterX,
            exactCenterY,
            this.radius - this.ringThickness.toFloat(),
            this.averagePaint
        )
    }

    override fun getIntrinsicHeight(): Int {
        return (this.radius * 2.0f).toInt()
    }

    override fun getIntrinsicWidth(): Int {
        return (this.radius * 2.0f).toInt()
    }

    override fun setAlpha(i: Int) {
        this.averagePaint.alpha = i
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        this.averagePaint.colorFilter = colorFilter
    }
}