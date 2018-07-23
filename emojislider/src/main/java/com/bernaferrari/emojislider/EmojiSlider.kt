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
import android.view.LayoutInflater
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
        const val BAR_CORNER_RADIUS = 2
        const val BAR_INNER_HORIZONTAL_OFFSET = 0

        const val SLIDER_WIDTH = 4

        const val TOP_SPREAD_FACTOR = 0.4f
        const val BOTTOM_START_SPREAD_FACTOR = 0.25f
        const val BOTTOM_END_SPREAD_FACTOR = 0.1f
        const val METABALL_HANDLER_FACTOR = 2.4f
        const val METABALL_MAX_DISTANCE = 15.0f
        const val METABALL_RISE_DISTANCE = 1.1f

        const val COLOR_BAR = 0xff6168e7.toInt()
        const val COLOR_LABEL = Color.WHITE
        const val COLOR_LABEL_TEXT = Color.BLACK
        const val COLOR_BAR_TEXT = Color.WHITE

        const val INITIAL_POSITION = 0.25f
        const val INITIAL_PERCENT_VALUE = 0.5f
    }

    private val barHeight: Float

    private val desiredWidth: Int
    private val desiredHeight: Int

    // this is the value the thumb will get when pressed. Example: 0.9 means it will be 90% of original size
    var thumbSizePercentWhenPressed = 0.9

    private val topCircleDiameter: Float = 0f
    private val bottomCircleDiameter: Float = 0f
    private val touchRectDiameter: Float = 0f
    private val labelRectDiameter: Float = 0f

    private val metaballMaxDistance: Float = 0f
    private val metaballRiseDistance: Float = 0f

    private val barVerticalOffset: Float = 0f
    private val barCornerRadius: Float
    private val barInnerOffset: Float

    var thumbAllowReselection: Boolean = true

    var emoji = "😍"
        set(value) {
            field = value
            updateThumb(field)
        }

    var flyingEmoji = FlyingEmoji(context)
    var flyingEmojiDirection: FlyingEmoji.Direction = FlyingEmoji.Direction.UP
    private var mScaledTouchSlop = 0

    var sliderParticleSystem: View? = null
        set(value) {
            field = value

            if (value?.background !is FlyingEmoji) {
                value?.background = flyingEmoji
            } else {
                flyingEmoji = value.background as FlyingEmoji
            }

            println("RAWR1: $value")
        }

    /**
     * Duration of "bubble" rise in milliseconds.
     */
    var pressedFinalValue = 0.0

    /**
     * Initial position of "bubble" in range form `0.0` to `1.0`.
     */
    var progress = INITIAL_POSITION
        set(value) {
            field = value.limitToRange()

            sliderBar.percentProgress = field
            sliderBar.invalidateSelf()
            invalidate()
        }

    //////////////////////////////////////////
    // Variables
    //////////////////////////////////////////

    lateinit var thumbDrawable: Drawable
    private val mThumbOffset: Int
    private var mTouchDownX = 0f

    val sliderBar: DrawableBars = DrawableBars()

    var isDragAnywhereEnabled = true
    var sliderHorizontalPadding: Int = 0

    var mIsUserSeekable = true
    var colorStart: Int = 0
    var colorEnd: Int = 0

    var averagePercentValue = INITIAL_PERCENT_VALUE
    var averageShouldShow: Boolean = true
    var mIsDragging = false

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
    var beginTrackingListener: (() -> Unit)? = null

    /**
     * Called when slider is released.
     */
    var endTrackingListener: (() -> Unit)? = null

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

        val position: Float
        val averagePercentValue: Float
        val colorLabel: Int
        val colorBar: Int
        val pressedFinalValue: Double

        constructor(
            superState: Parcelable,
            position: Float,
            averagePercentValue: Float,
            colorLabel: Int,
            colorBar: Int,
            pressedFinalValue: Double
        ) : super(superState) {
            this.position = position
            this.averagePercentValue = averagePercentValue
            this.colorLabel = colorLabel
            this.colorBar = colorBar
            this.pressedFinalValue = pressedFinalValue
        }

        private constructor(parcel: Parcel) : super(parcel) {
            this.position = parcel.readFloat()
            this.averagePercentValue = parcel.readFloat()
            this.colorLabel = parcel.readInt()
            this.colorBar = parcel.readInt()
            this.pressedFinalValue = parcel.readDouble()
        }

        override fun writeToParcel(parcel: Parcel, i: Int) {
            parcel.writeFloat(position)
            parcel.writeFloat(averagePercentValue)
            parcel.writeInt(colorLabel)
            parcel.writeInt(colorBar)
            parcel.writeDouble(pressedFinalValue)
        }

        override fun describeContents(): Int = 0
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        super.onRestoreInstanceState(state)
        if (state is State) {
            progress = state.position
            averagePercentValue = state.averagePercentValue
            pressedFinalValue = state.pressedFinalValue
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

        println("sliderBoundsContains3 paddinLeft: $paddingLeft")

        this.sliderBar.setBounds(
            left + Math.max(paddingLeft, mThumbOffset),
            h / 2 - sliderBar.intrinsicHeight / 2,
            right - Math.max(paddingRight, mThumbOffset),
            h / 2 + sliderBar.intrinsicHeight / 2
        )

        println("sliderBoundsContains2: " + sliderBar.bounds.toShortString() + " iheight: " + sliderBar.intrinsicHeight + " h: " + h / 2)
    }

    //////////////////////////////////////////
    // Select methods
    //////////////////////////////////////////

    fun valueWasSelected() {
        if (thumbAllowReselection) return

        mAverageSpring.endValue = 1.0
        mThumbSpring.endValue = 0.0
        mIsUserSeekable = false
        drawableProfileImage.show()

        showAveragePopup()
        invalidate()
    }


    fun reset() {
        mAverageSpring.endValue = 0.0
        mThumbSpring.endValue = 1.0
        mIsUserSeekable = true
        drawableProfileImage.hide()

        showAveragePopup()
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

        finalPosition.roundToInt().showPopupWindow()
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

        barHeight = 56 * density
        desiredWidth = (barHeight * SLIDER_WIDTH).toInt()
        desiredHeight =
                (density * 8 + context.resources.getDimension(R.dimen.slider_sticker_slider_handle_size)).roundToInt()
        mThumbOffset = desiredHeight / 2

        barCornerRadius = BAR_CORNER_RADIUS * density
        barInnerOffset = BAR_INNER_HORIZONTAL_OFFSET * density

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
                array.getProgress().let {
                    progress = if (it in 0f..1f) {
                        it
                    } else if (it > 1 && it < 100) {
                        it / 100f
                    } else if (it < 0) {
                        0f
                    } else {
                        INITIAL_POSITION
                    }
                }

                sliderBar.trackColor.color = array.getSliderTrackColor()
                sliderBar.colorStart = array.getProgressGradientStart()
                sliderBar.colorEnd = array.getProgressGradientEnd()

                colorStart = array.getProgressGradientStart()
                colorEnd = array.getProgressGradientEnd()

                sliderHorizontalPadding = array.getHorizontalPadding()
                isDragAnywhereEnabled = array.getThumbAllowScrollAnywhere()
                thumbAllowReselection = array.getAllowReselection()
                mIsUserSeekable = array.getIsTouchDisabled()
                averagePercentValue = array.getAverageProgress()

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

        }

        mScaledTouchSlop = ViewConfiguration.get(context).scaledTouchSlop
    }

    fun invalidateAll() {
        if (averageShouldShow) {
            drawableProfileImage.invalidateSelf()
            drawableAverageCircle.invalidateSelf()
        }
        sliderBar.invalidateSelf()
        thumbDrawable.invalidateSelf()
        invalidate()
    }

    //////////////////////////////////////////
    // PRIVATE GET METHODS
    //////////////////////////////////////////

    private fun TypedArray.getProgress(): Float =
        this.getFloat(R.styleable.EmojiSlider_progress, INITIAL_POSITION)

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

    private fun TypedArray.getHorizontalPadding(): Int {
        return this.getInt(
            R.styleable.EmojiSlider_horizontal_padding,
            context.resources.getDimensionPixelSize(R.dimen.slider_sticker_padding_horizontal)
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
        this.getFloat(R.styleable.EmojiSlider_average_progress, INITIAL_PERCENT_VALUE)

    private fun TypedArray.getIsTouchDisabled(): Boolean =
        this.getBoolean(R.styleable.EmojiSlider_is_touch_disabled, true)


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
            sliderLocation[1].toFloat() + DpToPx(context, 32f) - particleLocation[1]
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

    private fun Int.showPopupWindow() {
        val rootView =
            LayoutInflater.from(context).inflate(R.layout.bubble, null) as BubbleTextView
        val window = BernardoPopupWindow(rootView, rootView)
        window.xPadding = this
        window.setCancelOnTouch(true)
        window.setCancelOnTouchOutside(true)
        window.setCancelOnLater(2500)
        window.showArrowTo(this@EmojiSlider, BubbleStyle.ArrowDirection.Up)
    }

    //////////////////////////////////////////
    // Draw
    //////////////////////////////////////////

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

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

        sliderBar.draw(canvas)
        if (averageShouldShow) drawAverage(canvas)
        drawThumb(canvas)
        if (averageShouldShow) drawProfilePicture(canvas)
    }

    private fun drawThumb(canvas: Canvas) {

        val widthPosition = progress * sliderBar.bounds.width()
        val thumbScale = mThumbSpring.currentValue.toFloat()

        canvas.save()
        canvas.translate(sliderBar.bounds.left.toFloat(), sliderBar.bounds.top.toFloat())
        canvas.scale(thumbScale, thumbScale, widthPosition, sliderBar.bounds.exactCenterY())

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

        this.drawableProfileImage.updateDrawableBounds(widthPosition.roundToInt())
        this.drawableProfileImage.draw(canvas)

        canvas.restore()
    }

    private fun drawAverage(canvas: Canvas) {
        this.drawableAverageCircle.outerColor = getCorrectColor(
            this.colorStart,
            this.colorEnd,
            this.averagePercentValue
        )

        this.drawableAverageCircle.invalidateSelf()

        val scale = this.mAverageSpring.currentValue.toFloat()

        val widthPosition = this.averagePercentValue * sliderBar.bounds.width()
        val heightPosition = (sliderBar.bounds.height() / 2).toFloat()

        canvas.save()
        canvas.translate(sliderBar.bounds.left.toFloat(), sliderBar.bounds.top.toFloat())
        canvas.scale(scale, scale, widthPosition, heightPosition)

        this.drawableAverageCircle.updateDrawableBounds(widthPosition.roundToInt())
        this.drawableAverageCircle.draw(canvas)

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

        if (!mIsUserSeekable || !isEnabled) {
            return false
        }

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                println("ber -> actionDown")
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
                    // Touch up when we never crossed the touch slop threshold should
                    // be interpreted as a tap-seek to that location.
                    mIsDragging = true
                    progressStarted()
                    trackTouchEvent(event)
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
            println("moving.. " + "x: " + x + " width: " + sliderBar.bounds.width() + " equals: " + progress)
        }
    }

    private fun onCancelTouch() {
        mThumbSpring.endValue = 1.0

        if (mIsDragging) {
            valueWasSelected()
            flyingEmoji.onStopTrackingTouch()
            endTrackingListener?.invoke()
        }
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    private fun startDrag(event: MotionEvent) {

        val x = event.x.toInt() - sliderBar.bounds.left
        val y = event.y.toInt() - sliderBar.bounds.top

        println(
            "sliderBoundsContains: " + sliderBar.bounds.toShortString() + " --- x: " + x + " y: " + y + " mX: " + event.x + " mY: " + event.y + " contains1: " + sliderBar.bounds.containsXY(
                event
            ) + " contains2: " + sliderBar.bounds.contains(x, y)
        )
        if (!thumbDrawable.bounds.contains(x, y) &&
            !(isDragAnywhereEnabled && sliderBar.bounds.containsXY(event))
        ) return

        setViewPressed(true)
        progressStarted()
        mThumbSpring.endValue = thumbSizePercentWhenPressed
        beginTrackingListener?.invoke()
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
