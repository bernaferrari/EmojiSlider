package com.bernaferrari.emojislider

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.graphics.drawable.Drawable.Callback
import android.view.MotionEvent
import android.view.View
import com.facebook.rebound.SimpleSpringListener
import com.facebook.rebound.Spring
import com.facebook.rebound.SpringConfig
import com.facebook.rebound.SpringSystem
import kotlin.math.roundToInt

interface Lifecycles {
    fun startAnimation()
    fun stopAnimation()
}

class SliderDrawable(
    context: Context,
    private val tracking: TrackingTouch
) : Drawable(), Callback, Lifecycles, View.OnTouchListener {

    val drawSeekBar: DrawSeekBar = DrawSeekBar(context)
    var thumb: Drawable = generateThumb(
        context = context,
        text = "😍",
        size = R.dimen.slider_sticker_slider_handle_size
    )


    fun updateThumb() {

    }

    var slider_padding: Int = 0

    private var scale = 1f
    private val mSpringSystem = SpringSystem.create()
    private val mSpringListener = ExampleSpringListener()
    private val mScaleSpring: Spring = mSpringSystem.createSpring().setSpringConfig(
        SpringConfig.fromOrigamiTensionAndFriction(3.0, 5.0)
    )

    var isThumbSelected = false

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {

        val x = motionEvent.x.toInt() - drawSeekBar.bounds.left
        val y = motionEvent.y.toInt() - drawSeekBar.bounds.top

        when (motionEvent.actionMasked) {
            MotionEvent.ACTION_DOWN -> {

                println("x: " + x + " y: " + y + " ThumbBounds: " + thumb.bounds.toShortString() + "drawSeekBounds: " + drawSeekBar.bounds.toShortString() + " left: " + drawSeekBar.bounds.left + " slider_padding: " + slider_padding)

                if (this.thumb.bounds.contains(x, y)) {
                    isThumbSelected = true
                    tracking.onStartTrackingTouch()
                    mScaleSpring.endValue = 0.9
                }
            }

            MotionEvent.ACTION_UP -> {
                cancelMethod()
                view.performClick()
                invalidateSelf()
            }

            MotionEvent.ACTION_CANCEL -> cancelMethod()

            MotionEvent.ACTION_MOVE -> {
                if (isThumbSelected) {
                    println(
                        "newPercentFormula: " + Math.min(
                            Math.max(
                                (x) / drawSeekBar.bounds.width().toDouble(),
                                0.0
                            ), 1.0
                        )
                    )
                    updatePercentage(
                        (Math.min(
                            Math.max(
                                x / drawSeekBar.bounds.width().toDouble(),
                                0.0
                            ), 1.0
                        ) * 100).roundToInt()
                    )
                    println(
                        "moving.. " + "x: " + x + " width: " + drawSeekBar.bounds.width() + " equals: " + Math.min(
                            Math.max(
                                (x.toFloat() / drawSeekBar.bounds.width().toFloat()).toDouble(),
                                0.0
                            ),
                            1.0
                        ).toFloat()
                    )
                }
            }
        }

        return true
    }

    var percentage = 0

    fun updatePercentage(progress: Int) {
        println("updatePercent: $progress")
        percentage = progress

        drawSeekBar.percentage_progress_f32847n = progress / 100f
        drawSeekBar.invalidateSelf()
        tracking.onProgressChanged(progress)

//        val c7849d = this.bigCircleThumb_f32834a
//        val circleHandleC5190I = c7849d.imageHandle_f32861b
//        circleHandleC5190I.color_f20903c = C3395a.getCorrectColor_m7508a(
//            this.colorStart,
//            this.colorEnd,
//            this.percentage_progress_f32847n
//        )
//        circleHandleC5190I.invalidateSelf()
//        c7849d.invalidateSelf()

        invalidateSelf()
    }


    private fun updateThumbBounds(intrinsicWidth2: Int) {

        val customIntrinsicWidth = thumb.intrinsicWidth / 2
        val customIntrinsicHeight = thumb.intrinsicHeight / 2
        val customHeight = bounds.height() / 2

        this.thumb.setBounds(
            intrinsicWidth2 - customIntrinsicWidth,
            customHeight - customIntrinsicHeight,
            intrinsicWidth2 + customIntrinsicWidth,
            customHeight + customIntrinsicHeight
        )
    }


    fun cancelMethod() {
        if (isThumbSelected) {
            tracking.onStopTrackingTouch()
        }
        isThumbSelected = false
//        this.f32856w = false
//        this.f32857x = false
//        this.f32851r.setEndValue(1.0)
        mScaleSpring.endValue = 1.0
    }

    override fun startAnimation() {
        mScaleSpring.addListener(mSpringListener)
    }

    override fun stopAnimation() {
        mScaleSpring.removeListener(mSpringListener)
    }

    private inner class ExampleSpringListener : SimpleSpringListener() {
        override fun onSpringUpdate(spring: Spring) {
            // On each update of the spring value, we adjust the scale of the image view to match the
            // springs new value. We use the SpringUtil linear interpolation function mapValueFromRangeToRange
            // to translate the spring's 0 to 1 scale to a 100% to 50% scale range and apply that to the View
            // with setScaleX/Y. Note that rendering is an implementation detail of the application and not
            // Rebound itself.

            scale = spring.currentValue.toFloat()
            invalidateSelf()
        }
    }

    init {
        configureHandle()

        this.drawSeekBar.callback = this
        drawSeekBar.f32841h = true
        drawSeekBar.invalidateSelf()
        drawSeekBar.configureEmoji_m18487a("😍")
        drawSeekBar.m18483a(context.resources.getDimensionPixelSize(R.dimen.slider_sticker_tray_slider_handle_size))
        drawSeekBar.m18486a(C5186e.EMOJI)
        drawSeekBar.m18489b(context.resources.getDimensionPixelSize(R.dimen.slider_sticker_tray_track_height))
    }

    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT
    override fun scheduleDrawable(drawable: Drawable, runnable: Runnable, j: Long) = Unit
    override fun unscheduleDrawable(drawable: Drawable, runnable: Runnable) = Unit
    override fun invalidateDrawable(drawable: Drawable) {
        invalidateSelf()
    }

    override fun draw(canvas: Canvas) {
//        println("draw! " + bounds.width() + " exactCenter: " + bounds.exactCenterX())
        drawBar(canvas)
        drawThumb(canvas)
    }

    private fun configureHandle() {
        this.thumb.callback = this
        mScaleSpring.isOvershootClampingEnabled = true
        mScaleSpring.endValue = 1.0
        startAnimation()
    }

    private fun drawThumb(canvas: Canvas) {

        val intrinsicWidth2 = percentage * drawSeekBar.bounds.width() / 100f
        updateThumbBounds(intrinsicWidth2.roundToInt())

        canvas.save()
        canvas.translate(drawSeekBar.bounds.left.toFloat(), drawSeekBar.bounds.top.toFloat())
        canvas.scale(scale, scale, intrinsicWidth2, bounds.exactCenterY())
        this.thumb.draw(canvas)
        canvas.restore()
    }

    private fun drawBar(canvas: Canvas) {
        this.drawSeekBar.draw(canvas)
    }

    override fun setAlpha(i: Int) {
        this.thumb.alpha = i
        this.drawSeekBar.alpha = i
    }

    override fun setBounds(left: Int, top: Int, right: Int, bottom: Int) {
        super.setBounds(left, top, right, bottom)

        this.drawSeekBar.setBounds(
            left + slider_padding,
            top,
            right - slider_padding,
            bottom
        )
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        this.thumb.colorFilter = colorFilter
        this.drawSeekBar.colorFilter = colorFilter
    }
}


