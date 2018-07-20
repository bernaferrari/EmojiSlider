package com.bernaferrari.emojislider

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.graphics.drawable.Drawable.Callback
import android.view.MotionEvent
import android.view.View
import com.facebook.rebound.*


interface Lifecycles {
    fun startAnimation()
    fun stopAnimation()
}

class SpringSample1(context: Context) : Drawable(), Callback, Lifecycles, View.OnTouchListener {

    private val drawSeekBar: DrawSeekBar = DrawSeekBar(context)
    private val thumb: Drawable = generateThumb(
        context = context,
        text = "😍",
        size = R.dimen.slider_sticker_slider_handle_size
    )

    private val tray_height: Int
    private val slider_padding: Int
    private var scale = 1f

    private val mSpringSystem = SpringSystem.create()
    private val mSpringListener = ExampleSpringListener()
    private val mScaleSpring: Spring = mSpringSystem.createSpring().setSpringConfig(
        SpringConfig.fromOrigamiTensionAndFriction(3.0, 5.0)
    )

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {

        val x = motionEvent.x.toInt() - bounds.left
        val y = motionEvent.y.toInt() - bounds.top

        when (motionEvent.actionMasked) {
            MotionEvent.ACTION_DOWN -> {


                val x1 = (this.thumb.intrinsicWidth - this.thumb.bounds.right) / 2
                val y2 = (this.thumb.intrinsicHeight - this.thumb.bounds.bottom) / 2
                println("x: " + x + " y: " + y + " bounds: " + this.thumb.bounds.toShortString() + " x1: " + x1 + " y2: " + y2)


                val height = (bounds.height() / 2).toFloat()

                val intrinsicWidth2 = if (true) {
                    0.5 * (bounds.width().toFloat() - intrinsicWidth) + intrinsicWidth / 2.0f
                } else {
                    0.35 * bounds.width().toFloat()
                }

                val verticalCenter = (bounds.bottom - bounds.top) / 2 + bounds.top

                this.thumb.setBounds(
                    (intrinsicWidth2 - this.thumb.intrinsicWidth).toInt(),
                    verticalCenter - this.tray_height / 2,
                    (intrinsicWidth2 + this.thumb.intrinsicWidth).toInt(),
                    verticalCenter + this.tray_height / 2
                )


                if (this.thumb.bounds.contains(x, y)) {
                    mScaleSpring.endValue = 0.9
                }

//                this.f32856w = this.bigCircleThumb_f32834a.getBounds().contains(x, y)
//                if (this.f32856w) {
//                    this.f32851r.setCurrentValue(0.9)
//                }
//                this.f32857x = this.averageCircleHandle_f32835b.getBounds().contains(x, y)
//                if (this.f32857x) {
//                    this.f32852s.setCurrentValue(0.9)
//                }
//                if (this.f32850q != null) {
//                    var f: Float
//                    if (!this.f32840g || !this.f32856w) {
//                        val z = this.f32856w
//                        f = this.percentage_progress_f32847n
//                        if (z) {
//                            break
//                        }
//                    }
//                    val cjVar2 = this.f32850q
//                    f = this.percentage_progress_f32847n
//                    break
//                }
            }

            MotionEvent.ACTION_UP -> {
                cancelMethod()
                view.performClick()
                invalidateSelf()
            }

            MotionEvent.ACTION_CANCEL -> cancelMethod()

            MotionEvent.ACTION_MOVE -> {

            }
        }

        return true
    }

    fun cancelMethod() {
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
            // Rebound itself. If you need Gingerbread compatibility consider using NineOldAndroids to update
            // your view properties in a backwards compatible manner.
            val mappedValue =
                SpringUtil.mapValueFromRangeToRange(spring.currentValue, 0.0, 1.0, 1.0, 0.5)
                    .toFloat()

//            if (spring.currentValue > 0.99) {
//                mScaleSpring.endValue = 0.9
//            } else if (spring.currentValue < 0.91) {
//                mScaleSpring.endValue = 1.0
//            }

            scale = spring.currentValue.toFloat()

//            println("onSpringUpdate!: " + spring.currentValue)
            invalidateSelf()
        }
    }

    init {
        configureHandle()

        this.tray_height =
                context.resources.getDimensionPixelSize(R.dimen.slider_sticker_tray_height)
        this.slider_padding =
                context.resources.getDimensionPixelSize(R.dimen.slider_sticker_tray_slider_padding)

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
        drawHandle(canvas)
    }

    private fun configureHandle() {
        this.thumb.callback = this
        mScaleSpring.isOvershootClampingEnabled = true
        mScaleSpring.endValue = 1.0
        startAnimation()
    }

    private fun drawHandle(canvas: Canvas) {

        val intrinsicWidth2 = if (true) {
            0.5 * (bounds.width().toFloat() - intrinsicWidth) + intrinsicWidth / 2.0f
        } else {
            0.35 * bounds.width().toFloat()
        }

        canvas.save()
        canvas.scale(scale, scale, intrinsicWidth2.toFloat(), bounds.exactCenterY())
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
        val verticalCenter = (bottom - top) / 2 + top
        this.thumb.setBounds(
            left,
            verticalCenter - this.tray_height / 2,
            right,
            verticalCenter + this.tray_height / 2
        )
        this.drawSeekBar.setBounds(
            left + this.slider_padding,
            top,
            right - this.slider_padding,
            bottom
        )
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        this.thumb.colorFilter = colorFilter
        this.drawSeekBar.colorFilter = colorFilter
    }
}


