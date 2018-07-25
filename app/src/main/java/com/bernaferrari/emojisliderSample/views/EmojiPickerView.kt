package com.bernaferrari.emojisliderSample.views

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.text.SpannableString
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import com.bernaferrari.emojislider.TextDrawable
import com.facebook.rebound.SpringUtil

class EmojiPickerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
    defStyleAttr: Int = 0, defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes) {

    var progress: Float = 0f
    var emoji = "😍"
    var circlePaint: Drawable? = null
    var outlinePaint: Paint? = null
    var color = Color.WHITE

    fun updateColor() {
        // If the value is set here, it risks getting a solid color if width is blue.
        // This way, it will be refreshed on onDraw.
        circlePaint = null
        outlinePaint = null
        invalidate()
    }

    fun areColorsSet(): Boolean = circlePaint != null && outlinePaint != null

    init {
        setLayerType(1, null)
    }

    fun setOutlineColor(i: Int) {
        outlinePaint?.color = i
        invalidate()
    }

    private fun dpToPixels(i: Int): Float {
        return TypedValue.applyDimension(
            1,
            i.toFloat(),
            context.resources.displayMetrics
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (circlePaint == null) {


            val min = Math.min(width, height) / 2.0f - dpToPixels(6) * this.progress

            println("pixels1: $width min: $min")

            circlePaint = TextDrawable(context, width).apply {
                setSpannableValue(SpannableString(emoji))
                setTextSize(context.resources.displayMetrics.density * 28)
            }
        }

        if (outlinePaint == null) {
            outlinePaint = createPaintOutside()
        }

        canvas.save()
        val mappedValue =
            SpringUtil.mapValueFromRangeToRange(1 - progress.toDouble(), 0.0, 1.0, 0.7, 1.0)
                .toFloat()
        canvas.scale(mappedValue, mappedValue, width / 2f, height / 2f)
        circlePaint?.draw(canvas)
        canvas.restore()
    }

    fun reverseSelection() {
        if (this.isSelected) {
            deselectIfSelected(true)
        } else {
            selectIfDeselected(true)
        }
    }

    fun selectIfDeselected(animated: Boolean) {
        if (!this.isSelected) {
            startAnimation(0.0f, 1.0f, animated)
            this.isSelected = true
        }
    }

    fun deselectIfSelected(animated: Boolean) {
        if (this.isSelected) {
            startAnimation(1.0f, 0.0f, animated)
            this.isSelected = false
        }
    }

    private fun startAnimation(f: Float, f2: Float, z: Boolean) {
        if (z) {
            val ofFloat = ObjectAnimator.ofFloat(f, f2)
            ofFloat.duration = 250
            ofFloat.interpolator = AccelerateDecelerateInterpolator()
            ofFloat.addUpdateListener(updateListener())
            ofFloat.start()
            return
        }
        this.progress = f2
        invalidate()
    }

    private fun updateListener(): ValueAnimator.AnimatorUpdateListener =
        ValueAnimator.AnimatorUpdateListener { valueAnimator2 ->
            val valueAnimator = valueAnimator2?.animatedValue ?: throw NullPointerException()
            this.progress = (valueAnimator as Float).toFloat()
            this.invalidate()
        }

    private fun createPaintOutside(): Paint {
        val paint = Paint()
        paint.isAntiAlias = true
        paint.color = Color.WHITE
        paint.shader = LinearGradient(
            width.toFloat(), 0f, 0f, height.toFloat(),
            color,
            color,
            Shader.TileMode.CLAMP
        )
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = dpToPixels(3)

        return paint
    }
}