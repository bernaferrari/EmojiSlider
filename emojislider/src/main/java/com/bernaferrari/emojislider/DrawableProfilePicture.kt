package com.bernaferrari.emojislider

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.graphics.drawable.Drawable.Callback
import android.support.v4.content.ContextCompat
import com.facebook.rebound.SimpleSpringListener
import com.facebook.rebound.Spring
import com.facebook.rebound.SpringSystem


class DrawableProfilePicture(context: Context) : Drawable(), Callback {

    internal val drawableAverageHandle: DrawableAverageCircle = DrawableAverageCircle(context)

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

    internal var sizeHandle: Float = 0f

    init {
        this.drawableAverageHandle.callback = this
        this.drawableAverageHandle.outerColor =
                ContextCompat.getColor(context, R.color.colorPrimary)
    }

    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT
    override fun scheduleDrawable(drawable: Drawable, runnable: Runnable, j: Long) {}
    override fun unscheduleDrawable(drawable: Drawable, runnable: Runnable) {}

    private fun drawCircle(canvas: Canvas) {

        val drawable = drawableAverageHandle
//        val imageBounds = canvas.clipBounds

//        mCustomImage.setB/ounds(imageBounds)
//        mCustomImage.draw(canvas)

//        drawable = when (f20875a[c5186e.ordinal]) {
//            1 -> this.drawableAverageHandle
//            2 -> this.drawableAverageHandle
//            3 -> this.drawableAverageHandle
//            else -> this.drawableAverageHandle
//        }//                StringBuilder stringBuilder = new StringBuilder("Unsupported handle type: ");
        //                stringBuilder.append(this.f32865f);
        //                throw new IllegalStateException(stringBuilder.toString());
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

    override fun getIntrinsicHeight(): Int {
        return this.sizeHandle.toInt()
    }

    override fun getIntrinsicWidth(): Int {
        return this.sizeHandle.toInt()
    }

    override fun invalidateDrawable(drawable: Drawable) {
        invalidateSelf()
    }

    override fun setAlpha(i: Int) {
        this.drawableAverageHandle.alpha = i
    }

    override fun setBounds(left: Int, top: Int, right: Int, bottom: Int) {
        super.setBounds(left, top, right, bottom)
        this.drawableAverageHandle.setBounds(left, top, right, bottom)
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        this.drawableAverageHandle.colorFilter = colorFilter
    }
}