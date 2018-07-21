package com.bernaferrari.emojislider

import android.content.Context
import android.content.res.TypedArray
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.bernaferrari.emojislider.arrowpopupwindow.utils.Util

interface TrackingTouch {
    fun onStartTrackingTouch()
    fun onStopTrackingTouch()
    fun onProgressChanged(progress: Int)
}

open class EmojiSlider @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    val trackingTouch: TrackingTouch = object : TrackingTouch {
        override fun onStartTrackingTouch() = emojiHelper.progressStarted()

        override fun onStopTrackingTouch() = emojiHelper.onStopTrackingTouch()

        override fun onProgressChanged(progress: Int) = progressChanged(progress)
    }

    val sliderDrawable = SliderDrawable(context, trackingTouch)
    var emojiHelper = EmojiHelper(context)
    var emoji = "😍"

    private fun Int.limitToRange() = Math.max(Math.min(this, 100), 0)

    init {
        this.background = sliderDrawable

        // prevent render is in edit mode
//        if (isInEditMode) return

        val array = context.obtainStyledAttributes(attrs, R.styleable.EmojiSlider)

        try {

            sliderDrawable.updatePercentage(getProgress(array).limitToRange())

            sliderDrawable.drawSeekBar.setGradientBackground(getProgressGradientBackground(array))
            sliderDrawable.drawSeekBar.colorStart = getProgressGradientStart(array)
            sliderDrawable.drawSeekBar.colorEnd = getProgressGradientEnd(array)
            sliderDrawable.slider_padding = getHorizontalPadding(array)
            sliderDrawable.drawSeekBar.invalidateSelf()

            if (getEmojiGravity(array) == 0) {
                emojiHelper.direction = EmojiHelper.Direction.UP
            } else {
                emojiHelper.direction = EmojiHelper.Direction.DOWN
            }

            updateThumb(getEmoji(array))

//            cornerRadius = getCornerRadius(array)
//            minValue = getMinValue(array)
//            maxValue = getMaxValue(array)
//            minStartValue = getMinStartValue(array)
//            maxStartValue = getMaxStartValue(array)
//            steps = getSteps(array)
//            gap = getGap(array)
//            fixGap = getFixedGap(array)
//            _barHeight = getBarHeight(array)
//            barColorMode = getBarColorMode(array)
//            barColor = getBarColor(array)
//            barGradientStart = getBarGradientStart(array)
//            barGradientEnd = getBarGradientEnd(array)
//            barHighlightColorMode = getBarHighlightColorMode(array)
//            barHighlightColor = getBarHighlightColor(array)
//            barHighlightGradientStart = getBarHighlightGradientStart(array)
//            barHighlightGradientEnd = getBarHighlightGradientEnd(array)
//            leftThumbColorNormal = getLeftThumbColor(array)
//            rightThumbColorNormal = getRightThumbColor(array)
//            leftThumbColorPressed = getLeftThumbColorPressed(array)
//            rightThumbColorPressed = getRightThumbColorPressed(array)
//            leftDrawable = getLeftDrawable(array)
//            rightDrawable = getRightDrawable(array)
//            leftDrawablePressed = getLeftDrawablePressed(array)
//            rightDrawablePressed = getRightDrawablePressed(array)
//            thumbDiameter = getDiameter(array)
//            dataType = getDataType(array)
//            seekBarTouchEnabled = isSeekBarTouchEnabled(array)
        } finally {
            array.recycle()
        }
    }

    protected fun getProgress(typedArray: TypedArray): Int {
        return typedArray.getInt(R.styleable.EmojiSlider_progress, 10)
    }

    protected fun getMinValue(typedArray: TypedArray): Int {
        return typedArray.getInt(R.styleable.EmojiSlider_min_value, 0)
    }

    protected fun getMaxValue(typedArray: TypedArray): Int {
        return typedArray.getInt(R.styleable.EmojiSlider_min_value, 0)
    }

    protected fun getBarColor(typedArray: TypedArray): Int {
        return typedArray.getInt(R.styleable.EmojiSlider_min_value, 0)
    }

    protected fun getProgressGradientStart(typedArray: TypedArray): Int {
        return typedArray.getInt(
            R.styleable.EmojiSlider_bar_gradient_start,
            ContextCompat.getColor(context, R.color.slider_gradient_start)
        )
    }

    protected fun getProgressGradientEnd(typedArray: TypedArray): Int {
        return typedArray.getInt(
            R.styleable.EmojiSlider_bar_gradient_start,
            ContextCompat.getColor(context, R.color.slider_gradient_end)
        )
    }

    protected fun getProgressGradientBackground(typedArray: TypedArray): Int {
        return typedArray.getInt(
            R.styleable.EmojiSlider_horizontal_padding,
            ContextCompat.getColor(context, R.color.slider_gradient_background)
        )
    }

    protected fun getHorizontalPadding(typedArray: TypedArray): Int {
        return typedArray.getInt(
            R.styleable.EmojiSlider_horizontal_padding,
            context.resources.getDimensionPixelSize(R.dimen.slider_sticker_padding_horizontal) * 2
        )
    }

    protected fun getEmoji(typedArray: TypedArray): String {
        return typedArray.getString(R.styleable.EmojiSlider_emoji) ?: emoji
    }

    protected fun getEmojiGravity(typedArray: TypedArray): Int {
        return typedArray.getInt(R.styleable.EmojiSlider_gravity, 0)
    }

    var sliderParticleSystem: View? = null
        set(value) {
            field = value

            if (value?.background !is EmojiHelper) {
                value?.background = emojiHelper
            }
        }

    fun setBackgroundView(backgroundView: View, emojiHelper: EmojiHelper? = null) {
        if (emojiHelper != null) {
            this.emojiHelper = emojiHelper
        }

        sliderParticleSystem = backgroundView
    }

    fun progressChanged(progress: Int) {
        if (sliderParticleSystem == null) return

        val sliderLocation = IntArray(2)
        this.getLocationOnScreen(sliderLocation)

        val particleLocation = IntArray(2)
        sliderParticleSystem!!.getLocationOnScreen(particleLocation)

        this.emojiHelper.onProgressChanged(
            paddingLeft = sliderLocation[0].toFloat() + sliderDrawable.drawSeekBar.bounds.left + sliderDrawable.thumb.bounds.centerX() - particleLocation[0],
            paddingTop = sliderLocation[1].toFloat() + Util.DpToPx(
                context,
                32f
            ) - particleLocation[1]
        )

        this.emojiHelper.updateProgress(progress / 100f)
    }

    private fun updateThumb(emoji: String) {
        this.emoji = emoji
        sliderDrawable.thumb = generateThumb(
            context = this.context,
            text = emoji,
            size = R.dimen.slider_sticker_slider_handle_size
        )
        emojiHelper.emoji = emoji
        emojiHelper.invalidateSelf()
    }

    /**
     * Handles thumb selection and movement. Notifies listener callback on certain events.
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)
        performClick()
        sliderDrawable.onTouch(this, event)

        if (event.actionMasked == MotionEvent.ACTION_DOWN) {
            performClick()
        }
        // Call this method to handle the response, and
        // thereby enable accessibility services to
        // perform this action for a user who cannot
        // click the touchscreen.

        return true
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }
}