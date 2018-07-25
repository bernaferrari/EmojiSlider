package com.bernaferrari.emojisliderSample.views

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.text.SpannableString
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import com.bernaferrari.emojislider.TextDrawable
import com.bernaferrari.emojisliderSample.R
import com.facebook.rebound.SpringUtil

class EmojiPickerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
    defStyleAttr: Int = 0, defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes) {

    var progress: Float = 0f
    var emoji = "😍"
    var circlePaint: Drawable? = null
    var outlinePaint: Paint? = null

    fun updateColor() {
        // If the value is set here, it risks getting a solid color if width is blue.
        // This way, it will be refreshed on onDraw.
        circlePaint = null
        outlinePaint = null
        invalidate()
    }

    init {
        setLayerType(1, null)
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
            circlePaint = TextDrawable(context, width).apply {
                setSpannableValue(SpannableString(emoji))
                setTextSize(context.resources.displayMetrics.density * 28)
            }
        }

        if (outlinePaint == null) {
            outlinePaint = createPaintInside()
        }

        canvas.drawCircle(
            width.toFloat() / 2.0f,
            height.toFloat() / 2.0f,
            (Math.min(
                width,
                height
            ).toFloat() / 2.0f - dpToPixels(2)) * this.progress + 0.0f * (1.toFloat() - this.progress),
            outlinePaint
        )

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

    private fun createPaintInside(): Paint {
        val paint = Paint()
        paint.isAntiAlias = true
        paint.style = Paint.Style.FILL
        paint.shader = LinearGradient(
            width.toFloat(),
            0f,
            0f,
            height.toFloat(),
            ContextCompat.getColor(context, R.color.md_orange_A100),
            ContextCompat.getColor(context, R.color.md_deep_orange_A200),
            Shader.TileMode.MIRROR
        )
        return paint
    }
}