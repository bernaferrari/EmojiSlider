package com.bernaferrari.emojislider

import android.graphics.*
import android.graphics.Shader.TileMode
import android.graphics.drawable.Drawable
import android.graphics.drawable.Drawable.Callback

class DrawableBars : Drawable(), Callback {
    internal val progressBackgroundPaint = Paint(1)

    private val gradientPaint = Paint(1)
    private val barRect = RectF()

    var percentProgress = 0.90f

    internal var colorStart: Int = 0
    internal var colorEnd: Int = 0
    private var radius: Float = 0f
    internal var sliderHeight: Int = 0
    private var totalHeight: Float = 0f

    fun setGradientBackground(gradientBackground: Int) {
        this.progressBackgroundPaint.color = gradientBackground
    }

    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT
    override fun scheduleDrawable(drawable: Drawable, runnable: Runnable, j: Long) = Unit
    override fun unscheduleDrawable(drawable: Drawable, runnable: Runnable) = Unit

    fun configureHeight(i: Int) {
        val f = i.toFloat()
        this.radius = f / 2.0f
        this.totalHeight = f
        invalidateSelf()
    }

    override fun draw(canvas: Canvas) {

        canvas.save()
        canvas.translate(bounds.left.toFloat(), bounds.top.toFloat())

        var height2 = bounds.height() / 2f
        this.barRect.set(
            0f,
            height2 - this.totalHeight / 2,
            bounds.width().toFloat(),
            height2 + this.totalHeight / 2
        )

        // draw grey rect (__________)
        canvas.drawRoundRect(this.barRect, this.radius, this.radius, this.progressBackgroundPaint)

        val width: Float = this.percentProgress * (bounds.width().toFloat())
        height2 = bounds.height().toFloat() / 2.0f

        this.barRect.set(
            0.0f,
            height2 - this.totalHeight / 2.0f,
            width,
            height2 + this.totalHeight / 2.0f
        )

        canvas.drawRoundRect(this.barRect, this.radius, this.radius, this.gradientPaint)
        canvas.restore()
    }

    override fun getIntrinsicHeight(): Int {
        return this.sliderHeight
    }

    override fun invalidateDrawable(drawable: Drawable) {
        invalidateSelf()
    }

    override fun onBoundsChange(rect: Rect) {
        updateShader(rect)
    }

    private fun updateShader(rect: Rect) {
        gradientPaint.shader = LinearGradient(
            0.0f,
            rect.exactCenterY(),
            rect.width().toFloat(),
            rect.exactCenterY(),
            this.colorStart,
            this.colorEnd,
            TileMode.CLAMP
        )
    }

    override fun setAlpha(i: Int) {
        this.progressBackgroundPaint.alpha = i
        this.gradientPaint.alpha = i
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        this.progressBackgroundPaint.colorFilter = colorFilter
        this.gradientPaint.colorFilter = colorFilter
    }
}