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

class EmojiHelper(context: Context) : Drawable(), FrameCallback {
    private val particleMinSize: Int
    private val particleMaxSize: Int
    private val particleAnchorOffset: Int
    private val trackingList = mutableListOf<IDontKnow>()
    private val trackinglist2 = mutableListOf<IDontKnow>()
    private val rect = Rect()
    private val textpaint = TextPaint(1)
    var emoji = "😍"
    private var paddingLeft: Float = 0.toFloat()
    private var paddingTop: Float = 0.toFloat()
    private var emojiSize: Float = 0.toFloat()
    private var f20897l: Boolean = false
    private var previousTime: Long = 0
    private var tracking: IDontKnow? = null

    init {
        val resources = context.resources
        this.particleAnchorOffset =
                resources.getDimensionPixelSize(R.dimen.slider_particle_system_anchor_offset)
        this.particleMinSize =
                resources.getDimensionPixelSize(R.dimen.slider_particle_system_particle_min_size)
        this.particleMaxSize =
                resources.getDimensionPixelSize(R.dimen.slider_particle_system_particle_max_size)
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    fun progressStarted() {
        this.tracking = IDontKnow(this.emoji)
        this.tracking!!.paddingLeft = this.paddingLeft
        this.tracking!!.paddingTop = this.paddingTop
        this.tracking!!.emojiSize = this.emojiSize
        if (!this.f20897l) {
            this.f20897l = true
            doFrame(System.currentTimeMillis())
        }
    }

    fun updateProgress(percent: Float) {
        this.emojiSize = this.particleMinSize.toFloat() + percent *
                (this.particleMaxSize - this.particleMinSize).toFloat()
        this.tracking?.emojiSize = this.emojiSize
        invalidateSelf()
    }

    fun onProgressChanged(paddingLeft: Float, paddingTop: Float) {
        this.paddingLeft = paddingLeft
        this.paddingTop = paddingTop
        if (this.tracking != null) {
            this.tracking!!.paddingLeft = this.paddingLeft
            this.tracking!!.paddingTop = this.paddingTop
        }
        invalidateSelf()
    }

    private fun m9941a(canvas: Canvas, IDontKnow: IDontKnow) {
        this.textpaint.textSize = IDontKnow.emojiSize
        this.textpaint.getTextBounds(IDontKnow.mainEmoji, 0, IDontKnow.mainEmoji.length, this.rect)
        canvas.drawText(
            IDontKnow.mainEmoji,
            IDontKnow.paddingLeft - this.rect.width().toFloat() / 2.0f,
            IDontKnow.paddingTop + IDontKnow.naosei3 - this.rect.height().toFloat() / 2.0f,
            this.textpaint
        )
    }

    fun onStopTrackingTouch() {
        this.trackingList.add(0, this.tracking!!)
        this.tracking = null
    }

    override fun doFrame(j: Long) {
        if (this.tracking != null) {
            val toRadians = Math.toRadians((System.currentTimeMillis() / 8).toDouble())
            this.tracking!!.naosei3 =
                    (Math.sin(toRadians) * 16.0 - this.particleAnchorOffset.toDouble()).toFloat()
        }
        val currentTimeMillis = System.currentTimeMillis()
        if (this.previousTime != 0L) {
            val f = (currentTimeMillis - this.previousTime).toFloat() / 1000.0f
            for (i in this.trackingList.indices) {
                val c5187f = this.trackingList[i]
                c5187f.naosei5 += -1000.0f * f
                c5187f.paddingTop += c5187f.naosei5 * f
                if (c5187f.paddingTop < bounds.top.toFloat() - 2.0f * c5187f.emojiSize) {
                    this.trackinglist2.add(c5187f)
                }
            }
            if (!this.trackinglist2.isEmpty()) {
                this.trackingList.removeAll(this.trackinglist2)
                this.trackinglist2.clear()
            }
        }
        this.previousTime = currentTimeMillis
        if (this.tracking == null && this.trackingList.isEmpty()) {
            this.f20897l = false
        } else {
            Choreographer.getInstance().postFrameCallback(this)
        }
        invalidateSelf()
    }

    override fun draw(canvas: Canvas) {
        if (this.tracking != null) {
            m9941a(canvas, this.tracking!!)
        }
        for (i in this.trackingList.indices) {
            m9941a(canvas, this.trackingList[i])
        }
    }

    override fun setAlpha(i: Int) {
        this.textpaint.alpha = i
        invalidateSelf()
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        this.textpaint.colorFilter = colorFilter
        invalidateSelf()
    }

    inner class IDontKnow(val mainEmoji: String) {
        var paddingLeft: Float = 0f
        var paddingTop: Float = 0f
        var naosei3: Float = 0f
        var emojiSize: Float = 0f
        var naosei5: Float = 0f
    }

}
