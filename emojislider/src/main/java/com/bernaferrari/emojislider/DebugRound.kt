package com.bernaferrari.emojislider

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable


class DebugRound(val context: Context, val width: Int, val height: Int) : Drawable() {

    override fun draw(canvas: Canvas) {
        drawRedPaint(canvas)
        drawLines(canvas)
    }

    override fun getIntrinsicHeight() = height

    override fun getIntrinsicWidth() = width

    val RdYlGn = listOf(
        0xffa50026,
        0xffd73027,
        0xfff46d43,
        0xfffdae61,
        0xffffffbf,
        0xffd9ef8b,
        0xffa6d96a,
        0xff66bd63,
        0xff1a9850,
        0xff006837
    )

    val rulerColor = 0xff3DFDFF.toInt()

    val color2 = listOf(
        0xffa50026,
        0xffd73027,
        0xfff46d43,
        0xfffdae61,
        0xfffee090,
        0xffffffbf,
        0xffe0f3f8,
        0xffabd9e9,
        0xff74add1,
        0xff4575b4,
        0xff313695
    )

    enum class limiting {
        VERTICAL, HORIZONTAL, BIGGER, SMALLER
    }

    var maxRadius = 0

    var lim = limiting.BIGGER
        set(value) {
            field = value

            maxRadius = when (lim) {
                limiting.VERTICAL -> height
                limiting.HORIZONTAL -> width
                limiting.BIGGER -> Math.max(height, width)
                limiting.SMALLER -> Math.min(height, width)
            } / 2
        }

    // used for debugging
    private fun drawRedPaint(canvas: Canvas) {
        lim = limiting.BIGGER

        println("intrinsincWidth: $width + intrinsicHeight: $height")

        val paint = Paint()

        for (i in 10 downTo 1) {
            paint.color = RdYlGn[i - 1].toInt()
            canvas.drawCircle(
                width / 2f,
                height / 2f,
                (maxRadius / 10f) * i,
                paint
            )
        }
    }

    // used for debugging
    private fun drawLines(canvas: Canvas) {

        println("intrinsincWidth: $width + intrinsicHeight: $height")
        val paint = Paint()
        paint.color = -1

//        drawHorizontalPercentLine(canvas, 0.25f, paint.color, true)
//        drawHorizontalPercentLine(canvas, 0.75f, paint.color, true)
//        drawVerticalPercentLine(canvas, 0.25f, paint.color, true)
//        drawVerticalPercentLine(canvas, 0.75f, paint.color, true)
        drawGrid(canvas, 8f, 0f, paint.color)
    }

    fun drawGrid(canvas: Canvas, dpSize: Float, dpPadding: Float = 0f, color: Int) {
        canvas.save()

        val paint = Paint()
        paint.color = rulerColor

        val padding = dpPadding * density

        val numberOfColumns = (width / (dpSize * density)).toInt()
        for (i in 1..numberOfColumns) {
            canvas.drawLine(
                padding + (i / numberOfColumns.toFloat()) * width,
                0f,
                padding + (i / numberOfColumns.toFloat()) * width,
                height.toFloat(),
                paint
            )
        }

        val numberOfRows = (width / (dpSize * density)).toInt()
        for (j in 1..numberOfRows) {
            canvas.drawLine(
                0f,
                padding + (j / numberOfRows.toFloat()) * width,
                width.toFloat(),
                padding + (j / numberOfRows.toFloat()) * width,
                paint
            )
        }

        canvas.restore()
    }

    fun Double.toCos() = Math.cos(Math.toRadians(this))

    fun Double.toSin() = Math.sin(Math.toRadians(this))

    fun drawVerticalPercentLine(canvas: Canvas, percent: Float, color: Int, showText: Boolean) {
        canvas.save()

        val paint = Paint()
        paint.color = rulerColor

        canvas.drawLine(
            percent * width.toFloat(),
            0f,
            percent * width.toFloat(),
            height.toFloat(),
            paint
        )

        if (showText) {
            canvas.translate(density * 2 + width * percent, density * 2)
            paint.textSize = context.resources.getDimension(R.dimen.slider_sticker_corner_radius)
            val info = "${"%.0f".format(percent * 100)}%"
            canvas.drawText(info, 0, info.length, density * 2, density * 8, paint)
        }

        canvas.restore()
    }

    fun drawHorizontalPercentLine(canvas: Canvas, percent: Float, color: Int, showText: Boolean) {
        canvas.save()

        val paint = Paint()
        paint.color = rulerColor

        canvas.drawLine(
            0f,
            percent * height.toFloat(),
            width.toFloat(),
            percent * height.toFloat(),
            paint
        )

        if (showText) {
            canvas.translate(density * 2, density * 2 + height * percent)
            paint.textSize = context.resources.getDimension(R.dimen.slider_sticker_corner_radius)
            val info = "${"%.0f".format(percent * 100)}%"
            canvas.drawText(info, 0, info.length, density * 2, -density * 6, paint)
        }

        canvas.restore()
    }

    val density = context.resources.displayMetrics.density

    fun drawRuleAngled(canvas: Canvas, angleF: Float, color: Int, showText: Boolean) {
        canvas.save()

        val angle = angleF.toDouble()

        val xValue = (maxRadius * angle.toCos()).toFloat()
        val yValue = -(maxRadius * angle.toSin()).toFloat()

        val paint = Paint()
        paint.color = rulerColor

        canvas.translate(width / 2f, height / 2f)

        canvas.drawLine(
            xValue,
            yValue,
            0f, //-xValue,
            0f, //-yValue,
            paint
        )

        if (showText) {
            canvas.translate(
                (width / 2f * angle.toCos()).toFloat(),
                -(height / 2f * angle.toSin()).toFloat()
            )

            paint.textSize = context.resources.getDimension(R.dimen.slider_sticker_corner_radius)
            val info = "${"%.0f".format(angle)}%"
            canvas.drawText(info, 0, info.length, density * 2, -density * 6, paint)
        }

        canvas.restore()
    }

    override fun setAlpha(alpha: Int) = Unit

    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT

    override fun setColorFilter(colorFilter: ColorFilter?) = Unit
}