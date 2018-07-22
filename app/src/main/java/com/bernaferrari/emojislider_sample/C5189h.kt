package com.bernaferrari.emojislider_sample

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.graphics.drawable.Drawable.Callback
import android.os.Build.VERSION
import android.support.v4.content.ContextCompat
import android.text.Layout.Alignment

class C5189h(context: Context) : Drawable(), Callback {
    val f20900a: textDrawable

    init {
        val resources = context.resources
        val b = ContextCompat.getColor(context, R.color.slider_sticker_question_text)
        val dimensionPixelSize =
            resources.getDimensionPixelSize(R.dimen.slider_sticker_question_text_size)
        this.f20900a = textDrawable(
            context,
            resources.getDimensionPixelSize(R.dimen.slider_sticker_question_width)
        )
        this.f20900a.m10646a(Alignment.ALIGN_CENTER)
        this.f20900a.callback = this
        this.f20900a.setColor(b)
        this.f20900a.setTextSize(dimensionPixelSize.toFloat())
        if (VERSION.SDK_INT >= 21) {
            //            this.f20900a.m10650c();
            //            this.f20900a.setTypeface(ac.m7427a());
            //            return;
        }
        this.f20900a.m10645a(Typeface.SANS_SERIF, 0)
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    override fun scheduleDrawable(drawable: Drawable, runnable: Runnable, j: Long) {}

    override fun unscheduleDrawable(drawable: Drawable, runnable: Runnable) {}

    override fun draw(canvas: Canvas) {
        val min = Math.min(1.0f, bounds.height().toFloat() / this.f20900a.intrinsicHeight.toFloat())
        canvas.save()
        canvas.scale(min, min, bounds.exactCenterX(), bounds.exactCenterY())
        try {
            this.f20900a.draw(canvas)
        } catch (e: Exception) {

        }

        canvas.restore()
    }

    override fun getIntrinsicHeight(): Int = f20900a.intrinsicHeight

    override fun getIntrinsicWidth(): Int = f20900a.intrinsicWidth

    override fun invalidateDrawable(drawable: Drawable) = invalidateSelf()

    override fun onBoundsChange(rect: Rect) {
        f20900a.setBounds(
            (rect.exactCenterX() - intrinsicWidth.toFloat() / 2.0f).toInt(),
            (rect.exactCenterY() - intrinsicHeight.toFloat() / 2.0f).toInt(),
            (rect.exactCenterX() + intrinsicWidth.toFloat() / 2.0f).toInt(),
            (rect.exactCenterY() + intrinsicHeight.toFloat() / 2.0f).toInt()
        )
    }

    override fun setAlpha(i: Int) {
        f20900a.alpha = i
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        f20900a.colorFilter = colorFilter
    }
}
