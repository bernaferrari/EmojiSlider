package com.bernaferrari.emojislidersample.views

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.drawable.Drawable
import android.text.SpannableString
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.content.ContextCompat
import com.bernaferrari.emojislider.drawables.TextDrawable
import com.bernaferrari.emojislidersample.R
import com.bernaferrari.emojislidersample.extensions.dpToPixels
import com.bernaferrari.emojislidersample.extensions.mapValueFromRangeToRange

class EmojiPickerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
    defStyleAttr: Int = 0, defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes) {

    var progress: Float = 0f
    var emoji = "😍"
    var circleDrawable: Drawable? = null
    var insidePaint: Paint? = null

    fun updateColor() {
        // If the value is set here, it risks getting a solid color if width is blue.
        // This way, it will be refreshed on onDraw.
        circleDrawable = null
        insidePaint = null
        invalidate()
    }

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (circleDrawable == null) {
            circleDrawable = TextDrawable(context, width).apply {
                setSpannableValue(SpannableString(emoji))
                setTextSize(context.resources.displayMetrics.density * TEXT_SIZE)
            }
        }

        if (insidePaint == null) {
            insidePaint = createPaintInside()
        }

        val largeRadius = Math.min(width, height) / 2

        canvas.drawCircle(
            width / 2f,
            height / 2f,
            (largeRadius - dpToPixels(CIRCLE_PADDING, context)) * progress,
            insidePaint
        )

        canvas.save()
        val scale = (ANIM_MAX_VALUE - progress).mapValueFromRangeToRange(
            ANIM_MIN_VALUE, ANIM_MAX_VALUE, ANIM_MAPPED_MIN_VALUE, ANIM_MAX_VALUE
        )

        canvas.scale(scale, scale, width / 2f, height / 2f)
        circleDrawable?.draw(canvas)
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
            startAnimation(ANIM_MIN_VALUE, ANIM_MAX_VALUE, animated)
            this.isSelected = true
        }
    }

    fun deselectIfSelected(animated: Boolean) {
        if (this.isSelected) {
            startAnimation(ANIM_MAX_VALUE, ANIM_MIN_VALUE, animated)
            this.isSelected = false
        }
    }

    private fun startAnimation(fromValue: Float, toValue: Float, isAnimated: Boolean) {
        if (isAnimated) {
            val ofFloat = ObjectAnimator.ofFloat(fromValue, toValue)
            ofFloat.duration = ANIM_DURATION
            ofFloat.interpolator = AccelerateDecelerateInterpolator()
            ofFloat.addUpdateListener(updateListener())
            ofFloat.start()
            return
        }
        this.progress = toValue
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
            SHADER_MARGIN,
            SHADER_MARGIN,
            height.toFloat(),
            ContextCompat.getColor(context, R.color.md_orange_A100),
            ContextCompat.getColor(context, R.color.md_deep_orange_A200),
            Shader.TileMode.MIRROR
        )
        return paint
    }

    private companion object {
        private const val ANIM_MAX_VALUE = 1.0f
        private const val ANIM_MIN_VALUE = 0.0f
        private const val ANIM_MAPPED_MIN_VALUE = 0.7f
        private const val ANIM_DURATION = 250L
        private const val SHADER_MARGIN = 0f
        private const val CIRCLE_PADDING = 2f
        private const val TEXT_SIZE = 28
    }
}
