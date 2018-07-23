package com.bernaferrari.emojislider

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.view.Choreographer
import android.view.Choreographer.FrameCallback

class FlyingEmoji(context: Context) : Drawable(), FrameCallback {
    private val particleMinSize: Int =
        context.resources.getDimensionPixelSize(R.dimen.slider_particle_system_particle_min_size)
    private val particleMaxSize: Int =
        context.resources.getDimensionPixelSize(R.dimen.slider_particle_system_particle_max_size)
    private val particleAnchorOffset: Int =
        context.resources.getDimensionPixelSize(R.dimen.slider_particle_system_anchor_offset)
    private val trackingList = mutableListOf<Tracking>()
    private val pendingList = mutableListOf<Tracking>()
    private val rect = Rect()
    private val textPaint = TextPaint(1)
    private var emojiSize: Float = 0f
    private var isTracking: Boolean = false
    private var previousTime: Long = 0
    private var tracking: Tracking? = null

    enum class Direction {
        UP, DOWN
    }

    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT

    fun progressStarted(
        emoji: String,
        direction: Direction,
        paddingLeft: Float,
        paddingTop: Float
    ) {

        tracking = Tracking(emoji).also {
            it.paddingLeft = paddingLeft
            it.paddingTop = paddingTop
            it.emojiSize = emojiSize
            it.direction = direction
        }

        if (!isTracking) {
            isTracking = true
            doFrame(System.currentTimeMillis())
        }
    }

    fun onProgressChanged(percent: Float, paddingLeft: Float, paddingTop: Float) {
        tracking?.let {
            it.paddingLeft = paddingLeft
            it.paddingTop = paddingTop
            it.emojiSize = particleMinSize + percent * (particleMaxSize - particleMinSize)
        }

        invalidateSelf()
    }

    private fun drawToCanvas(canvas: Canvas, tracking: Tracking) {

        textPaint.textSize = tracking.emojiSize
        textPaint.getTextBounds(
            tracking.mainEmoji,
            0,
            tracking.mainEmoji.length,
            rect
        )

        canvas.drawText(
            tracking.mainEmoji,
            tracking.paddingLeft - rect.width() / 2f,
            tracking.paddingTop + tracking.breathing - rect.height() / 2f,
            textPaint
        )
    }

    fun onStopTrackingTouch() {
        if (tracking != null) {
            trackingList.add(0, tracking!!)
            tracking = null
        }
    }

    override fun doFrame(j: Long) {

        tracking?.breathing =
                ((System.currentTimeMillis() / 8).toDoubleRadiansSin() * 16.0 - particleAnchorOffset).toFloat()

        val currentTimeMillis = System.currentTimeMillis()
        if (previousTime != 0L) {
            val f = (currentTimeMillis - previousTime) / 1000.0f
            for (i in trackingList.indices) {
                trackingList[i].let {

                    it.dismissPadding += 1000f * f

                    when (it.direction) {
                        Direction.UP -> it.paddingTop -= it.dismissPadding * f
                        Direction.DOWN -> it.paddingTop += it.dismissPadding * f
                    }

                    if (it.paddingTop < bounds.top - 2f * it.emojiSize || it.emojiSize < 0) {
                        pendingList.add(it)
                    }
                }
            }
            if (!pendingList.isEmpty()) {
                trackingList.removeAll(pendingList)
                pendingList.clear()
            }
        }

        previousTime = currentTimeMillis
        if (tracking == null && trackingList.isEmpty()) {
            isTracking = false
        } else {
            Choreographer.getInstance().postFrameCallback(this)
        }
        invalidateSelf()
    }

    override fun draw(canvas: Canvas) {
        if (this.tracking != null) {
            drawToCanvas(canvas, this.tracking!!)
        }
        for (i in this.trackingList.indices) {
            drawToCanvas(canvas, this.trackingList[i])
        }
    }

    override fun setAlpha(i: Int) {
        this.textPaint.alpha = i
        invalidateSelf()
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        this.textPaint.colorFilter = colorFilter
        invalidateSelf()
    }

    private class Tracking(val mainEmoji: String) {
        var paddingLeft: Float = 0f
        var paddingTop: Float = 0f
        var breathing: Float = 0f
        var emojiSize: Float = 0f
        var dismissPadding: Float = 0f
        var direction: Direction = Direction.UP
    }

    private fun Double.toRadians() = Math.toRadians(this)

    private fun Double.toSin() = Math.sin(this)

    private fun Long.toDoubleRadiansSin() = this.toDouble().toRadians().toSin()
}
