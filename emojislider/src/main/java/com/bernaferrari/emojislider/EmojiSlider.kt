package com.bernaferrari.emojislider

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
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
    }

    private val desiredWidth: Int
    private val desiredHeight: Int

    // these will be used on onTouch
    private var mScaledTouchSlop = 0
    private var mIsDragging = false
    private val mThumbOffset: Int
    private var mTouchDownX = 0f

    var isDragAnywhereEnabled = true
    var isUserSeekable = true
    var isValueSelected = false

    // this is the value the thumb will get when pressed. Example: 0.9 means it will be 90% of original size
    var thumbSizePercentWhenPressed = 0.9
    var thumbAllowReselection = true

    var averagePercentValue: Float = INITIAL_PERCENT_VALUE

    var displayProfilePicture: Boolean = true
    var shouldDisplayAverage: Boolean = true
        set(value) {
            field = value
            invalidate()
        }

    var shouldDisplayPopup: Boolean = true

    var flyingEmoji = FlyingEmoji(context)
    var flyingEmojiDirection: FlyingEmoji.Direction = FlyingEmoji.Direction.UP

    var emoji = "😍"
        set(value) {
            field = value
            updateThumb(field)
        }

    var sliderParticleSystem: View? = null
        set(value) {
            field = value

            if (value?.background !is FlyingEmoji) {
                value?.background = flyingEmoji
            } else {
                // this will work like a Singleton. If the FlyingEmoji is already present on the
                // view's background, our FlyingEmoji's instance will become that.
                flyingEmoji = value.background as FlyingEmoji
            }
        }

    /**
     * Initial position of progress in range form `0.0` to `1.0`.
     */
    var progress: Float = INITIAL_POSITION
        set(value) {
            field = value.limitToRange()

            sliderBar.percentProgress = field
            sliderBar.invalidateSelf()
            invalidate()
        }

    var colorTrack: Int
        get() = sliderBar.trackColor.color
        set(value) {
            sliderBar.trackColor.color = value
        }

    var colorStart: Int
        get() = sliderBar.colorStart
        set(value) {
            sliderBar.colorStart = value
        }

    var colorEnd: Int
        get() = sliderBar.colorEnd
        set(value) {
            sliderBar.colorEnd = value
        }

    //////////////////////////////////////////
    // Drawables
    //////////////////////////////////////////

    lateinit var thumbDrawable: Drawable
    val sliderBar: DrawableBars = DrawableBars()

    private val drawableAverageCircle: DrawableAverageCircle by lazy {
        DrawableAverageCircle(context)
    }

    private val drawableProfileImage: DrawableProfilePicture by lazy {
        DrawableProfilePicture(context)
    }

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
        .origamiConfig(3.0, 5.0)
        .setCurrentValue(1.0)
        .setEndValue(1.0)
        .setOvershootClampingEnabled(true)

    private val mAverageSpring: Spring = mSpringSystem.createSpring()
        .origamiConfig(40.0, 7.0)
        .setCurrentValue(0.0)

    //////////////////////////////////////////
    // State persistence
    //////////////////////////////////////////

    class State : BaseSavedState {
        companion object {
            @JvmField
            @Suppress("unused")
            val CREATOR = object : Parcelable.Creator<State> {
                override fun createFromParcel(parcel: Parcel): State = State(parcel)
                override fun newArray(size: Int): Array<State?> = arrayOfNulls(size)
            }
        }

        val progress: Float
        val averagePercentValue: Float
        val colorStart: Int
        val colorEnd: Int
        val colorTrack: Int
        val thumbSizePercentWhenPressed: Double
        val flyingEmojiDirection: Int

        constructor(
            superState: Parcelable,
            progress: Float,
            averagePercentValue: Float,
            colorStart: Int,
            colorEnd: Int,
            colorTrack: Int,
            pressedFinalValue: Double,
            flyingEmojiDirection: Int
        ) : super(superState) {
            this.progress = progress
            this.averagePercentValue = averagePercentValue
            this.colorStart = colorStart
            this.colorEnd = colorEnd
            this.colorTrack = colorTrack
            this.thumbSizePercentWhenPressed = pressedFinalValue
            this.flyingEmojiDirection = flyingEmojiDirection
        }

        private constructor(parcel: Parcel) : super(parcel) {
            this.progress = parcel.readFloat()
            this.averagePercentValue = parcel.readFloat()
            this.colorStart = parcel.readInt()
            this.colorEnd = parcel.readInt()
            this.colorTrack = parcel.readInt()
            this.thumbSizePercentWhenPressed = parcel.readDouble()
            this.flyingEmojiDirection = parcel.readInt()
        }

        override fun writeToParcel(parcel: Parcel, i: Int) {
            parcel.writeFloat(progress)
            parcel.writeFloat(averagePercentValue)
            parcel.writeInt(colorStart)
            parcel.writeInt(colorEnd)
            parcel.writeInt(colorTrack)
            parcel.writeDouble(thumbSizePercentWhenPressed)
            parcel.writeInt(flyingEmojiDirection)
        }

        override fun describeContents(): Int = 0
    }

//    override fun onSaveInstanceState(): Parcelable {
//        return State(
//            super.onSaveInstanceState(),
//            progress,
//            averagePercentValue,
//            colorStart,
//            colorEnd,
//            colorTrack,
//            thumbSizePercentWhenPressed,
//            flyingEmojiDirection.ordinal
//        )
//    }

    override fun onRestoreInstanceState(state: Parcelable) {
        super.onRestoreInstanceState(state)
        if (state is State) {
            progress = state.progress
            averagePercentValue = state.averagePercentValue
            colorStart = state.colorStart
            colorEnd = state.colorEnd
            colorTrack = state.colorTrack
            thumbSizePercentWhenPressed = state.thumbSizePercentWhenPressed
            flyingEmojiDirection = FlyingEmoji.Direction.values()[state.flyingEmojiDirection]
        }
    }

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

        this.sliderBar.setBounds(
            left + Math.max(paddingLeft, mThumbOffset),
            h / 2 - sliderBar.intrinsicHeight / 2,
            right - Math.max(paddingRight, mThumbOffset),
            h / 2 + sliderBar.intrinsicHeight / 2
        )
    }

    //////////////////////////////////////////
    // Select methods
    //////////////////////////////////////////

    fun valueSelectedAnimated() {
        if (thumbAllowReselection) return

        drawableProfileImage.endValue = 1.0
        mAverageSpring.endValue = 1.0

        if (shouldDisplayAverage && shouldDisplayPopup) {
            showAveragePopup()
        }

        mThumbSpring.endValue = 0.0
        isUserSeekable = false
        isValueSelected = true

        invalidate()
    }

    fun valueSelectedNow() {
        if (thumbAllowReselection) return

        drawableProfileImage.currentValue = 1.0
        mAverageSpring.currentValue = 1.0

        mThumbSpring.currentValue = 0.0
        isUserSeekable = false
        isValueSelected = true

        invalidate()
    }

    fun resetAnimated() {
        drawableProfileImage.endValue = 0.0
        mAverageSpring.endValue = 0.0

        mThumbSpring.endValue = 1.0
        isUserSeekable = true
        isValueSelected = false

        invalidate()
    }

    fun resetNow() {
        drawableProfileImage.currentValue = 0.0
        mAverageSpring.currentValue = 0.0

        mThumbSpring.currentValue = 1.0
        isUserSeekable = true
        isValueSelected = false

        invalidate()
    }

    fun showAveragePopup() {

        val finalPosition = SpringUtil.mapValueFromRangeToRange(
            (averagePercentValue * sliderBar.bounds.width()).toDouble(),
            0.0,
            sliderBar.bounds.width().toDouble(),
            -(sliderBar.bounds.width() / 2).toDouble(),
            (sliderBar.bounds.width() / 2).toDouble()
        )

        showPopupWindow(finalPosition.roundToInt(), sliderBar.bounds.top)
    }

    //////////////////////////////////////////
    // Lifecycle methods
    //////////////////////////////////////////

    fun startAnimation() {
        mThumbSpring.addListener(mSpringListener)
        mAverageSpring.addListener(mSpringListener)
    }

    fun stopAnimation() {
        mThumbSpring.removeListener(mSpringListener)
        mAverageSpring.removeListener(mSpringListener)
    }

    fun setHandleSize(size: Int) {
        drawableProfileImage.sizeHandle = size.toFloat()
        drawableProfileImage.drawableAverageHandle.radius = drawableProfileImage.sizeHandle / 2.0f
        drawableProfileImage.drawableAverageHandle.invalidateSelf()
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

        this.drawableProfileImage.callback = this
        this.drawableAverageCircle.callback = this
        this.sliderBar.callback = this

        setHandleSize(context.resources.getDimensionPixelSize(R.dimen.slider_sticker_slider_handle_size))
        sliderBar.totalHeight =
                context.resources.getDimensionPixelSize(R.dimen.slider_sticker_slider_height)
        sliderBar.setTrackHeight(context.resources.getDimension(R.dimen.slider_sticker_slider_track_height))
        sliderBar.invalidateSelf()

        if (attrs != null) {
            val array = context.obtainStyledAttributes(attrs, R.styleable.EmojiSlider)

            try {
                progress = array.getProgress().limitToRange()

                colorStart = array.getProgressGradientStart()
                colorEnd = array.getProgressGradientEnd()
                colorTrack = array.getSliderTrackColor()

                colorStart = array.getProgressGradientStart()
                colorEnd = array.getProgressGradientEnd()

                isDragAnywhereEnabled = array.getThumbAllowScrollAnywhere()
                thumbAllowReselection = array.getAllowReselection()
                isUserSeekable = array.getIsTouchDisabled()
                averagePercentValue = array.getAverageProgress().limitToRange()
                shouldDisplayPopup = array.getShouldDisplayPopup()
                shouldDisplayAverage = array.getShouldDisplayAverage()

                flyingEmojiDirection = if (array.getEmojiGravity() == 0) {
                    FlyingEmoji.Direction.UP
                } else {
                    FlyingEmoji.Direction.DOWN
                }

                this.emoji = array.getEmoji()

                invalidateAll()

            } finally {
                array.recycle()
            }
        } else {
            colorStart = ContextCompat.getColor(context, R.color.slider_gradient_start)
            colorEnd = ContextCompat.getColor(context, R.color.slider_gradient_end)
            colorTrack = ContextCompat.getColor(context, R.color.slider_gradient_background)
        }

        mScaledTouchSlop = ViewConfiguration.get(context).scaledTouchSlop
    }

    fun invalidateAll() {
        if (shouldDisplayAverage) drawableAverageCircle.invalidateSelf()
        if (displayProfilePicture) drawableProfileImage.invalidateSelf()

        sliderBar.invalidateSelf()
        thumbDrawable.invalidateSelf()
        invalidate()
    }

    //////////////////////////////////////////
    // PRIVATE GET METHODS
    //////////////////////////////////////////

    private fun TypedArray.getProgress(): Float =
        this.getFloat(R.styleable.EmojiSlider_progress, progress)

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

    private fun TypedArray.getEmoji(): String =
        this.getString(R.styleable.EmojiSlider_emoji) ?: emoji

    private fun TypedArray.getEmojiGravity(): Int =
        this.getInt(R.styleable.EmojiSlider_flying_gravity, 0)

    private fun TypedArray.getThumbAllowScrollAnywhere(): Boolean =
        this.getBoolean(R.styleable.EmojiSlider_thumb_allow_scroll_anywhere, true)

    private fun TypedArray.getAllowReselection(): Boolean =
        this.getBoolean(R.styleable.EmojiSlider_allow_reselection, true)

    private fun TypedArray.getAverageProgress(): Float =
        this.getFloat(R.styleable.EmojiSlider_average_progress, averagePercentValue)

    private fun TypedArray.getIsTouchDisabled(): Boolean =
        this.getBoolean(R.styleable.EmojiSlider_is_touch_disabled, isUserSeekable)

    private fun TypedArray.getShouldDisplayPopup(): Boolean =
        this.getBoolean(R.styleable.EmojiSlider_should_display_popup, shouldDisplayPopup)

    private fun TypedArray.getShouldDisplayAverage(): Boolean =
        this.getBoolean(R.styleable.EmojiSlider_should_display_average, shouldDisplayAverage)


    //////////////////////////////////////////
    // Flying Emoji Methods
    //////////////////////////////////////////

    private fun progressChanged(progress: Float) {
        if (sliderParticleSystem == null) return

        val (paddingLeft, paddingTop) = getPaddingForFlyingEmoji()

        flyingEmoji.onProgressChanged(
            percent = progress,
            paddingLeft = paddingLeft,
            paddingTop = paddingTop
        )
    }

    private fun progressStarted() {
        if (sliderParticleSystem == null) return

        val (paddingLeft, paddingTop) = getPaddingForFlyingEmoji()

        flyingEmoji.progressStarted(
            emoji = emoji,
            direction = flyingEmojiDirection,
            paddingLeft = paddingLeft,
            paddingTop = paddingTop
        )
    }

    private fun getPaddingForFlyingEmoji(): Pair<Float, Float> {
        val sliderLocation = IntArray(2)
        getLocationOnScreen(sliderLocation)

        val particleLocation = IntArray(2)
        sliderParticleSystem!!.getLocationOnScreen(particleLocation)

        val widthPosition = progress * sliderBar.bounds.width()

        return Pair(
            sliderLocation[0].toFloat() + sliderBar.bounds.left + widthPosition - particleLocation[0],
            sliderLocation[1].toFloat() + sliderBar.bounds.top + DpToPx(
                context,
                32f
            ) - particleLocation[1]
        )
    }

    //////////////////////////////////////////
    // Update method
    //////////////////////////////////////////

    private fun updateThumb(emoji: String) {
        thumbDrawable = textToDrawable(
            context = this.context,
            text = emoji,
            size = R.dimen.slider_sticker_slider_handle_size
        )
        thumbDrawable.callback = this
    }

    //////////////////////////////////////////
    // Extension functions
    //////////////////////////////////////////

    private fun Float.limitToRange() = Math.max(Math.min(this, 1f), 0f)

    private fun Rect.containsXY(motionEvent: MotionEvent): Boolean =
        this.contains(motionEvent.x.toInt(), motionEvent.y.toInt())

    private fun Spring.origamiConfig(tension: Double, friction: Double): Spring =
        this.setSpringConfig(SpringConfig.fromOrigamiTensionAndFriction(tension, friction))

    private fun showPopupWindow(position: Int, paddingTop: Int) {
        val rootView = View.inflate(context, R.layout.bubble, null) as BubbleTextView
        val window = BernardoPopupWindow(rootView, rootView)
        window.xPadding = position
        window.yPadding = paddingTop
        window.setCancelOnTouch(true)
        window.setCancelOnTouchOutside(true)
        window.setCancelOnLater(2500)
        window.showArrowTo(this, BubbleStyle.ArrowDirection.Up)
    }

    //////////////////////////////////////////
    // Draw
    //////////////////////////////////////////

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        sliderBar.draw(canvas)
        if (shouldDisplayAverage) drawAverage(canvas)
        drawThumb(canvas)
        if (displayProfilePicture) drawProfilePicture(canvas)
    }

    // used for debugging
    private fun drawRedPaint(canvas: Canvas) {
        val x = width
        val y = height
        val radius = 100
        val paint = Paint()
        paint.style = Paint.Style.FILL
        paint.color = Color.WHITE
        canvas.drawPaint(paint)
        // Use Color.parseColor to define HTML colors
        paint.color = Color.parseColor("#CD5C5C")
        canvas.drawCircle(x / 2f, y / 2f, radius.toFloat(), paint)
    }

    private fun drawThumb(canvas: Canvas) {

        val widthPosition = progress * sliderBar.bounds.width()
        val thumbScale = mThumbSpring.currentValue.toFloat()

        canvas.save()
        canvas.translate(sliderBar.bounds.left.toFloat(), sliderBar.bounds.top.toFloat())
        canvas.scale(
            thumbScale,
            thumbScale,
            widthPosition,
            (sliderBar.bounds.bottom - sliderBar.bounds.top) / 2f
        )

        thumbDrawable.updateDrawableBounds(widthPosition.roundToInt())
        thumbDrawable.draw(canvas)

        canvas.restore()
    }

    private fun drawProfilePicture(canvas: Canvas) {

        val widthPosition = progress * sliderBar.bounds.width()
        val height: Float = sliderBar.bounds.height() / 2f

        canvas.save()
        canvas.translate(sliderBar.bounds.left.toFloat(), sliderBar.bounds.top.toFloat())
        canvas.scale(1f, 1f, widthPosition, height)

        drawableProfileImage.updateDrawableBounds(widthPosition.roundToInt())
        drawableProfileImage.draw(canvas)

        canvas.restore()
    }

    private fun drawAverage(canvas: Canvas) {
        drawableAverageCircle.outerColor = getCorrectColor(
            colorStart,
            colorEnd,
            averagePercentValue
        )

        drawableAverageCircle.invalidateSelf()

        val scale = mAverageSpring.currentValue.toFloat()

        val widthPosition = averagePercentValue * sliderBar.bounds.width()
        val heightPosition = (sliderBar.bounds.height() / 2).toFloat()

        canvas.save()
        canvas.translate(sliderBar.bounds.left.toFloat(), sliderBar.bounds.top.toFloat())
        canvas.scale(scale, scale, widthPosition, heightPosition)

        drawableAverageCircle.updateDrawableBounds(widthPosition.roundToInt())
        drawableAverageCircle.draw(canvas)

        canvas.restore()
    }

    private fun Drawable.updateDrawableBounds(widthPosition: Int) {

        val customIntrinsicWidth = this.intrinsicWidth / 2
        val customIntrinsicHeight = this.intrinsicHeight / 2
        val heightPosition = sliderBar.bounds.height() / 2

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
            MotionEvent.ACTION_DOWN -> {

                if (isScrollContainer) {
                    mTouchDownX = event.x
                } else {
                    startDrag(event)
                }
            }

            MotionEvent.ACTION_MOVE -> {
                if (mIsDragging) {
                    trackTouchEvent(event)
                } else {
                    if (Math.abs(event.x - mTouchDownX) > mScaledTouchSlop) {
                        startDrag(event)
                    }
                }
            }

            MotionEvent.ACTION_UP -> {
                if (mIsDragging) {
                    onCancelTouch()
                    performClick()
                    invalidate()
                    mIsDragging = false
                    isPressed = false
                } else {

                    if (isDragAnywhereEnabled && sliderBar.bounds.containsXY(event)) {
                        // Touch up when we never crossed the touch slop threshold should
                        // be interpreted as a tap-seek to that location.
                        mIsDragging = true
                        progressStarted()
                        trackTouchEvent(event)
                    }
                    onCancelTouch()
                    mIsDragging = false
                }
                // ProgressBar doesn't know to repaint the thumb drawable
                // in its inactive state when the touch stops (because the
                // value has not apparently changed)
                invalidate()
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

    private fun trackTouchEvent(event: MotionEvent) {
        if (mIsDragging) {
            val x = event.x.toInt() - sliderBar.bounds.left
            progress = x / sliderBar.bounds.width().toFloat()

            progressChanged(progress)
            positionListener?.invoke(progress)
        }
    }

    private fun onCancelTouch() {
        mThumbSpring.endValue = 1.0

        if (mIsDragging) {
            valueSelectedAnimated()
            flyingEmoji.onStopTrackingTouch()
            stopTrackingListener?.invoke()
        }
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    private fun startDrag(event: MotionEvent) {

        val x = event.x.toInt() - sliderBar.bounds.left
        val y = event.y.toInt() - sliderBar.bounds.top

        if (!thumbDrawable.bounds.contains(x, y) &&
            !(isDragAnywhereEnabled && sliderBar.bounds.containsXY(event))
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
