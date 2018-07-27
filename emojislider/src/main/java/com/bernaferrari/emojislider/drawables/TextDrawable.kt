package com.bernaferrari.emojislider.drawables

import android.annotation.TargetApi
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.text.*
import android.text.Layout.Alignment
import android.view.ViewTreeObserver.OnPreDrawListener

/**
 * Converts a text into a drawable.
 */
class TextDrawable(context: Context, var width: Int) : Drawable() {
    val text: TextPaint = TextPaint()
    var spannable: Spannable? = null
    private lateinit var staticlayout: StaticLayout
    var align = Alignment.ALIGN_CENTER
    var horizontalPadding: Float = 0f
    var verticalPadding: Float = 0f
    private var bitmap: Bitmap? = null
    private var rectLeft: Int = 0
    private var rectTop: Int = 0
    private var intrinsicWidth: Int = 0
    private var intrinsicHeight: Int = 0
    private val spacingadd = 0.0f
    private val spacingmult = 1.0f

    init {
        this.text.density = context.resources.displayMetrics.density
        this.text.isAntiAlias = true
        this.text.isDither = true
        this.text.isFilterBitmap = true
        this.text.color = -1
    }

    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT

    private fun drawText() {
        if (this.spannable == null) {
            return
        }

        val charSequence = this.spannable

        this.staticlayout = StaticLayout(
            charSequence,
            this.text,
            this.width,
            this.align,
            this.spacingmult,
            this.spacingadd,
            false
        )

        this.intrinsicWidth = getMaxLineWidth(this.staticlayout) +
                Math.round(this.horizontalPadding * 2.0f)
        this.intrinsicHeight = this.staticlayout.height +
                Math.round(this.verticalPadding * 2.0f)

        clearBitmap()
    }

    fun setTextSize(f: Float) {
        this.text.textSize = f
        drawText()
        invalidateSelf()
    }

    fun setShadowLayer(radius: Float, dy: Float, shadowColor: Int) {
        this.text.setShadowLayer(radius, 0.0f, dy, shadowColor)
        drawText()
        invalidateSelf()
    }

    fun setColor(i: Int) {
        this.text.color = i
        drawText()
        invalidateSelf()
    }

    private fun drawText(canvas: Canvas) {
        if (!TextUtils.isEmpty(this.spannable)) {

            val onPreDrawListenerArr =
                this.spannable!!.getSpans(0, this.spannable!!.length, OnPreDrawListener::class.java)

            for (i in onPreDrawListenerArr.indices) {
                onPreDrawListenerArr[i].onPreDraw()
            }
        }
        canvas.translate(this.horizontalPadding, this.verticalPadding)

        if (this.align != Alignment.ALIGN_NORMAL) {
            val translateValue = getMinLineLeft(this.staticlayout).toFloat()
            canvas.save()
            canvas.translate(-translateValue, 0.0f)
            this.staticlayout.draw(canvas)
            canvas.restore()
            return
        }
        this.staticlayout.draw(canvas)
    }

    fun setTypeface(typeface: Typeface) {
        this.text.typeface = typeface
        drawText()
        invalidateSelf()
    }

    fun setSpannableValue(spannable: Spannable) {
        if (this.spannable == null || this.spannable != spannable) {
            this.spannable = spannable
            drawText()
            invalidateSelf()
        }
    }

    fun clearBitmap() {
        if (bitmap != null) {
            bitmap!!.recycle()
            bitmap = null
        }
    }

    fun setPadding(horizontal: Float, vertical: Float) {
        this.horizontalPadding = horizontal
        this.verticalPadding = vertical
        drawText()
        invalidateSelf()
    }

    @TargetApi(21)
    fun setLetterSpacing() {
        this.text.letterSpacing = -0.03f
        drawText()
        invalidateSelf()
    }

    fun clearShadowLayer() {
        this.text.clearShadowLayer()
        drawText()
        invalidateSelf()
    }

    override fun draw(canvas: Canvas) {
        canvas.save()
        canvas.translate(this.rectLeft.toFloat(), this.rectTop.toFloat())
        if (bitmap != null) {
            canvas.drawBitmap(this.bitmap!!, 0.0f, 0.0f, this.text)
        } else {
            drawText(canvas)
        }
        canvas.restore()
    }

    override fun getIntrinsicHeight(): Int = this.intrinsicHeight

    override fun getIntrinsicWidth(): Int = this.intrinsicWidth

    override fun onBoundsChange(rect: Rect) {
        this.rectLeft = rect.left
        this.rectTop = rect.top
    }

    override fun setAlpha(i: Int) {
        this.text.alpha = i
        drawText()
        invalidateSelf()
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        this.text.colorFilter = colorFilter
        drawText()
        invalidateSelf()
    }

    fun getMaxLineWidth(layout: Layout?): Int {
        if (layout == null) {
            return 0
        }

        var maxWidth = 0
        for (i in 0..layout.lineCount) {
            maxWidth =
                    Math.max(maxWidth, Math.round(layout.getLineRight(i) - layout.getLineLeft(i)))
        }

        return maxWidth
    }

    fun getMinLineLeft(layout: Layout?): Int {
        if (layout == null || layout.lineCount == 0) {
            return 0
        }

        var minLineLeft = Integer.MAX_VALUE
        for (i in 0..layout.lineCount) {
            minLineLeft = Math.min(minLineLeft, layout.getLineLeft(i).toInt())
        }
        return minLineLeft
    }
}
