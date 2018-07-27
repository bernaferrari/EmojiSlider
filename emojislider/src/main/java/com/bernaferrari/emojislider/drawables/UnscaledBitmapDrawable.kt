package com.bernaferrari.emojislider.drawables

import android.graphics.*
import android.graphics.Shader.TileMode
import android.graphics.drawable.Drawable

/**
 * Generates a round drawable from a [bitmap] with unrestricted size.
 * [BitmapDrawable] will make sure the size is scaled correctly for [ResultDrawable].
 */
class UnscaledBitmapDrawable(private val bitmap: Bitmap) : Drawable() {
    private val paint =
        Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG or Paint.DITHER_FLAG)
    private val rect = RectF()

    init {
        paint.shader = BitmapShader(bitmap, TileMode.CLAMP, TileMode.CLAMP)
    }

    override fun draw(canvas: Canvas) {
        rect.set(bounds)
        if (rect.height() > rect.width()) {
            rect.inset(0.0f, (rect.height() - rect.width()) / 2.0f)
        } else if (rect.height() < rect.width()) {
            rect.inset((rect.width() - rect.height()) / 2.0f, 0.0f)
        }
        canvas.drawOval(rect, paint)
    }

    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT

    override fun getIntrinsicHeight(): Int = bitmap.height

    override fun getIntrinsicWidth(): Int = bitmap.width

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }
}
