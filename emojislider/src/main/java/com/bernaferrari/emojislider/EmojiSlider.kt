package com.bernaferrari.emojislider

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import com.bernaferrari.emojislider.drawables.CircleDrawable
import com.bernaferrari.emojislider.drawables.ResultDrawable
import com.bernaferrari.emojislider.drawables.TrackDrawable
import com.cpiz.android.bubbleview.BubbleStyle
import com.cpiz.android.bubbleview.BubbleTextView
import com.facebook.rebound.*
import kotlin.math.roundToInt

class EmojiSlider @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private companion object {
        const val INITIAL_POSITION = 0.25f
        const val INITIAL_PERCENT_VALUE = 0.5f
        const val INITIAL_THUMB_SIZE_PERCENT_WHEN_PRESSED = 0.9

        const val INITIAL_AUTO_DISMISS_TIMER = 2500

        const val TENSION_SMALL = 3.0
        const val TENSION_BIG = 40.0

        const val FRICTION_SMALL = 5.0
        const val FRICTION_BIG = 7.0
    }

    private val desiredWidth: Int
    private val desiredHeight: Int

    // these will be used on onTouch
    private var mScaledTouchSlop = 0
    private var mIsDragging = false
    private val mThumbOffset: Int
    private var mTouchDownX = 0f

    /**
     * Should the slider ignore touches outside of the thumb?
     * This increases the target area, but might not be good when user is scrolling.
     */
    var registerTouchOnTrack = true

    /**
     * If false, user won't be able to move the slider.
     */
    var isUserSeekable = true

    /**
     * Useful to tell the state of the slider.
     */
    var isValueSelected = false

    /**
     * The thumb reduces its size when pressed. It is a Double number between 0.0 and 1.0.
     * Example: 0.9 means it will be 90% of original thumb's width
     */
    var thumbSizePercentWhenPressed = INITIAL_THUMB_SIZE_PERCENT_WHEN_PRESSED

    /**
     * Should the slider behave like a SeekBar or show the results like that famous social app?
     * This allows to toggle before both behaviours.
     *
     * When true, it works like a SeekBar.
     * When false, the first value chosen will be final.
     */
    var thumbAllowReselection = true

    /**
     * The average value which (unless disabled) will be displayed when a value is selected
     *
     * It is a Float, between 0.0f and 1.0f.
     */
    var averagePercentValue: Float = INITIAL_PERCENT_VALUE

    /**
     * Should the profile picture (or any image) be shown when a value is selected, like on that
     * famous app? Set it false to disable it.
     */
    var displayProfilePicture: Boolean = true

    /**
     * This controls whatever the average circle will appear when the final value
     * is selected. If this is disabled, [shouldDisplayAverage]'s value will be ignored and the
     * "Average value" tooltip will not be shown.
     */
    var shouldDisplayAverage: Boolean = true
        set(value) {
            field = value
            invalidate()
        }

    /**
     * This controls whatever the "Average value" tooltip will appear when the final value
     * is selected. If [shouldDisplayAverage] is disabled, this will also be disabled, since it
     * would make no sense otherwise.
     */
    var shouldDisplayTooltip: Boolean = true

    /**
     * Tooltip text ("Average value") is, by default, translated into 40 languages.
     * You can, however, overwrite it with your own (possibly localized) string.
     */
    var tooltipText: String = ""

    /**
     * Timer in milliseconds to hide the tooltip after it is shown.
     * Default is 2.5 seconds.
     */
    var tooltipAutoDismissTimer = INITIAL_AUTO_DISMISS_TIMER

    private var floatingEmoji = FloatingEmoji(context)

    /**
     * When user lifts the finger, if [sliderParticleSystem] is not null, the emoji can fly either
     * up or down. It is up by default, like on a famous social media app.
     */
    var floatingEmojiDirection: FloatingEmoji.Direction = FloatingEmoji.Direction.UP

    /**
     * The main characteristic from the [EmojiSlider]. There is no restriction, as long as it is
     * a text. It actually can be anything - even a text with multiple chars, the convert text
     * to drawable process works flawless.
     */
    var emoji = "😍"
        set(value) {
            field = value
            updateThumb(field)
        }

    /**
     * The foreground view which will be used to draw the [FloatingEmoji].
     *
     * Here it is possible to see how the [floatingEmoji] instance is replaced with the current one
     * from [sliderParticleSystem]'s background, if there is one. This allows all views to share
     * the same instance and the same view.
     */
    var sliderParticleSystem: View? = null
        set(value) {
            field = value

            if (value?.background !is FloatingEmoji) {
                value?.background = floatingEmoji
            } else {
                floatingEmoji = value.background as FloatingEmoji
            }
        }

    /**
     * Initial position of progress in range form `0.0` to `1.0`.
     */
    var progress: Float = INITIAL_POSITION
        set(value) {
            field = value.limitToRange()

            trackDrawable.percentProgress = field
            trackDrawable.invalidateSelf()
            invalidate()
        }

    /**
     * The track color - default is light-grey.
     */
    var colorTrack: Int
        get() = trackDrawable.trackColor.color
        set(value) {
            trackDrawable.trackColor.color = value
        }

    /**
     * The track progress color for the left side of the slider - default is purple.
     */
    var colorStart: Int
        get() = trackDrawable.colorStart
        set(value) {
            trackDrawable.colorStart = value
        }

    /**
     * The track progress color for the right side of the slider - default is red.
     */
    var colorEnd: Int
        get() = trackDrawable.colorEnd
        set(value) {
            trackDrawable.colorEnd = value
        }

    //////////////////////////////////////////
    // Drawables
    //////////////////////////////////////////

    /**
     * Drawable which will contain the emoji already converted into a drawable.
     */
    lateinit var thumbDrawable: Drawable

    /**
     * Drawable which will contain the track: both the background with help from [colorTrack]
     * and the progress by mixing together [colorStart] and [colorEnd]
     */
    val trackDrawable: TrackDrawable = TrackDrawable()

    /**
     * Drawable which displays the average's small round circle with a small ring around.
     */
    val averageDrawable: CircleDrawable = CircleDrawable(context)

    /**
     * Drawable which displays the result's big round circle with a bitmap (if any) or a big circle.
     */
    val resultDrawable: ResultDrawable = ResultDrawable(context)

    //////////////////////////////////////////
    // Public callbacks
    //////////////////////////////////////////

    /**
     * Current position tracker. Receive current position, in range from `0.0f` to `1.0f`.
     */
    var positionListener: ((Float) -> Unit)? = null

    /**
     * Called on slider touch.
     */
    var startTrackingListener: (() -> Unit)? = null

    /**
     * Called when slider is released.
     */
    var stopTrackingListener: (() -> Unit)? = null

    //////////////////////////////////////////
    // Spring Methods from Facebook's Rebound
    //////////////////////////////////////////

    private val mSpringSystem = SpringSystem.create()
    private val mSpringListener = object : SimpleSpringListener() {
        override fun onSpringUpdate(spring: Spring?) {
            invalidate()
        }
    }

    private val mThumbSpring = mSpringSystem.createSpring()
        .origamiConfig(TENSION_SMALL, FRICTION_SMALL)
        .setCurrentValue(1.0)
        .setEndValue(1.0)
        .setOvershootClampingEnabled(true)

    private val mAverageSpring: Spring = mSpringSystem.createSpring()
        .origamiConfig(TENSION_BIG, FRICTION_BIG)
        .setCurrentValue(0.0)

    //////////////////////////////////////////
    // Measure methods
    //////////////////////////////////////////

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val w = resolveSizeAndState(desiredWidth, widthMeasureSpec, 0)
        val h = resolveSizeAndState(desiredHeight, heightMeasureSpec, 0)
        setMeasuredDimension(w, h)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        this.trackDrawable.setBounds(
            left + Math.max(paddingLeft, mThumbOffset),
            h / 2 - trackDrawable.intrinsicHeight / 2,
            right - Math.max(paddingRight, mThumbOffset),
            h / 2 + trackDrawable.intrinsicHeight / 2
        )
    }

    //////////////////////////////////////////
    // Select methods
    //////////////////////////////////////////

    /**
     * Called when the final value was selected. Transitions the thumb's size to 0 while displaying
     * the [averageDrawable] and [resultDrawable].
     */
    fun valueSelectedAnimated() {
        if (thumbAllowReselection) return

        resultDrawable.endValue = 1.0
        mAverageSpring.endValue = 1.0

        if (shouldDisplayAverage && shouldDisplayTooltip) {
            showAverageTooltip()
        }

        mThumbSpring.endValue = 0.0
        isUserSeekable = false
        isValueSelected = true

        invalidate()
    }

    /**
     * Same as [valueSelectedAnimated] but transition happens immediately. Good for state restoring.
     */
    fun valueSelectedNow() {
        if (thumbAllowReselection) return

        resultDrawable.currentValue = 1.0
        mAverageSpring.currentValue = 1.0

        mThumbSpring.currentValue = 0.0
        isUserSeekable = false
        isValueSelected = true

        invalidate()
    }

    /**
     * Resets the state to as if nothing happened, with animation.
     */
    fun resetAnimated() {
        resultDrawable.endValue = 0.0
        mAverageSpring.endValue = 0.0

        mThumbSpring.endValue = 1.0
        isUserSeekable = true
        isValueSelected = false

        invalidate()
    }

    /**
     * Same as [resetAnimated] but without animation.
     */
    fun resetNow() {
        resultDrawable.currentValue = 0.0
        mAverageSpring.currentValue = 0.0

        mThumbSpring.currentValue = 1.0
        isUserSeekable = true
        isValueSelected = false

        invalidate()
    }

    /**
     * Finds out the correct position to show the tooltip and shows it.
     */
    fun showAverageTooltip() {

        val finalPosition = SpringUtil.mapValueFromRangeToRange(
            (averagePercentValue * trackDrawable.bounds.width()).toDouble(),
            0.0,
            trackDrawable.bounds.width().toDouble(),
            -(trackDrawable.bounds.width() / 2).toDouble(),
            (trackDrawable.bounds.width() / 2).toDouble()
        ).toInt()

        val rootView = View.inflate(context, R.layout.bubble, null) as BubbleTextView
        if (tooltipText.isNotBlank()) {
            rootView.text = tooltipText
        }

        val window = BernardoPopupWindow(rootView, rootView)
        window.xPadding = finalPosition
        window.yPadding = paddingTop
        window.setCancelOnTouch(true)
        window.setCancelOnTouchOutside(true)
        window.setCancelOnLater(tooltipAutoDismissTimer.toLong())
        window.showArrowTo(this, BubbleStyle.ArrowDirection.Up)
    }

    //////////////////////////////////////////
    // Lifecycle methods
    //////////////////////////////////////////

    /**
     * This can be used with lifecycles to avoid any leaks.
     */
    fun startAnimation() {
        mThumbSpring.addListener(mSpringListener)
        mAverageSpring.addListener(mSpringListener)
    }

    /**
     * Same as [startAnimation].
     */
    fun stopAnimation() {
        mThumbSpring.removeListener(mSpringListener)
        mAverageSpring.removeListener(mSpringListener)
    }

    override fun scheduleDrawable(drawable: Drawable, runnable: Runnable, j: Long) = Unit
    override fun unscheduleDrawable(drawable: Drawable, runnable: Runnable) = Unit
    override fun invalidateDrawable(drawable: Drawable) = invalidate()


    //////////////////////////////////////////
    // Initialization
    //////////////////////////////////////////


    init {

        val density = context.resources.displayMetrics.density

        desiredWidth = (56 * density * 4).toInt()
        desiredHeight =
                (density * 8 + context.resources.getDimension(R.dimen.slider_sticker_slider_handle_size)).roundToInt()
        mThumbOffset = desiredHeight / 2

        startAnimation()

        this.resultDrawable.callback = this
        this.averageDrawable.callback = this
        this.trackDrawable.callback = this

        setResultHandleSize(context.resources.getDimensionPixelSize(R.dimen.slider_sticker_slider_handle_size))
        trackDrawable.totalHeight =
                context.resources.getDimensionPixelSize(R.dimen.slider_sticker_slider_height)
        trackDrawable.setTrackHeight(context.resources.getDimension(R.dimen.slider_sticker_slider_track_height))
        trackDrawable.invalidateSelf()

        if (attrs != null) {
            val array = context.obtainStyledAttributes(attrs, R.styleable.EmojiSlider)

            try {
                progress = array.getProgress()

                colorStart = array.getProgressGradientStart()
                colorEnd = array.getProgressGradientEnd()
                colorTrack = array.getSliderTrackColor()

                colorStart = array.getProgressGradientStart()
                colorEnd = array.getProgressGradientEnd()

                registerTouchOnTrack = array.getThumbAllowScrollAnywhere()
                thumbAllowReselection = array.getAllowReselection()
                isUserSeekable = array.getIsTouchDisabled()
                averagePercentValue = array.getAverageProgress()
                shouldDisplayTooltip = array.getShouldDisplayPopup()
                shouldDisplayAverage = array.getShouldDisplayAverage()
                tooltipAutoDismissTimer = array.getTooltipTimer()
                thumbSizePercentWhenPressed = array.getThumbSizeWhenPressed()

                floatingEmojiDirection = if (array.getEmojiGravity() == 0) {
                    FloatingEmoji.Direction.UP
                } else {
                    FloatingEmoji.Direction.DOWN
                }

                array.getTooltipText()?.let {
                    tooltipText = it
                }

                emoji = array.getEmoji()

                invalidateAll()

            } finally {
                array.recycle()
            }
        } else {
            colorStart = ContextCompat.getColor(context, R.color.slider_gradient_start)
            colorEnd = ContextCompat.getColor(context, R.color.slider_gradient_end)
            colorTrack = ContextCompat.getColor(context, R.color.slider_gradient_background)
            emoji = emoji
        }

        mScaledTouchSlop = ViewConfiguration.get(context).scaledTouchSlop
    }

    /**
     * Invalidate all drawables with a hammer. There are so many things happening on screen, this solves
     * any invalidate problem brutally.
     */
    fun invalidateAll() {
        if (shouldDisplayAverage) averageDrawable.invalidateSelf()
        if (displayProfilePicture) resultDrawable.invalidateSelf()

        trackDrawable.invalidateSelf()
        thumbDrawable.invalidateSelf()
        invalidate()
    }

    /**
     * Sets the [resultDrawable]'s size
     *
     * @param size is the diameter in pixels
     */
    fun setResultHandleSize(size: Int) {
        resultDrawable.sizeHandle = size.toFloat()
        resultDrawable.imageDrawable.invalidateSelf()
        resultDrawable.circleDrawable.invalidateSelf()
    }

    //////////////////////////////////////////
    // PRIVATE GET METHODS
    //////////////////////////////////////////

    private fun TypedArray.getProgressGradientStart(): Int {
        return this.getColor(
            R.styleable.EmojiSlider_bar_progress_color_start,
            ContextCompat.getColor(context, R.color.slider_gradient_start)
        )
    }

    private fun TypedArray.getProgressGradientEnd(): Int {
        return this.getColor(
            R.styleable.EmojiSlider_bar_progress_color_end,
            ContextCompat.getColor(context, R.color.slider_gradient_end)
        )
    }

    private fun TypedArray.getSliderTrackColor(): Int {
        return this.getColor(
            R.styleable.EmojiSlider_bar_track_color,
            ContextCompat.getColor(context, R.color.slider_gradient_background)
        )
    }

    private fun TypedArray.getProgress(): Float =
        this.getFloat(R.styleable.EmojiSlider_progress, progress).limitToRange()

    private fun TypedArray.getEmoji(): String =
        this.getString(R.styleable.EmojiSlider_emoji) ?: emoji

    private fun TypedArray.getEmojiGravity(): Int =
        this.getInt(R.styleable.EmojiSlider_particle_direction, 0)

    private fun TypedArray.getThumbAllowScrollAnywhere(): Boolean =
        this.getBoolean(
            R.styleable.EmojiSlider_register_touches_outside_thumb,
            registerTouchOnTrack
        )

    private fun TypedArray.getAllowReselection(): Boolean =
        this.getBoolean(R.styleable.EmojiSlider_allow_reselection, thumbAllowReselection)

    private fun TypedArray.getAverageProgress(): Float =
        this.getFloat(R.styleable.EmojiSlider_average_progress, averagePercentValue).limitToRange()

    private fun TypedArray.getIsTouchDisabled(): Boolean =
        this.getBoolean(R.styleable.EmojiSlider_is_touch_disabled, isUserSeekable)

    private fun TypedArray.getShouldDisplayPopup(): Boolean =
        this.getBoolean(R.styleable.EmojiSlider_should_display_tooltip, shouldDisplayTooltip)

    private fun TypedArray.getShouldDisplayAverage(): Boolean =
        this.getBoolean(R.styleable.EmojiSlider_should_display_average, shouldDisplayAverage)

    private fun TypedArray.getTooltipText(): String? =
        this.getString(R.styleable.EmojiSlider_tooltip_text)

    private fun TypedArray.getTooltipTimer(): Int =
        this.getInt(R.styleable.EmojiSlider_tooltip_timer, tooltipAutoDismissTimer)

    private fun TypedArray.getThumbSizeWhenPressed(): Double =
        this.getFloat(
            R.styleable.EmojiSlider_thumb_size_percent_on_pressed,
            thumbSizePercentWhenPressed.toFloat()
        ).limitToRange().toDouble()


    //////////////////////////////////////////
    // Floating Emoji Methods
    //////////////////////////////////////////

    private fun progressChanged(progress: Float) {
        if (sliderParticleSystem == null) return

        val (paddingLeft, paddingTop) = getPaddingForFloatingEmoji()

        floatingEmoji.onProgressChanged(
            percent = progress,
            paddingLeft = paddingLeft,
            paddingTop = paddingTop
        )
    }

    private fun progressStarted() {
        if (sliderParticleSystem == null) return

        val (paddingLeft, paddingTop) = getPaddingForFloatingEmoji()

        floatingEmoji.progressStarted(
            emoji = emoji,
            direction = floatingEmojiDirection,
            paddingLeft = paddingLeft,
            paddingTop = paddingTop
        )
    }

    private fun getPaddingForFloatingEmoji(): Pair<Float, Float> {
        val sliderLocation = IntArray(2)
        getLocationOnScreen(sliderLocation)

        val particleLocation = IntArray(2)
        sliderParticleSystem!!.getLocationOnScreen(particleLocation)

        val widthPosition = progress * trackDrawable.bounds.width()

        return Pair(
            sliderLocation[0].toFloat()
                    + trackDrawable.bounds.left
                    + widthPosition
                    - particleLocation[0],
            sliderLocation[1].toFloat()
                    + trackDrawable.bounds.top
                    + dpToPx(context, 32f)
                    - particleLocation[1]
        )
    }

    //////////////////////////////////////////
    // Helper methods
    //////////////////////////////////////////

    private fun updateThumb(emoji: String) {
        thumbDrawable = textToDrawable(
            context = this.context,
            text = emoji,
            size = R.dimen.slider_sticker_slider_handle_size
        )
        thumbDrawable.callback = this
        invalidate()
    }

    /**
     * This will generate a drawable for [resultDrawable] based on a bitmap image.
     */
    fun setResultDrawable(bitmap: Bitmap) {
        resultDrawable.setDrawableFromBitmap(bitmap)
    }

    //////////////////////////////////////////
    // Extension functions
    //////////////////////////////////////////

    private fun Float.limitToRange() = Math.max(Math.min(this, 1f), 0f)

    private fun Rect.containsXY(motionEvent: MotionEvent): Boolean =
        this.contains(motionEvent.x.toInt(), motionEvent.y.toInt())

    private fun Spring.origamiConfig(tension: Double, friction: Double): Spring =
        this.setSpringConfig(SpringConfig.fromOrigamiTensionAndFriction(tension, friction))

    //////////////////////////////////////////
    // Draw methods
    //////////////////////////////////////////

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        trackDrawable.draw(canvas)
        if (shouldDisplayAverage) drawAverage(canvas)
        drawThumb(canvas)
        if (displayProfilePicture) drawProfilePicture(canvas)
    }

    private fun drawThumb(canvas: Canvas) {

        val widthPosition = progress * trackDrawable.bounds.width()
        val thumbScale = mThumbSpring.currentValue.toFloat()

        canvas.save()
        canvas.translate(trackDrawable.bounds.left.toFloat(), trackDrawable.bounds.top.toFloat())
        canvas.scale(
            thumbScale,
            thumbScale,
            widthPosition,
            (trackDrawable.bounds.bottom - trackDrawable.bounds.top) / 2f
        )

        thumbDrawable.updateDrawableBounds(widthPosition.roundToInt())
        thumbDrawable.draw(canvas)

        canvas.restore()
    }

    private fun drawProfilePicture(canvas: Canvas) {

        val widthPosition = progress * trackDrawable.bounds.width()
        val height: Float = trackDrawable.bounds.height() / 2f

        canvas.save()
        canvas.translate(trackDrawable.bounds.left.toFloat(), trackDrawable.bounds.top.toFloat())
        canvas.scale(1f, 1f, widthPosition, height)

        resultDrawable.updateDrawableBounds(widthPosition.roundToInt())
        resultDrawable.draw(canvas)

        canvas.restore()
    }

    private fun drawAverage(canvas: Canvas) {
        averageDrawable.outerColor = getCorrectColor(
            colorStart,
            colorEnd,
            averagePercentValue
        )

        // this will invalidate it in case the averageValue changes, so it updates the position
        averageDrawable.invalidateSelf()

        val scale = mAverageSpring.currentValue.toFloat()

        val widthPosition = averagePercentValue * trackDrawable.bounds.width()
        val heightPosition = (trackDrawable.bounds.height() / 2).toFloat()

        canvas.save()
        canvas.translate(trackDrawable.bounds.left.toFloat(), trackDrawable.bounds.top.toFloat())
        canvas.scale(scale, scale, widthPosition, heightPosition)

        averageDrawable.updateDrawableBounds(widthPosition.roundToInt())
        averageDrawable.draw(canvas)

        canvas.restore()
    }

    private fun Drawable.updateDrawableBounds(widthPosition: Int) {

        val customIntrinsicWidth = this.intrinsicWidth / 2
        val customIntrinsicHeight = this.intrinsicHeight / 2
        val heightPosition = trackDrawable.bounds.height() / 2

        this.setBounds(
            widthPosition - customIntrinsicWidth,
            heightPosition - customIntrinsicHeight,
            widthPosition + customIntrinsicWidth,
            heightPosition + customIntrinsicHeight
        )
    }

    //////////////////////////////////////////
    // Touch Event
    //////////////////////////////////////////

    /**
     * Handles thumbDrawable selection and movement. Notifies listener callback on certain events.
     * Inspired by AbsSeekBar.
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {

        if (!isUserSeekable || !isEnabled) {
            return false
        }

        when (event.action) {
            MotionEvent.ACTION_DOWN -> onTouchDown(event)
            MotionEvent.ACTION_MOVE -> onActionMove(event)
            MotionEvent.ACTION_UP -> {
                if (mIsDragging) performClick()
                onActionUp(event)
            }
            MotionEvent.ACTION_CANCEL -> {
                if (mIsDragging) {
                    mIsDragging = false
                    isPressed = false
                }
                invalidate() // see above explanation
            }
        }

        return true
    }

    private fun onTouchDown(event: MotionEvent) {
        if (isScrollContainer) {
            mTouchDownX = event.x
        } else {
            startDrag(event)
        }
    }

    private fun onActionMove(event: MotionEvent) {
        if (mIsDragging) {
            trackTouchEvent(event)
        } else {
            if (Math.abs(event.x - mTouchDownX) > mScaledTouchSlop) {
                startDrag(event)
            }
        }
    }

    private fun onActionUp(event: MotionEvent) {
        if (!mIsDragging && registerTouchOnTrack && trackDrawable.bounds.containsXY(event)) {
            // Touch up when we never crossed the touch slop threshold should
            // be interpreted as a tap-seek to that location.
            mIsDragging = true
            progressStarted()
            trackTouchEvent(event)
        }

        onCancelTouch()
        mIsDragging = false
        isPressed = false

        // ProgressBar doesn't know to repaint the thumb drawable
        // in its inactive state when the touch stops (because the
        // value has not apparently changed)
        invalidate()
    }

    private fun trackTouchEvent(event: MotionEvent) {
        if (mIsDragging) {
            val x = event.x.toInt() - trackDrawable.bounds.left
            progress = x / trackDrawable.bounds.width().toFloat()

            progressChanged(progress)
            positionListener?.invoke(progress)
        }
    }

    private fun onCancelTouch() {
        mThumbSpring.endValue = 1.0

        if (mIsDragging) {
            valueSelectedAnimated()
            floatingEmoji.onStopTrackingTouch()
            stopTrackingListener?.invoke()
        }
    }

    /**
     * Implemented to avoid a warning.
     */
    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    private fun startDrag(event: MotionEvent) {

        val x = event.x.toInt() - trackDrawable.bounds.left
        val y = event.y.toInt() - trackDrawable.bounds.top

        if (!thumbDrawable.bounds.contains(x, y) &&
            !(registerTouchOnTrack && trackDrawable.bounds.containsXY(event))
        ) return

        setViewPressed(true)
        progressStarted()
        mThumbSpring.endValue = thumbSizePercentWhenPressed
        startTrackingListener?.invoke()
        mIsDragging = true
        attemptClaimDrag()
    }

    /**
     * Sets the pressed state for this view.
     *
     * @see .isClickable
     * @see .setClickable
     * @param pressed Pass true to set the View's internal state to "pressed", or false to reverts
     * the View's internal state from a previously set "pressed" state.
     */
    private fun setViewPressed(pressed: Boolean) {
        dispatchSetPressed(pressed)
    }

    /**
     * Tries to claim the user's drag motion, and requests disallowing any
     * ancestors from stealing events in the drag.
     */
    private fun attemptClaimDrag() {
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(true)
        }
    }
}

