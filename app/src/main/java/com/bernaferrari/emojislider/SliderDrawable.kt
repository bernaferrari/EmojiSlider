package com.bernaferrari.emojislider

import android.content.Context
import android.graphics.*
import android.graphics.PorterDuff.Mode
import android.graphics.drawable.Drawable
import android.graphics.drawable.Drawable.Callback
import android.support.v4.content.ContextCompat
import android.view.MotionEvent
import android.view.View

class SliderDrawable(context: Context) : Drawable(), Callback, View.OnTouchListener {

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        return this.sliderBar.onTouch(v, event)
    }

    val paddingHorizontal: Int
    val sliderBar: DrawSeekBar
    private val paddingTopWithoutQuestion: Int
    private val paddingBottomWithoutQuestion: Int
    private val sliderStickerBackground: Drawable?
    private val stickerBackground: Drawable?
    var f32871e: C5179d? = null
    var percentage_f32872f: Float = 0.5f

    fun updateShader() {
        sliderBar.updateShader(sliderBar.bounds)
//        sliderBar.invalidateSelf()
    }

    init {
        val resources = context.resources
        this.paddingTopWithoutQuestion =
                resources.getDimensionPixelSize(R.dimen.slider_sticker_padding_top_without_question)
        this.paddingBottomWithoutQuestion =
                resources.getDimensionPixelSize(R.dimen.slider_sticker_padding_bottom_without_question)
        this.paddingHorizontal =
                resources.getDimensionPixelSize(R.dimen.slider_sticker_padding_horizontal)

        this.sliderStickerBackground =
                ContextCompat.getDrawable(context, R.drawable.slider_sticker_background)
        this.sliderStickerBackground!!.callback = this
        this.stickerBackground = ContextCompat.getDrawable(
            context,
            R.drawable.slider_sticker_background
        ) //question_background_shadow);
        this.stickerBackground!!.callback = this

        this.sliderBar = DrawSeekBar(context)
        this.sliderBar.callback = this
        this.sliderBar.m18483a(resources.getDimensionPixelSize(R.dimen.slider_sticker_slider_handle_size))
        var c7848b = this.sliderBar
        c7848b.sliderHeight = resources.getDimensionPixelSize(R.dimen.slider_sticker_slider_height)
        c7848b.invalidateSelf()
        this.sliderBar.m18489b(resources.getDimensionPixelSize(R.dimen.slider_sticker_slider_track_height))
        c7848b = this.sliderBar
        val voteAverageHandleSize =
            resources.getDimensionPixelSize(R.dimen.slider_sticker_slider_vote_average_handle_size)
        val circleHandleC5190I = c7848b.averageCircleDrawable
        circleHandleC5190I.radius = voteAverageHandleSize.toFloat() / 2.0f
        circleHandleC5190I.invalidateSelf()
    }

    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT

    override fun scheduleDrawable(drawable: Drawable, runnable: Runnable, j: Long) = Unit

    override fun unscheduleDrawable(drawable: Drawable, runnable: Runnable) = Unit

    fun m18503c() {
        val color: Int
        var str: String?
        var i2: Int
        var drawSeekBar: DrawSeekBar
        var averageCircleDrawable: AverageCircleDrawable
        var somecolor3: Int
        var drawSeekBar2: DrawSeekBar
        var percentage_f: Float
        var drawSeekBar3: DrawSeekBar
        var f2: Float
        if (this.f32871e == null) {
            color = -1
        } else {
            str = this.f32871e!!.backgroundColorf20864e
            color = if (str == null) {
                0
            } else {
                Color.parseColor(str)
            }
        }

        this.sliderStickerBackground!!.mutate().colorFilter = PorterDuffColorFilter(color, Mode.SRC)

        if (this.f32871e != null) {
            if (this.f32871e!!.f20867h != null) {
                drawSeekBar = this.sliderBar
                averageCircleDrawable = drawSeekBar.averageCircleDrawable
                averageCircleDrawable.color_f20902b = color
                averageCircleDrawable.invalidateSelf()
                somecolor3 = if (color != -1) {
                    drawSeekBar.gradientBackground
                } else {
                    ColorUtil_C3395a.m7509b(color)
                }

                drawSeekBar.paintGradientBackground.color = somecolor3

                drawSeekBar.updateShader(drawSeekBar.bounds)
                drawSeekBar.invalidateSelf()
                if (this.percentage_f32872f == 0f) {
                    this.sliderBar.m18491b(C5186e.USER)
                    drawSeekBar = this.sliderBar
                    if (this.f32871e != null) {
                        if (this.f32871e!!.f20861b == -1) {
                            if (this.f32871e!!.m9938a()) {
                                i2 = this.f32871e!!.f20861b
                                percentage_f = (i2 * this.f32871e!!.f20863d + percentage_f32872f) /
                                        (i2 + 1).toFloat()
                            } else {
                                percentage_f = this.f32871e!!.f20863d
                            }
                            drawSeekBar.f32843j = !drawSeekBar.f32842i
                            drawSeekBar.f32842i = true
                            drawSeekBar.percentage_f32848o = percentage_f
                            drawSeekBar.invalidateSelf()
                        }
                    }

                    drawSeekBar.f32843j = !drawSeekBar.f32842i
                    drawSeekBar.f32842i = true
                    drawSeekBar.percentage_f32848o = percentage_f32872f
                    drawSeekBar.invalidateSelf()
                } else {
                    drawSeekBar2 = this.sliderBar
                    if (this.f32871e != null) {
                        if (this.f32871e!!.f20865f == null) {
                            str = f32871e!!.f20865f
                            drawSeekBar2.m18487a(str)
                            this.sliderBar.m18486a(C5186e.EMOJI)
                            drawSeekBar3 = this.sliderBar
                            drawSeekBar3.f32843j = false
                            drawSeekBar3.f32842i = false
                            drawSeekBar3.invalidateSelf()
                        }
                    }
                    str = "😍"
                    drawSeekBar2.m18487a(str)
                    this.sliderBar.m18486a(C5186e.EMOJI)
                    drawSeekBar3 = this.sliderBar
                    drawSeekBar3.f32843j = false
                    drawSeekBar3.f32842i = false
                    drawSeekBar3.invalidateSelf()
                }
                if (this.percentage_f32872f == 0f) {
                    drawSeekBar2 = this.sliderBar
                    f2 = percentage_f32872f
                } else {
                    drawSeekBar2 = this.sliderBar
                    f2 = 0.1f
                }
                drawSeekBar2.m18488b(f2)
                invalidateSelf()
            }
        }

        drawSeekBar = this.sliderBar
        averageCircleDrawable = drawSeekBar.averageCircleDrawable
        averageCircleDrawable.color_f20902b = color
        averageCircleDrawable.invalidateSelf()

        somecolor3 = if (color != -1) {
            ColorUtil_C3395a.m7509b(color)
        } else {
            drawSeekBar.gradientBackground
        }

        drawSeekBar.paintGradientBackground.color = somecolor3
        drawSeekBar.updateShader(drawSeekBar.bounds)
        drawSeekBar.invalidateSelf()

        this.sliderBar.m18491b(C5186e.USER)
        drawSeekBar = this.sliderBar
        if (this.f32871e != null && this.f32871e!!.f20861b == -1) {
            if (this.f32871e!!.m9938a()) {
                i2 = this.f32871e!!.f20861b
                percentage_f = (i2.toFloat() * this.f32871e!!.f20863d + percentage_f32872f) /
                        (i2 + 1).toFloat()
            } else {
                percentage_f = this.f32871e!!.f20863d
            }
            drawSeekBar.f32843j = !drawSeekBar.f32842i
            drawSeekBar.f32842i = true
            drawSeekBar.percentage_f32848o = percentage_f
            drawSeekBar.invalidateSelf()
        }
        percentage_f = percentage_f32872f
        drawSeekBar.f32843j = !drawSeekBar.f32842i
        drawSeekBar.f32842i = true
        drawSeekBar.percentage_f32848o = percentage_f
        drawSeekBar.invalidateSelf()

        drawSeekBar2 = this.sliderBar
        f2 = percentage_f32872f

        drawSeekBar2.m18488b(f2)
        invalidateSelf()
    }

    override fun draw(canvas: Canvas) = sliderBar.draw(canvas)

    override fun getIntrinsicHeight() =
        paddingTopWithoutQuestion + sliderBar.intrinsicHeight + paddingBottomWithoutQuestion

    override fun invalidateDrawable(drawable: Drawable) = invalidateSelf()

    override fun setAlpha(i: Int) {
        this.sliderStickerBackground!!.alpha = i
        this.sliderBar.alpha = i
    }

    override fun setBounds(left: Int, i2: Int, right: Int, i4: Int) {
        var top = (i2 + i4) / 2
        val height = intrinsicHeight / 2
        top += height

        this.sliderBar.setBounds(
            left + paddingHorizontal,
            top - sliderBar.intrinsicHeight - paddingBottomWithoutQuestion,
            right - paddingHorizontal,
            top - paddingBottomWithoutQuestion
        )
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        this.sliderStickerBackground!!.colorFilter = colorFilter
        this.sliderBar.colorFilter = colorFilter
    }
}
