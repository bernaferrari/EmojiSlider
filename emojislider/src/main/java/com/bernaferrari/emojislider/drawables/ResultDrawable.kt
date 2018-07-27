package com.bernaferrari.emojislider.drawables

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.graphics.drawable.Drawable.Callback
import android.support.v4.content.ContextCompat
import com.bernaferrari.emojislider.R
import com.bernaferrari.emojislider.origamiConfig
import com.facebook.rebound.SimpleSpringListener
import com.facebook.rebound.Spring
import com.facebook.rebound.SpringSystem


class ResultDrawable(context: Context) : Drawable(), Callback {

    val imageDrawable = BitmapDrawable()
    val circleDrawable: CircleDrawable =
        CircleDrawable(context)

    private val mSpringSystem = SpringSystem.create()
    private val mSpringListener = object : SimpleSpringListener() {
        override fun onSpringUpdate(spring: Spring?) {
            invalidateSelf()
        }
    }

    private val profileSpring = mSpringSystem.createSpring()
        .origamiConfig(40.0, 7.0)
        .setCurrentValue(0.0)
        .addListener(mSpringListener)

    var sizeHandle: Float = 0f
        set(value) {
            field = value
            imageDrawable.diameter = sizeHandle
            circleDrawable.radius = sizeHandle / 2
        }

    init {
        imageDrawable.callback = this
        circleDrawable.callback = this
        circleDrawable.outerColor = ContextCompat.getColor(context, R.color.colorPrimary)
    }

    fun setDrawableFromBitmap(bitmap: Bitmap) {
        imageDrawable.generateDrawable(bitmap)
    }

    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT
    override fun scheduleDrawable(drawable: Drawable, runnable: Runnable, j: Long) = Unit
    override fun unscheduleDrawable(drawable: Drawable, runnable: Runnable) = Unit

    private fun drawCircle(canvas: Canvas) {

        val drawable = when (imageDrawable.drawable) {
            null -> circleDrawable
            else -> imageDrawable
        }

        val intrinsicWidth = (this.sizeHandle - drawable.intrinsicWidth.toFloat()) / 2.0f
        val intrinsicHeight = (this.sizeHandle - drawable.intrinsicHeight.toFloat()) / 2.0f
        val scale = profileSpring.currentValue.toFloat()

        canvas.save()
        canvas.translate(intrinsicWidth, intrinsicHeight)
        canvas.scale(scale, scale, bounds.exactCenterX(), bounds.exactCenterY())
        drawable.draw(canvas)
        canvas.restore()
    }

    var currentValue: Double
        get() = this.profileSpring.currentValue
        set(value) {
            this.profileSpring.currentValue = value
            invalidateSelf()
        }

    var endValue: Double
        get() = this.profileSpring.endValue
        set(value) {
            this.profileSpring.endValue = value
            invalidateSelf()
        }

    override fun draw(canvas: Canvas) {
        drawCircle(canvas)
    }

    override fun getIntrinsicHeight(): Int = sizeHandle.toInt()

    override fun getIntrinsicWidth(): Int = sizeHandle.toInt()

    override fun invalidateDrawable(drawable: Drawable) = invalidateSelf()

    override fun setAlpha(i: Int) {
        this.circleDrawable.alpha = i
        this.imageDrawable.alpha = i
    }

    override fun setBounds(left: Int, top: Int, right: Int, bottom: Int) {
        super.setBounds(left, top, right, bottom)
        this.circleDrawable.setBounds(left, top, right, bottom)
        this.imageDrawable.setBounds(left, top, right, bottom)
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        this.circleDrawable.colorFilter = colorFilter
        this.imageDrawable.colorFilter = colorFilter
    }
}