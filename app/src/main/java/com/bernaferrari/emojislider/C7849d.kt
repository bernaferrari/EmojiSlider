package com.bernaferrari.emojislider

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.graphics.drawable.Drawable.Callback

import com.bernaferrari.emojislider.p228m.C1559f
import com.bernaferrari.emojislider.p228m.C1561h
import com.bernaferrari.emojislider.p228m.C6724v
import com.bernaferrari.emojislider.p228m.SPRING_C1558e

class C7849d(context: Context) : Drawable(), Callback, C1561h {
    internal val f32860a: textDrawable
    internal val imageHandle_f32861BAverage: AverageCircle
    internal val f32862c: C7852l
    internal val f32863d = C6724v.m13495c().m4019a().m4028a(this).setCurrentValuem4025a(1.0)
        .m4027a(C1559f.m4037a(40.0, 7.0))
    internal var f32864e: Float = 0.toFloat()
    internal var f32865f: C5186e? = null
    internal var f32866g: C5186e? = null

    init {
        this.f32860a = textDrawable(context, getWidthPixels(context))
        this.f32860a.callback = this
        this.imageHandle_f32861BAverage = AverageCircle(context)
        this.imageHandle_f32861BAverage.callback = this
        this.f32862c = C7852l()
        this.f32862c.callback = this
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    override fun scheduleDrawable(drawable: Drawable, runnable: Runnable, j: Long) {}

    override fun unscheduleDrawable(drawable: Drawable, runnable: Runnable) {}

    fun getWidthPixels(context: Context): Int {
        return context.resources.displayMetrics.widthPixels
    }

    private fun m18494a(canvas: Canvas, c5186e: C5186e, f: Float) {
        val drawable: Drawable

        val f20875a = IntArray(C5186e.values().size)/* synthetic */

        //        Logger.d("f20875a ordinal value is: " + f20875a[c5186e.ordinal()]);
        when (f20875a[c5186e.ordinal]) {
            1 -> drawable = this.imageHandle_f32861BAverage
            2 -> drawable = this.imageHandle_f32861BAverage
            3 -> drawable = this.imageHandle_f32861BAverage
            else -> drawable = this.imageHandle_f32861BAverage
        }//                StringBuilder stringBuilder = new StringBuilder("Unsupported handle type: ");
        //                stringBuilder.append(this.f32865f);
        //                throw new IllegalStateException(stringBuilder.toString());
        val intrinsicWidth = (this.f32864e - drawable.getIntrinsicWidth().toFloat()) / 2.0f
        val intrinsicHeight = (this.f32864e - drawable.getIntrinsicHeight().toFloat()) / 2.0f
        canvas.save()
        canvas.translate(intrinsicWidth, intrinsicHeight)
        canvas.scale(f, f, bounds.exactCenterX(), bounds.exactCenterY())
        drawable.draw(canvas)
        canvas.restore()
    }

    fun mo964a() {
        invalidateSelf()
    }

    fun m18496a(c5186e: C5186e) {
        this.f32865f = c5186e
        this.f32866g = null
        this.f32863d.setCurrentValuem4026a(1.0, true)
        invalidateSelf()
    }

    override fun draw(canvas: Canvas) {
        val f = this.f32863d.f6940d.f6934a.toFloat()
        if (this.f32866g != null && f < 1.0f) {
            m18494a(canvas, this.f32866g!!, 1.0f - f)
        }
        if (this.f32865f != null && f > 0.0f) {
            m18494a(canvas, this.f32865f!!, f)
        }
    }

    override fun getIntrinsicHeight(): Int {
        return this.f32864e.toInt()
    }

    override fun getIntrinsicWidth(): Int {
        return this.f32864e.toInt()
    }

    override fun invalidateDrawable(drawable: Drawable) {
        invalidateSelf()
    }

    override fun setAlpha(i: Int) {
        this.f32860a.alpha = i
        this.imageHandle_f32861BAverage.alpha = i
        this.f32862c.alpha = i
    }

    override fun setBounds(i: Int, i2: Int, i3: Int, i4: Int) {
        super.setBounds(i, i2, i3, i4)
        this.f32860a.setBounds(i, i2, i3, i4)
        this.imageHandle_f32861BAverage.setBounds(i, i2, i3, i4)
        this.f32862c.setBounds(i, i2, i3, i4)
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        this.f32860a.colorFilter = colorFilter
        this.imageHandle_f32861BAverage.colorFilter = colorFilter
        this.f32862c.colorFilter = colorFilter
    }

    override fun mo964a(SPRINGC1558E: SPRING_C1558e) {
        invalidateSelf()
    }

    override fun mo965b(SPRINGC1558E: SPRING_C1558e) {

    }

    override fun mo966c(SPRINGC1558E: SPRING_C1558e) {

    }

    override fun mo967d(SPRINGC1558E: SPRING_C1558e) {

    }
}