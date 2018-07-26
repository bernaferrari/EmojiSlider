package com.bernaferrari.emojislider

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.graphics.drawable.Drawable.Callback
import android.support.v4.content.ContextCompat
import com.facebook.rebound.SimpleSpringListener
import com.facebook.rebound.Spring
import com.facebook.rebound.SpringSystem


class DrawableProfilePicture(context: Context) : Drawable(), Callback {

    val imageDrawable = BitmapDrawable()
    internal val ringDrawable: DrawableAverageCircle = DrawableAverageCircle(context)

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
            ringDrawable.radius = sizeHandle / 2
        }

    init {
        imageDrawable.callback = this
        ringDrawable.callback = this
        ringDrawable.outerColor = ContextCompat.getColor(context, R.color.colorPrimary)
    }

    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT
    override fun scheduleDrawable(drawable: Drawable, runnable: Runnable, j: Long) {}
    override fun unscheduleDrawable(drawable: Drawable, runnable: Runnable) {}

    private fun drawCircle(canvas: Canvas) {

        val drawable = when (imageDrawable.drawable) {
            null -> ringDrawable
            else -> imageDrawable
        }

        val intrinsicWidth = (this.sizeHandle - drawable.intrinsicWidth.toFloat()) / 2.0f
        val intrinsicHeight = (this.sizeHandle - drawable.intrinsicHeight.toFloat()) / 2.0f
        val scale = profileSpring.currentValue.toFloat()

//        canvas.save()
//        canvas.scale(scale, scale, bounds.exactCenterX(), bounds.exactCenterY())
//        drawRedPaint(canvas)
//        canvas.restore()

        canvas.save()
        canvas.translate(intrinsicWidth, intrinsicHeight)
        canvas.scale(scale, scale, bounds.exactCenterX(), bounds.exactCenterY())
        drawable.draw(canvas)
        canvas.restore()
    }

    // used for debugging
    private fun drawRedPaint(canvas: Canvas) {
        val paint = Paint()
        paint.style = Paint.Style.FILL
        paint.color = Color.parseColor("#CD5C5C")
        canvas.drawCircle(bounds.exactCenterX(), bounds.exactCenterY(), sizeHandle / 1.5f, paint)
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
        this.ringDrawable.alpha = i
        this.imageDrawable.alpha = i
    }

    override fun setBounds(left: Int, top: Int, right: Int, bottom: Int) {
        super.setBounds(left, top, right, bottom)
        this.ringDrawable.setBounds(left, top, right, bottom)
        this.imageDrawable.setBounds(left, top, right, bottom)
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        this.ringDrawable.colorFilter = colorFilter
        this.imageDrawable.colorFilter = colorFilter
    }
}