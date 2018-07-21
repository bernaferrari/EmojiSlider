package com.bernaferrari.emojislider

import android.content.Context
import android.graphics.*
import android.graphics.Shader.TileMode
import android.graphics.drawable.Drawable
import android.graphics.drawable.Drawable.Callback
import com.facebook.rebound.SimpleSpringListener
import com.facebook.rebound.Spring
import com.facebook.rebound.SpringConfig
import com.facebook.rebound.SpringSystem

class DrawSeekBar(context: Context) : Drawable(), Callback {
    val bigCircleThumb_f32834a: C7849d
    val f32853t: Spring
    internal val progressBackgroundPaint = Paint(1)
    private val bigCircleThumbf32851r: Spring
    private val gradientPaint = Paint(1)
    private val barRect = RectF()
    var f32841h: Boolean = false

    var percentProgress = 0.90f

    internal var colorStart: Int = 0
    internal var sliderHeight: Int = 0
    internal var colorEnd: Int = 0
    private var radius: Float = 0.toFloat()
    private var totalHeight: Float = 0.toFloat()

    init {

        // Add a spring to the system.
        val c = SpringSystem.create()

        this.bigCircleThumbf32851r = c.createSpring()
            .setSpringConfig(SpringConfig.fromOrigamiTensionAndFriction(2.0, 3.0))
            .setCurrentValue(1.0)
            .addListener(object : SimpleSpringListener() {
                override fun onSpringUpdate(spring: Spring?) {
                    // Just tell the UI to update based on the springs current state.
                    invalidateSelf()
                }
            })

        this.bigCircleThumb_f32834a = C7849d(context)
        this.bigCircleThumb_f32834a.callback = this


        this.f32853t = c.createSpring()
            .addListener(object : SimpleSpringListener() {
                override fun onSpringUpdate(spring: Spring?) {
                    // Just tell the UI to update based on the springs current state.
                    invalidateSelf()
                }
            })
            .setCurrentValue(0.0)
            .setSpringConfig(SpringConfig.fromOrigamiTensionAndFriction(40.0, 7.0))

        //        this.gradientBackground = ContextCompat.getColor(context, R.color.slider_gradient_background);
        //        innerColor = ContextCompat.getColor(context, R.color.slider_gradient_start);
        //        outerColor = ContextCompat.getColor(context, R.color.slider_gradient_end);

    }

    fun setGradientBackground(gradientBackground: Int) {
        this.progressBackgroundPaint.color = gradientBackground
    }

    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT
    override fun scheduleDrawable(drawable: Drawable, runnable: Runnable, j: Long) = Unit
    override fun unscheduleDrawable(drawable: Drawable, runnable: Runnable) = Unit

    fun m18483a(i: Int) {
        val c7849d = this.bigCircleThumb_f32834a
        c7849d.f32864e = i.toFloat()
        //        c7849d.f32860a.m10639a(c7849d.f32864e);
        val circleHandleC5190I = c7849d.imageHandle_f32861BAverage
        circleHandleC5190I.radius = c7849d.f32864e / 2.0f
        circleHandleC5190I.invalidateSelf()
        val c7852l = c7849d.f32862c
        c7852l.f32890a = c7849d.f32864e
        c7852l.invalidateSelf()
        c7849d.invalidateSelf()
    }

    fun m18486a(c5186e: C5186e) {
        this.bigCircleThumb_f32834a.m18496a(c5186e)
    }

    fun configureEmoji_m18487a(str: String) {
        val c7849d = this.bigCircleThumb_f32834a
        //        c7849d.f32860a.m10647a(new SpannableString(str));
        c7849d.invalidateSelf()
    }

    fun m18488b(f: Float) {
        //        this.percentProgress = f;
        val c7849d = this.bigCircleThumb_f32834a
        val circleHandleC5190I = c7849d.imageHandle_f32861BAverage
        circleHandleC5190I.outerColor = ColorHelper.getCorrectColor(
            this.colorStart,
            this.colorEnd,
            this.percentProgress
        )
        circleHandleC5190I.invalidateSelf()
        c7849d.invalidateSelf()
        invalidateSelf()
    }

    fun configureHeight(i: Int) {
        val f = i.toFloat()
        this.radius = f / 2.0f
        this.totalHeight = f
        invalidateSelf()
    }

    fun m18491b(c5186e: C5186e) {
        val c7849d = this.bigCircleThumb_f32834a
        if (c7849d.f32865f == null) {
            c7849d.m18496a(c5186e)
            return
        }
        if (c7849d.f32865f != c5186e) {
            c7849d.f32866g = c7849d.f32865f
            c7849d.f32865f = c5186e
            //            c7849d.f328163d.setCurrentValuem4026a(0.0d, true).m4030b(1.0d);
            c7849d.invalidateSelf()
        }
    }

    override fun draw(canvas: Canvas) {
        val width: Float
        val bigCircleThumb_f32834asxsy: Float = this.bigCircleThumbf32851r.currentValue.toFloat()
        var intrinsicWidth: Float = this.bigCircleThumb_f32834a.intrinsicWidth.toFloat()
        val intrinsicHeight: Float = this.bigCircleThumb_f32834a.intrinsicHeight.toFloat()
        var height: Float = (bounds.height() / 2).toFloat()

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



        height2 = bounds.height().toFloat() / 2.0f
        width = this.percentProgress * (bounds.width().toFloat())

        this.barRect.set(
            0.0f,
            height2 - this.totalHeight / 2.0f,
            width,
            height2 + this.totalHeight / 2.0f
        )

        canvas.drawRoundRect(this.barRect, this.radius, this.radius, this.gradientPaint)

        val intrinsicWidth2 =
            this.percentProgress * (bounds.width().toFloat() - intrinsicWidth) + intrinsicWidth / 2.0f

        canvas.save()
        canvas.scale(
            bigCircleThumb_f32834asxsy,
            bigCircleThumb_f32834asxsy,
            intrinsicWidth2,
            height
        )
        intrinsicWidth /= 2.0f

        this.bigCircleThumb_f32834a.setBounds(
            (intrinsicWidth2 - intrinsicWidth).toInt(),
            (height - intrinsicHeight).toInt(),
            (intrinsicWidth2 + intrinsicWidth).toInt(),
            (height + intrinsicHeight).toInt()
        )

        this.bigCircleThumb_f32834a.draw(canvas)
        canvas.restore()
        canvas.restore()
    }

    override fun getIntrinsicHeight(): Int {
        return if (this.sliderHeight > 0) this.sliderHeight else this.bigCircleThumb_f32834a.intrinsicHeight
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
        this.bigCircleThumb_f32834a.alpha = i
        this.progressBackgroundPaint.alpha = i
        this.gradientPaint.alpha = i
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        this.bigCircleThumb_f32834a.colorFilter = colorFilter
        this.progressBackgroundPaint.colorFilter = colorFilter
        this.gradientPaint.colorFilter = colorFilter
    }
}