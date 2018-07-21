package com.bernaferrari.emojislider2

import android.content.Context
import android.content.res.TypedArray
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

open class EmojiSlider @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    val trackingTouch: TrackingTouch = object : TrackingTouch {
        override fun onStartTrackingTouch() = emojiHelper.progressStarted(emoji)

        override fun onStopTrackingTouch() = emojiHelper.onStopTrackingTouch()

        override fun onProgressChanged(progress: Int) = progressChanged(progress)
    }

    val sliderDrawable = SliderDrawable(context, trackingTouch)
    var emojiHelper = FlyingEmoji(context)
    var emoji = "😍"
    var sliderParticleSystem: View? = null
        set(value) {
            field = value

            if (value?.background !is FlyingEmoji) {
                value?.background = emojiHelper
            } else {
                emojiHelper = value.background as FlyingEmoji
            }
        }


    private fun Int.limitToRange() = Math.max(Math.min(this, 100), 0)

    init {
        this.background = sliderDrawable

        // prevent render is in edit mode
//        if (isInEditMode) return

        val array = context.obtainStyledAttributes(attrs, R.styleable.EmojiSlider)

        try {

            sliderDrawable.updatePercentage(getProgress(array).limitToRange())

            sliderDrawable.sliderBar.setGradientBackground(getProgressGradientBackground(array))
            sliderDrawable.sliderBar.colorStart = getProgressGradientStart(array)
            sliderDrawable.sliderBar.colorEnd = getProgressGradientEnd(array)

            sliderDrawable.colorStart = getProgressGradientStart(array)
            sliderDrawable.colorEnd = getProgressGradientEnd(array)

            sliderDrawable.sliderPadding = getHorizontalPadding(array)
            sliderDrawable.isThumbAllowedToScrollEverywhere = getThumbAllowScrollAnywhere(array)
            sliderDrawable.thumbAllowReselection = getAllowReselection(array)
            sliderDrawable.sliderBar.invalidateSelf()

            sliderDrawable.averagePercentValue = getAverageProgress(array) / 100f

            if (getEmojiGravity(array) == 0) {
                emojiHelper.direction = FlyingEmoji.Direction.UP
            } else {
                emojiHelper.direction = FlyingEmoji.Direction.DOWN
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

    protected fun getThumbAllowScrollAnywhere(typedArray: TypedArray): Boolean {
        return typedArray.getBoolean(R.styleable.EmojiSlider_thumb_allow_scroll_anywhere, true)
    }

    protected fun getAllowReselection(typedArray: TypedArray): Boolean {
        return typedArray.getBoolean(R.styleable.EmojiSlider_allow_reselection, true)
    }

    protected fun getAverageProgress(typedArray: TypedArray): Int {
        return typedArray.getInt(R.styleable.EmojiSlider_average_progress, 50)
    }

    fun progressChanged(progress: Int) {
        if (sliderParticleSystem == null) return

        val sliderLocation = IntArray(2)
        this.getLocationOnScreen(sliderLocation)

        val particleLocation = IntArray(2)
        sliderParticleSystem!!.getLocationOnScreen(particleLocation)

        this.emojiHelper.onProgressChanged(
            paddingLeft = sliderLocation[0].toFloat() + sliderDrawable.sliderBar.bounds.left + sliderDrawable.thumb.bounds.centerX() - particleLocation[0],
            paddingTop = sliderLocation[1].toFloat() + DpToPx(
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
        sliderDrawable.thumb.callback = sliderDrawable
//        emojiHelper.emoji = emoji
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
