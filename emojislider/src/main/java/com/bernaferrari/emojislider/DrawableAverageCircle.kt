package com.bernaferrari.emojislider

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable

class DrawableAverageCircle(context: Context) : Drawable() {
    private val ringThickness: Int =
        context.resources.getDimensionPixelSize(R.dimen.slider_handle_ring_thickness)
    private val averagePaint = Paint(1)
    internal var radius: Float =
        context.resources.getDimension(R.dimen.slider_sticker_slider_vote_average_handle_size) / 2
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

    override fun getIntrinsicHeight(): Int = (radius * 2).toInt()

    override fun getIntrinsicWidth(): Int = (radius * 2).toInt()

    override fun setAlpha(i: Int) {
        this.averagePaint.alpha = i
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        this.averagePaint.colorFilter = colorFilter
    }
}