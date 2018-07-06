package com.bernaferrari.emojislider

import android.content.Context
import android.graphics.*
import android.graphics.Shader.TileMode
import android.graphics.drawable.Drawable
import android.graphics.drawable.Drawable.Callback
import android.support.v4.content.ContextCompat
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import com.facebook.rebound.SimpleSpringListener
import com.facebook.rebound.Spring
import com.facebook.rebound.SpringConfig
import com.facebook.rebound.SpringSystem
import com.orhanobut.logger.Logger

class DrawSeekBar(context: Context) : Drawable(), Callback, OnTouchListener {
    val bigCircleThumb_f32834a: ImageChooserSelected_C7849d
    val averageCircleDrawable: AverageCircleDrawable
    internal val paintGradientBackground = Paint(1)
    private val paintShader = Paint(1)

    val f32853t: Spring
    private val f32851r: Spring
    private val f32852s: Spring
    private val rect_f32855v = RectF()

    var f32841h: Boolean = false
    var f32842i: Boolean = false
    var f32843j: Boolean = false

    var percentage_progress_f32847n = 0.90f
    var percentage_f32848o = 0.50f

    internal var gradientBackground: Int = 0
    internal var sliderHeight: Int = 0
    internal var color0_f32845l: Int = 0
    internal var color1_f32846m: Int = 0

    private var f32856w: Boolean = false
    private var f32857x: Boolean = false
    private var rectRx_f32858y: Float = 0f
    private var f32859z: Float = 0f

    init {
        // Add a spring to the system.
        val c = SpringSystem.create()

        this.f32851r = c.createSpring()
            .setSpringConfig(
                SpringConfig.fromOrigamiTensionAndFriction(10.0, 20.0)
            )
            .setCurrentValue(1.0)
            .addListener(object : SimpleSpringListener() {
                override fun onSpringUpdate(spring: Spring?) {
                    // Just tell the UI to update based on the springs current state.
                    invalidateSelf()
                }
            })

        this.f32852s = c.createSpring()
            .setSpringConfig(
                SpringConfig.fromOrigamiTensionAndFriction(10.0, 20.0)
            )
            .setCurrentValue(1.0)
            .addListener(object : SimpleSpringListener() {
                override fun onSpringUpdate(spring: Spring?) {
                    // Just tell the UI to update based on the springs current state.
                    invalidateSelf()
                }
            })


        this.bigCircleThumb_f32834a =
                ImageChooserSelected_C7849d(context)
        this.bigCircleThumb_f32834a.callback = this
        this.averageCircleDrawable =
                AverageCircleDrawable(context)
        this.averageCircleDrawable.callback = this

        this.f32853t = c.createSpring()
            .addListener(object : SimpleSpringListener() {
                override fun onSpringUpdate(spring: Spring?) {
                    // Just tell the UI to update based on the springs current state.
                    invalidateSelf()
                }
            })
            .setCurrentValue(0.0)
            .setSpringConfig(
                SpringConfig.fromOrigamiTensionAndFriction(40.0, 7.0)
            )

        this.gradientBackground =
                ContextCompat.getColor(context, R.color.slider_gradient_background)
        color0_f32845l = ContextCompat.getColor(context, R.color.slider_gradient_start)
        color1_f32846m = ContextCompat.getColor(context, R.color.slider_gradient_end)
        this.paintGradientBackground.color = this.gradientBackground
    }

    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT

    override fun scheduleDrawable(drawable: Drawable, runnable: Runnable, j: Long) = Unit

    override fun unscheduleDrawable(drawable: Drawable, runnable: Runnable) = Unit

    fun m18483a(i: Int) {

        bigCircleThumb_f32834a.f32864e = i.toFloat()

        val circleHandleC5190I = bigCircleThumb_f32834a.imageHandle_f32861b
        circleHandleC5190I.radius = bigCircleThumb_f32834a.f32864e / 2.0f
        circleHandleC5190I.invalidateSelf()

        val c7852l = bigCircleThumb_f32834a.f32862c
        c7852l.f32890a = bigCircleThumb_f32834a.f32864e
        c7852l.invalidateSelf()
        bigCircleThumb_f32834a.invalidateSelf()
    }

    internal fun updateShader(rect: Rect) {

        Logger.d(
            "updateShader to: " + ColorUtil_C3395a.getCorrectColor_m7508a(
                this.color0_f32845l,
                this.color1_f32846m,
                this.percentage_progress_f32847n
            )
        )

        paintShader.shader = LinearGradient(
            0.0f,
            rect.exactCenterY(),
            rect.width().toFloat(),
            rect.exactCenterY(),
            this.color0_f32845l,
            ColorUtil_C3395a.getCorrectColor_m7508a(
                this.color0_f32845l,
                this.color1_f32846m,
                this.percentage_progress_f32847n
            ),
            TileMode.CLAMP
        )
    }

    fun m18486a(c5186e: C5186e) {
        this.bigCircleThumb_f32834a.m18496a(c5186e)
    }

    fun m18487a(str: String) {
        val c7849d = this.bigCircleThumb_f32834a
        c7849d.invalidateSelf()
    }

    fun m18488b(f: Float) {
        //        this.percentage_progress_f32847n = f;
        val c7849d = this.bigCircleThumb_f32834a
        val circleHandleC5190I = c7849d.imageHandle_f32861b
        circleHandleC5190I.color_f20903c = ColorUtil_C3395a.getCorrectColor_m7508a(
            this.color0_f32845l,
            this.color1_f32846m,
            this.percentage_progress_f32847n
        )
        circleHandleC5190I.invalidateSelf()
        c7849d.invalidateSelf()
        invalidateSelf()
    }

    fun m18489b(i: Int) {
        val f = i.toFloat()
        this.rectRx_f32858y = f / 2.0f
        this.f32859z = f
        invalidateSelf()
    }

    fun m18491b(c5186e: C5186e) {

        if (bigCircleThumb_f32834a.f32865f == null) {
            bigCircleThumb_f32834a.m18496a(c5186e)
            return
        }
        if (bigCircleThumb_f32834a.f32865f != c5186e) {
            bigCircleThumb_f32834a.f32866g = bigCircleThumb_f32834a.f32865f
            bigCircleThumb_f32834a.f32865f = c5186e
            //            c7849d.f328163d.setCurrentValuem4026a(0.0d, true).m4030b(1.0d);
            bigCircleThumb_f32834a.invalidateSelf()
        }
    }

    override fun draw(canvas: Canvas) {

        Logger.d("started draw..")

        val width: Float
        var scaleValue: Float
        var bounds: Rect
        var intrinsicWidth: Float
        var intrinsicHeight: Float
        var height: Float
        var bounds2 = getBounds()

        canvas.save()
        canvas.translate(bounds2.left.toFloat(), bounds2.top.toFloat())

        val bounds3 = getBounds()
        var height2 = bounds3.height().toFloat() / 2.0f
        this.rect_f32855v.set(
            0.0f,
            height2 - this.f32859z / 2.0f,
            bounds3.width().toFloat(),
            height2 + this.f32859z / 2.0f
        )

        // draw grey rect (__________)
        canvas.drawRoundRect(
            this.rect_f32855v,
            this.rectRx_f32858y,
            this.rectRx_f32858y,
            this.paintGradientBackground
        )

        bounds2 = getBounds()
        var intrinsicWidth2 = this.bigCircleThumb_f32834a.intrinsicWidth.toFloat()
        height2 = bounds2.height().toFloat() / 2.0f
        width = if (this.f32841h) {
            percentage_progress_f32847n * (bounds2.width().toFloat() - intrinsicWidth2) + intrinsicWidth2 / 2.0f
        } else {
            percentage_progress_f32847n * bounds2.width().toFloat()
        }
        this.rect_f32855v.set(
            0.0f,
            height2 - this.f32859z / 2.0f,
            width,
            height2 + this.f32859z / 2.0f
        )
//        this.paintShader.shader = LinearGradient(
//            0.0f,
//            bounds2.centerY().toFloat(),
//            width,
//            bounds2.centerY().toFloat(),
//            this.color0_f32845l,
//            this.color1_f32846m,
//            TileMode.CLAMP
//        )

        canvas.drawRoundRect(
            this.rect_f32855v,
            this.rectRx_f32858y,
            this.rectRx_f32858y,
            this.paintShader
        )

        if (this.f32842i) {
            val correctColor = ColorUtil_C3395a.getCorrectColor_m7508a(
                this.color0_f32845l,
                this.color1_f32846m,
                this.percentage_f32848o
            )

            averageCircleDrawable.color_f20903c = correctColor
            averageCircleDrawable.invalidateSelf()

            scaleValue = if (this.f32843j) {
                f32853t.currentValue.toFloat()
            } else {
                f32852s.currentValue.toFloat()
            }

            bounds = getBounds()
            intrinsicWidth = averageCircleDrawable.intrinsicWidth.toFloat()
            intrinsicHeight = averageCircleDrawable.intrinsicHeight.toFloat()

            intrinsicWidth2 = if (f32841h) {
                percentage_f32848o * (bounds.width().toFloat() - intrinsicWidth) + intrinsicWidth / 2.0f
            } else {
                percentage_f32848o * bounds.width().toFloat()
            }

            height = (bounds.height() / 2).toFloat()
            canvas.save()
            canvas.scale(scaleValue, scaleValue, intrinsicWidth2, height)
            intrinsicWidth /= 2.0f
            intrinsicHeight /= 2.0f
            this.averageCircleDrawable.setBounds(
                (intrinsicWidth2 - intrinsicWidth).toInt(),
                (height - intrinsicHeight).toInt(),
                (intrinsicWidth2 + intrinsicWidth).toInt(),
                (height + intrinsicHeight).toInt()
            )

            // draw circle ()
            this.averageCircleDrawable.draw(canvas)

            canvas.restore()
        }
        bounds = getBounds()
        intrinsicWidth = this.bigCircleThumb_f32834a.intrinsicWidth.toFloat()
        intrinsicHeight = this.bigCircleThumb_f32834a.intrinsicHeight.toFloat()
        scaleValue = this.f32851r.currentValue.toFloat()
        intrinsicWidth2 = if (f32841h) {
            percentage_progress_f32847n * (bounds.width().toFloat() - intrinsicWidth) + intrinsicWidth / 2.0f
        } else {
            percentage_progress_f32847n * bounds.width().toFloat()
        }
        height = (bounds.height() / 2).toFloat()
        canvas.save()
        canvas.scale(scaleValue, scaleValue, intrinsicWidth2, height)
        intrinsicWidth /= 2.0f
        intrinsicHeight /= 2.0f
        this.bigCircleThumb_f32834a.setBounds(
            (intrinsicWidth2 - intrinsicWidth).toInt(),
            (height - intrinsicHeight).toInt(),
            (intrinsicWidth2 + intrinsicWidth).toInt(),
            (height + intrinsicHeight).toInt()
        )
        this.bigCircleThumb_f32834a.draw(canvas)
        canvas.restore()
        canvas.restore()

        Logger.d("scale value is: " + scaleValue + " --- is equal to 1? " + (scaleValue == 1f) + " -- less than 1: " + (scaleValue < 1f))

        if (scaleValue > 0f && scaleValue < 1f) {
            Logger.d("invalidating self..")

//            inval
//            invalidateSelf()
        }

    }

    override fun getIntrinsicHeight(): Int {
        return if (this.sliderHeight > 0) this.sliderHeight else this.bigCircleThumb_f32834a.intrinsicHeight
    }

    override fun invalidateDrawable(drawable: Drawable) = invalidateSelf()

    override fun onBoundsChange(rect: Rect) = updateShader(rect)

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        val bounds = bounds
        val x = motionEvent.x.toInt() - bounds.left
        val y = motionEvent.y.toInt() - bounds.top

        when (motionEvent.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                this.f32856w = this.bigCircleThumb_f32834a.bounds.contains(x, y)
                if (this.f32856w) {
                    this.f32851r.currentValue = 0.9
                }
                this.f32857x = this.averageCircleDrawable.bounds.contains(x, y)
                if (this.f32857x) {
                    this.f32852s.currentValue = 0.9
                }
            }

            MotionEvent.ACTION_UP -> view.performClick()

            MotionEvent.ACTION_CANCEL -> cancelMethod()

            MotionEvent.ACTION_MOVE -> {
            }
        }

        return true
    }

    fun cancelMethod() {
        this.f32856w = false
        this.f32857x = false
        this.f32851r.endValue = 0.0
        this.f32852s.endValue = 0.0
//        invalidateSelf()
    }

    override fun setAlpha(i: Int) {
        this.bigCircleThumb_f32834a.alpha = i
        this.paintGradientBackground.alpha = i
        this.paintShader.alpha = i
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        this.bigCircleThumb_f32834a.colorFilter = colorFilter
        this.paintGradientBackground.colorFilter = colorFilter
        this.paintShader.colorFilter = colorFilter
    }
}