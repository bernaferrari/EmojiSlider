package com.bernaferrari.emojislider

import android.content.Context
import android.graphics.*
import android.graphics.PorterDuff.Mode
import android.graphics.drawable.Drawable
import android.graphics.drawable.Drawable.Callback
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener

class Bolinha(context: Context) : Drawable(), Callback, OnTouchListener {
    val paddingHorizontal: Int
    val handleSize: C7848b
    private val paddingTopWithQuestion: Int
    private val paddingTopWithoutQuestion: Int
    private val paddingBottomWithQuestion: Int
    private val paddingBottomWithoutQuestion: Int
    private val questionMargin: Int
    private val shadowSize: Int
    private val stickerWidth: Int
    private val sliderStickerBackground: Drawable?
    private val stickerBackground: Drawable?
    private val f32882p: C5189h
    var f32869c: Boolean = false
    var f32870d: Int = 0
    var f32871e: C5179d? = null
    var percentage_f32872f: Float = 0.5f

    init {
        val resources = context.resources
        this.paddingTopWithQuestion =
                resources.getDimensionPixelSize(R.dimen.slider_sticker_padding_top_with_question)
        this.paddingTopWithoutQuestion =
                resources.getDimensionPixelSize(R.dimen.slider_sticker_padding_top_without_question)
        this.paddingBottomWithQuestion =
                resources.getDimensionPixelSize(R.dimen.slider_sticker_padding_bottom_with_question)
        this.paddingBottomWithoutQuestion =
                resources.getDimensionPixelSize(R.dimen.slider_sticker_padding_bottom_without_question)
        this.paddingHorizontal =
                resources.getDimensionPixelSize(R.dimen.slider_sticker_padding_horizontal)
        this.questionMargin =
                resources.getDimensionPixelSize(R.dimen.slider_sticker_question_margin)
        this.shadowSize = resources.getDimensionPixelSize(R.dimen.slider_sticker_shadow_size)
        this.stickerWidth = resources.getDimensionPixelSize(R.dimen.slider_sticker_width)
        this.sliderStickerBackground =
                ContextCompat.getDrawable(context, R.drawable.slider_sticker_background)
        this.sliderStickerBackground!!.callback = this
        this.stickerBackground = ContextCompat.getDrawable(
            context,
            R.drawable.slider_sticker_background
        )//question_background_shadow);
        this.stickerBackground!!.callback = this
        this.f32882p = C5189h(context)
        this.f32882p.callback = this
        val c5189h = this.f32882p
        c5189h.f20900a.setColor(
            ContextCompat.getColor(
                context,
                R.color.slider_sticker_question_text
            )
        )
        c5189h.invalidateSelf()
        this.handleSize = C7848b(context)
        this.handleSize.callback = this
        this.handleSize.m18483a(resources.getDimensionPixelSize(R.dimen.slider_sticker_slider_handle_size))
        var c7848b = this.handleSize
        c7848b.sliderHeight = resources.getDimensionPixelSize(R.dimen.slider_sticker_slider_height)
        c7848b.invalidateSelf()
        this.handleSize.m18489b(resources.getDimensionPixelSize(R.dimen.slider_sticker_slider_track_height))
        c7848b = this.handleSize
        val voteAverageHandleSize =
            resources.getDimensionPixelSize(R.dimen.slider_sticker_slider_vote_average_handle_size)
        val circleHandleC5190I = c7848b.averageCircleHandle_f32835b
        circleHandleC5190I.radius_f20901a = voteAverageHandleSize.toFloat() / 2.0f
        circleHandleC5190I.invalidateSelf()
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    override fun scheduleDrawable(drawable: Drawable, runnable: Runnable, j: Long) {}

    override fun unscheduleDrawable(drawable: Drawable, runnable: Runnable) {}

    fun m18502b(): Int {
        return if (m18500d()) this.paddingBottomWithQuestion else this.paddingBottomWithoutQuestion
    }

    fun m18503c() {
        val color: Int
        var str: String?
        var i2: Int
        var charSequence: CharSequence
        var c5189h: C5189h
        var c7848b: C7848b
        var circleHandleC5190I: CircleHandle_C5190i
        var somecolor3: Int
        var c7848b2: C7848b
        var percentage_f: Float
        var c7848b3: C7848b
        var f2: Float
        if (this.f32871e == null) {
            color = -1
        } else {
            str = this.f32871e!!.backgroundColorf20864e
            if (str == null) {
                color = 0
            } else {
                color = Color.parseColor(str)
            }
        }
        if (this.f32871e == null) {
            i2 = -16777216
        } else {
            str = this.f32871e!!.f20868i
            if (str == null) {
                i2 = 0
            } else {
                i2 = Color.parseColor(str)
            }
        }
        this.sliderStickerBackground!!.mutate().colorFilter = PorterDuffColorFilter(color, Mode.SRC)
        val c5189h2 = this.f32882p
        if (this.f32871e != null) {
            if (this.f32871e!!.f20867h != null) {
                charSequence = this.f32871e!!.f20867h
                //                c5189h2.f20900a.m10647a(new SpannableString(charSequence));
                c5189h2.invalidateSelf()
                c5189h = this.f32882p
                c5189h.f20900a.setColor(i2)
                c5189h.invalidateSelf()
                c7848b = this.handleSize
                circleHandleC5190I = c7848b.averageCircleHandle_f32835b
                circleHandleC5190I.color_f20902b = color
                circleHandleC5190I.invalidateSelf()
                somecolor3 = if (color != -1) {
                    c7848b.gradientBackground
                } else {
                    C3395a.m7509b(color)
                }

                c7848b.paint_f32836c.color = somecolor3

                if (i2 == -1) {
                    i2 = c7848b.gradientEnd
                }
//                c7848b.color1_f32846m = i2
                c7848b.m18484a(c7848b.bounds)
                c7848b.invalidateSelf()
                if (this.percentage_f32872f == 0f) {
                    c7848b2 = this.handleSize
                    //                    am amVar = this.percentage_f32872f.f20870b;
                    //                    C7849d c7849d = c7848b2.bigCircleThumb_f32834a;
                    //                    C7852l c7852l = c7849d.f32862c;
                    //                    C3139c b = ab.f13242h.m6994b(amVar.f36630d);
                    //                    b.f13336b = new WeakReference(c7852l);
                    //                    ab.f13242h.m6992a(b.m7019a());
                    //                    c7849d.invalidateSelf();
                    this.handleSize.m18491b(C5186e.USER)
                    c7848b = this.handleSize
                    if (this.f32871e != null) {
                        if (this.f32871e!!.f20861b == -1) {
                            if (this.f32871e!!.m9938a()) {
                                i2 = this.f32871e!!.f20861b
                                percentage_f = (i2 * this.f32871e!!.f20863d + percentage_f32872f) /
                                        (i2 + 1).toFloat()
                            } else {
                                percentage_f = this.f32871e!!.f20863d
                            }
                            c7848b.f32843j = !c7848b.f32842i
                            c7848b.f32842i = true
                            c7848b.percentage_f32848o = percentage_f
                            if (c7848b.f32843j) {
                                //                                c7848b.f32853t.m4030b(1.0d);
                            }
                            c7848b.invalidateSelf()
                        }
                    }

                    c7848b.f32843j = !c7848b.f32842i
                    c7848b.f32842i = true
                    c7848b.percentage_f32848o = percentage_f32872f
                    if (c7848b.f32843j) {
                        //                        c7848b.f32853t.m4030b(1.0d);
                    }
                    c7848b.invalidateSelf()
                } else {
                    c7848b2 = this.handleSize
                    if (this.f32871e != null) {
                        if (this.f32871e!!.f20865f == null) {
                            str = f32871e!!.f20865f
                            c7848b2.m18487a(str)
                            this.handleSize.m18486a(C5186e.EMOJI)
                            c7848b3 = this.handleSize
                            c7848b3.f32843j = false
                            c7848b3.f32842i = false
                            c7848b3.invalidateSelf()
                        }
                    }
                    str = "😍"
                    c7848b2.m18487a(str)
                    this.handleSize.m18486a(C5186e.EMOJI)
                    c7848b3 = this.handleSize
                    c7848b3.f32843j = false
                    c7848b3.f32842i = false
                    c7848b3.invalidateSelf()
                }
                if (this.percentage_f32872f == 0f) {
                    c7848b2 = this.handleSize
                    f2 = percentage_f32872f
                } else {
                    c7848b2 = this.handleSize
                    if (this.f32871e != null) {
                        if (!this.f32871e!!.m9938a()) {
                            f2 = this.f32871e!!.f20862c
                        }
                    }
                    f2 = 0.1f
                }
                c7848b2.m18488b(f2)
                invalidateSelf()
            }
        }
        charSequence = ""
        //        c5189h2.f20900a.m10647a(new SpannableString(charSequence));
        c5189h2.invalidateSelf()
        c5189h = this.f32882p
        c5189h.f20900a.setColor(i2)
        c5189h.invalidateSelf()
        c7848b = this.handleSize
        circleHandleC5190I = c7848b.averageCircleHandle_f32835b
        circleHandleC5190I.color_f20902b = color
        circleHandleC5190I.invalidateSelf()
        if (color != -1) {
            somecolor3 = C3395a.m7509b(color)
        } else {
            somecolor3 = c7848b.gradientBackground
        }

        c7848b.paint_f32836c.color = somecolor3
        if (i2 != -1) {
            somecolor3 = c7848b.gradientStart
        } else {
            somecolor3 = i2
        }
//        c7848b.color0_f32845l = somecolor3
        if (i2 == -1) {
            i2 = c7848b.gradientEnd
        }
//        c7848b.color1_f32846m = i2
        c7848b.m18484a(c7848b.bounds)
        c7848b.invalidateSelf()
        if (this.percentage_f32872f == null) {
            c7848b2 = this.handleSize
            if (this.f32871e != null) {
                if (this.f32871e!!.f20865f == null) {
                    str = this.f32871e!!.f20865f
                    c7848b2.m18487a(str)
                    this.handleSize.m18486a(C5186e.EMOJI)
                    c7848b3 = this.handleSize
                    c7848b3.f32843j = false
                    c7848b3.f32842i = false
                    c7848b3.invalidateSelf()
                }
            }
            str = "😍"
            c7848b2.m18487a(str)
            this.handleSize.m18486a(C5186e.EMOJI)
            c7848b3 = this.handleSize
            c7848b3.f32843j = false
            c7848b3.f32842i = false
            c7848b3.invalidateSelf()
        } else {
            c7848b2 = this.handleSize
            //            am amVar2 = this.percentage_f32872f.f20870b;
            //            C7849d c7849d2 = c7848b2.bigCircleThumb_f32834a;
            //            C7852l c7852l2 = c7849d2.f32862c;
            //            C3139c b2 = ab.f13242h.m6994b(amVar2.f36630d);
            //            b2.f13336b = new WeakReference(c7852l2);
            //            ab.f13242h.m6992a(b2.m7019a());
            //            c7849d2.invalidateSelf();
            this.handleSize.m18491b(C5186e.USER)
            c7848b = this.handleSize
            if (this.f32871e != null) {
                if (this.f32871e!!.f20861b == -1) {
                    if (this.f32871e!!.m9938a()) {
                        i2 = this.f32871e!!.f20861b
                        percentage_f = (i2.toFloat() * this.f32871e!!.f20863d + percentage_f32872f) /
                                (i2 + 1).toFloat()
                    } else {
                        percentage_f = this.f32871e!!.f20863d
                    }
                    c7848b.f32843j = !c7848b.f32842i
                    c7848b.f32842i = true
                    c7848b.percentage_f32848o = percentage_f
                    if (c7848b.f32843j) {
                        //                        c7848b.f32853t.m4030b(1.0d);
                    }
                    c7848b.invalidateSelf()
                }
            }
            percentage_f = percentage_f32872f
            c7848b.f32843j = !c7848b.f32842i
            c7848b.f32842i = true
            c7848b.percentage_f32848o = percentage_f
            if (c7848b.f32843j) {
                //                c7848b.f32853t.m4030b(1.0d);
            }
            c7848b.invalidateSelf()
        }
        if (this.percentage_f32872f == null) {
            c7848b2 = this.handleSize
            if (this.f32871e != null) {
                if (!this.f32871e!!.m9938a()) {
                    f2 = this.f32871e!!.f20862c
                }
            }
            f2 = 0.1f
        } else {
            c7848b2 = this.handleSize
            f2 = percentage_f32872f
        }
        c7848b2.m18488b(f2)
        invalidateSelf()
    }

    private fun m18500d(): Boolean {
        return this.f32871e != null && !TextUtils.isEmpty(this.f32871e!!.f20867h)
    }

    override fun draw(canvas: Canvas) {
//        if (this.f32869c) {
//            this.stickerBackground!!.draw(canvas)
//        }
//        this.sliderStickerBackground!!.draw(canvas)
        this.handleSize.draw(canvas)
//        if (m18500d()) {
//            this.f32882p.draw(canvas)
//        }
    }

    override fun getIntrinsicHeight(): Int {
        if (this.f32870d > 0) {
            return this.f32870d
        }
        val intrinsicHeight = this.handleSize.intrinsicHeight
        return if (!m18500d()) {
            this.paddingTopWithoutQuestion + intrinsicHeight + this.paddingBottomWithoutQuestion
        } else this.paddingTopWithQuestion + this.f32882p.intrinsicHeight + this.questionMargin + intrinsicHeight + this.paddingBottomWithQuestion
    }

    override fun getIntrinsicWidth(): Int {
        return this.stickerWidth
    }

    override fun invalidateDrawable(drawable: Drawable) {
        invalidateSelf()
    }

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        return this.handleSize.onTouch(view, motionEvent)
    }

    override fun setAlpha(i: Int) {
        this.sliderStickerBackground!!.alpha = i
        this.handleSize.alpha = i
        this.f32882p.alpha = i
    }

    override fun setBounds(i: Int, i2: Int, i3: Int, i4: Int) {
        //        super.setBounds(i, i2, i3, i4);
        val i5 = (i + i3) / 2
        var i6 = (i2 + i4) / 2
        val intrinsicHeight = intrinsicHeight
        val i7 = intrinsicHeight / 2
        val i8 = i6 - i7
        i6 += i7
        this.sliderStickerBackground!!.setBounds(i, i8, i3, i6)
        this.stickerBackground!!.setBounds(
            i - this.shadowSize,
            i8 - this.shadowSize,
            this.shadowSize + i3,
            this.shadowSize + i6
        )
        this.handleSize.setBounds(
            i + this.paddingHorizontal,
            i6 - this.handleSize.intrinsicHeight - m18502b(),
            i3 - this.paddingHorizontal,
            i6 - m18502b()
        )
        if (m18500d()) {
            this.f32882p.setBounds(
                i5 - this.f32882p.intrinsicWidth / 2,
                this.paddingTopWithQuestion + i8,
                i5 + this.f32882p.intrinsicWidth / 2,
                i8 + this.paddingTopWithQuestion + (intrinsicHeight - this.paddingBottomWithQuestion - this.questionMargin - this.handleSize.intrinsicHeight - this.paddingBottomWithQuestion)
            )
        }
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        this.sliderStickerBackground!!.colorFilter = colorFilter
        this.handleSize.colorFilter = colorFilter
        this.f32882p.colorFilter = colorFilter
    }
}
