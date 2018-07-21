package com.bernaferrari.emojislider2

import android.content.Context
import android.content.res.TypedArray
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import com.cpiz.android.bubbleview.BubbleStyle
import com.cpiz.android.bubbleview.BubbleTextView

open class EmojiSlider @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    val trackingTouch: TrackingTouch = object : TrackingTouch {

        override fun onStartTrackingTouch() = flyingEmoji.progressStarted(emoji)

        override fun onStopTrackingTouch() = flyingEmoji.onStopTrackingTouch()

        override fun onProgressChanged(progress: Int) = progressChanged(progress)

        override fun showPopupWindow(finalPosition: Int) {
            val rootView =
                LayoutInflater.from(context).inflate(R.layout.bubble, null) as BubbleTextView
            val window = BernardoPopupWindow(rootView, rootView)
            window.xPadding = finalPosition
            window.setCancelOnTouch(true)
            window.setCancelOnTouchOutside(true)
            window.setCancelOnLater(2500)
            window.showArrowTo(this@EmojiSlider, BubbleStyle.ArrowDirection.Up)
        }
    }

    val sliderDrawable = DrawableSlider(context, trackingTouch)
    var flyingEmoji = FlyingEmoji(context)
    var emoji = "😍"
    var sliderParticleSystem: View? = null
        set(value) {
            field = value

            if (value?.background !is FlyingEmoji) {
                value?.background = flyingEmoji
            } else {
                flyingEmoji = value.background as FlyingEmoji
            }
        }

    private fun Int.limitToRange() = Math.max(Math.min(this, 100), 0)

    //////////////////////////////////////////
    // Initialization
    //////////////////////////////////////////

    init {
        this.background = sliderDrawable

        val array = context.obtainStyledAttributes(attrs, R.styleable.EmojiSlider)

        try {

            sliderDrawable.updatePercentage(getProgress(array).limitToRange())

            sliderDrawable.sliderBar.setGradientBackground(getProgressGradientBackground(array))
            sliderDrawable.sliderBar.colorStart = getProgressGradientStart(array)
            sliderDrawable.sliderBar.colorEnd = getProgressGradientEnd(array)

            sliderDrawable.colorStart = getProgressGradientStart(array)
            sliderDrawable.colorEnd = getProgressGradientEnd(array)

            sliderDrawable.sliderHorizontalPadding = getHorizontalPadding(array)
            sliderDrawable.isThumbAllowedToScrollEverywhere = getThumbAllowScrollAnywhere(array)
            sliderDrawable.thumbAllowReselection = getAllowReselection(array)
            sliderDrawable.sliderBar.invalidateSelf()
            sliderDrawable.isTouchDisabled = getIsTouchDisabled(array)
            sliderDrawable.averagePercentValue = getAverageProgress(array) / 100f

            if (getEmojiGravity(array) == 0) {
                flyingEmoji.direction = FlyingEmoji.Direction.UP
            } else {
                flyingEmoji.direction = FlyingEmoji.Direction.DOWN
            }

            updateThumb(getEmoji(array))

        } finally {
            array.recycle()
        }
    }

    //////////////////////////////////////////
    // PUBLIC METHODS
    //////////////////////////////////////////

    fun setGradientBackground(color: Int): EmojiSlider {
        sliderDrawable.sliderBar.progressBackgroundPaint.color = color
        return this
    }

    fun setGradientColorStart(color: Int): EmojiSlider {
        sliderDrawable.colorStart = color
        return this
    }

    fun setGradientColorEnd(color: Int): EmojiSlider {
        sliderDrawable.colorEnd = color
        return this
    }

    fun setHorizontalPadding(padding: Int): EmojiSlider {
        sliderDrawable.sliderHorizontalPadding = padding
        return this
    }

    fun setAllowScrollEverywhere(isAllowed: Boolean): EmojiSlider {
        sliderDrawable.isThumbAllowedToScrollEverywhere = isAllowed
        return this
    }

    fun setThumbAllowReselection(isAllowed: Boolean): EmojiSlider {
        sliderDrawable.thumbAllowReselection = isAllowed
        return this
    }

    fun setAverageProgress(progress: Int): EmojiSlider {
        sliderDrawable.averagePercentValue = progress / 100f
        return this
    }

    fun setFlyingEmojiDirection(direction: FlyingEmoji.Direction): EmojiSlider {
        flyingEmoji.direction = direction
        return this
    }

    fun setEmoji(emoji: String): EmojiSlider {
        updateThumb(emoji)
        return this
    }

    fun apply() {
        sliderDrawable.drawableProfileImage.invalidateSelf()
        sliderDrawable.drawableAverageCircle.invalidateSelf()
        sliderDrawable.sliderBar.invalidateSelf()
        sliderDrawable.thumbDrawable.invalidateSelf()
        sliderDrawable.invalidateSelf()
        invalidate()
    }

    //////////////////////////////////////////
    // PRIVATE GET METHODS
    //////////////////////////////////////////

    private fun getProgress(typedArray: TypedArray): Int {
        return typedArray.getInt(R.styleable.EmojiSlider_progress, 10)
    }

    private fun getProgressGradientStart(typedArray: TypedArray): Int {
        return typedArray.getInt(
            R.styleable.EmojiSlider_bar_gradient_start,
            ContextCompat.getColor(context, R.color.slider_gradient_start)
        )
    }

    private fun getProgressGradientEnd(typedArray: TypedArray): Int {
        return typedArray.getInt(
            R.styleable.EmojiSlider_bar_gradient_start,
            ContextCompat.getColor(context, R.color.slider_gradient_end)
        )
    }

    private fun getProgressGradientBackground(typedArray: TypedArray): Int {
        return typedArray.getInt(
            R.styleable.EmojiSlider_bar_background_color,
            ContextCompat.getColor(context, R.color.slider_gradient_background)
        )
    }

    private fun getHorizontalPadding(typedArray: TypedArray): Int {
        return typedArray.getInt(
            R.styleable.EmojiSlider_horizontal_padding,
            context.resources.getDimensionPixelSize(R.dimen.slider_sticker_padding_horizontal) * 2
        )
    }

    private fun getEmoji(typedArray: TypedArray): String {
        return typedArray.getString(R.styleable.EmojiSlider_emoji) ?: emoji
    }

    private fun getEmojiGravity(typedArray: TypedArray): Int {
        return typedArray.getInt(R.styleable.EmojiSlider_gravity, 0)
    }

    private fun getThumbAllowScrollAnywhere(typedArray: TypedArray): Boolean {
        return typedArray.getBoolean(R.styleable.EmojiSlider_thumb_allow_scroll_anywhere, true)
    }

    private fun getAllowReselection(typedArray: TypedArray): Boolean {
        return typedArray.getBoolean(R.styleable.EmojiSlider_allow_reselection, true)
    }

    private fun getAverageProgress(typedArray: TypedArray): Int {
        return typedArray.getInt(R.styleable.EmojiSlider_average_progress, 100)
    }

    private fun getIsTouchDisabled(typedArray: TypedArray): Boolean {
        return typedArray.getBoolean(R.styleable.EmojiSlider_is_touch_disabled, false)
    }


    //////////////////////////////////////////
    // OTHER METHODS
    //////////////////////////////////////////

    fun progressChanged(progress: Int) {
        if (sliderParticleSystem == null) return

        val sliderLocation = IntArray(2)
        this.getLocationOnScreen(sliderLocation)

        val particleLocation = IntArray(2)
        sliderParticleSystem!!.getLocationOnScreen(particleLocation)

        this.flyingEmoji.onProgressChanged(
            paddingLeft = sliderLocation[0].toFloat() + sliderDrawable.sliderBar.bounds.left + sliderDrawable.thumbDrawable.bounds.centerX() - particleLocation[0],
            paddingTop = sliderLocation[1].toFloat() + DpToPx(
                context,
                32f
            ) - particleLocation[1]
        )

        this.flyingEmoji.updateProgress(progress / 100f)
    }

    private fun updateThumb(emoji: String) {
        this.emoji = emoji
        sliderDrawable.thumbDrawable = generateThumb(
            context = this.context,
            text = emoji,
            size = R.dimen.slider_sticker_slider_handle_size
        )
        sliderDrawable.thumbDrawable.callback = sliderDrawable
    }

    /**
     * Handles thumbDrawable selection and movement. Notifies listener callback on certain events.
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
