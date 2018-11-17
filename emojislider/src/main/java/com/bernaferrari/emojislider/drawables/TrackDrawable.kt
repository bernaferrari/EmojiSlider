package com.bernaferrari.emojislider.drawables

import android.graphics.*
import android.graphics.Shader.TileMode

/**
 * This contains the track, track background, progress and respective colors.
 * Most of the other components and drawables are dependent on [TrackDrawable]'s dimensions.
 */
class TrackDrawable : GenericDrawableCallback() {

    internal val trackColor = Paint(1)
    private val progressGradient = Paint(1)

    private val barRect = RectF()
    var percentProgress = 0.90f

    internal var colorStart: Int = 0
        set(value) {
            if (field == value) return
            field = value
            updateShader(bounds)
        }

    internal var colorEnd: Int = 0
        set(value) {
            if (field == value) return
            field = value
            updateShader(bounds)
        }

    private var radius: Float = 0f
    internal var totalHeight: Int = 0
    internal var trackHeight: Float = 0f

    fun setTrackHeight(size: Float) {
        radius = size / 2
        trackHeight = size
        invalidateSelf()
    }

    override fun draw(canvas: Canvas) {

        canvas.save()
        canvas.translate(bounds.left.toFloat(), bounds.top.toFloat())

        barRect.set(
            0f,
            bounds.height() / 2f - trackHeight / 2,
            bounds.width().toFloat(),
            bounds.height() / 2f + trackHeight / 2
        )

        canvas.drawRoundRect(barRect, radius, radius, trackColor)

        barRect.set(
            0f,
            bounds.height() / 2f - trackHeight / 2,
            percentProgress * bounds.width(),
            bounds.height() / 2f + trackHeight / 2
        )

        canvas.drawRoundRect(barRect, radius, radius, progressGradient)
        canvas.restore()
    }

    override fun onBoundsChange(rect: Rect) = updateShader(rect)

    override fun getIntrinsicHeight(): Int = totalHeight

    private fun updateShader(rect: Rect) {
        progressGradient.shader = LinearGradient(
            0.0f,
            rect.exactCenterY(),
            rect.width().toFloat(),
            rect.exactCenterY(),
            colorStart,
            colorEnd,
            TileMode.CLAMP
        )
    }

    override fun setAlpha(alpha: Int) {
        this.progressGradient.alpha = alpha
        this.trackColor.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter) {
        this.progressGradient.colorFilter = colorFilter
        this.trackColor.colorFilter = colorFilter
    }
}
