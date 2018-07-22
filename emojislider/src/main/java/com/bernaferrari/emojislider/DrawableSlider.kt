package com.bernaferrari.emojislider

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.graphics.drawable.Drawable.Callback
import android.view.MotionEvent
import android.view.View
import com.facebook.rebound.*
import kotlin.math.roundToInt

class DrawableSlider(
    val context: Context,
    private val tracking: TrackingTouch
) : Drawable(), Callback, Lifecycles, View.OnTouchListener {

    //////////////////////////////////////////
    // Spring Methods from Facebook's Rebound
    //////////////////////////////////////////

    private val mSpringSystem = SpringSystem.create()
    private val mSpringListener = object : SimpleSpringListener() {
        override fun onSpringUpdate(spring: Spring?) {
            invalidateSelf()
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
    // Variables
    //////////////////////////////////////////

    lateinit var thumbDrawable: Drawable
    val sliderBar: DrawableBars = DrawableBars()
    val drawableProfileImage: DrawableProfilePicture = DrawableProfilePicture(context)

    var isThumbAllowedToScrollEverywhere = true
    var sliderHorizontalPadding: Int = 0

    var isTouchDisabled = false
    internal var colorStart: Int = 0
    internal var colorEnd: Int = 0

    var averagePercentValue = 0f
    var averageShouldShow: Boolean = true
    var isThumbSelected = false

    val drawableAverageCircle: DrawableAverageCircle by lazy {
        DrawableAverageCircle(context).apply {
            val voteAverageHandleSize =
                context.resources.getDimensionPixelSize(R.dimen.slider_sticker_slider_vote_average_handle_size)
            this.radius = voteAverageHandleSize.toFloat() / 2.0f
            this.invalidateSelf()
        }
    }

    var thumbAllowReselection: Boolean = true
    var progressValue = 0

    private val barPaddingTop: Int =
        context.resources.getDimensionPixelSize(R.dimen.slider_sticker_padding_top_without_question)
    private val barPaddingBottom: Int =
        context.resources.getDimensionPixelSize(R.dimen.slider_sticker_padding_bottom_without_question)

    fun valueWasSelected() {
        if (thumbAllowReselection) return

        mAverageSpring.endValue = 1.0
        mThumbSpring.endValue = 0.0
        isTouchDisabled = true
        drawableProfileImage.updateThis()

        showAveragePopup()
        invalidateSelf()
    }

    fun showAveragePopup() {
        val finalPosition = SpringUtil.mapValueFromRangeToRange(
            sliderBar.bounds.left.toFloat() + (this.averagePercentValue * sliderBar.bounds.width()).toDouble(),
            0.0,
            bounds.width().toDouble(),
            (-bounds.width() / 2).toDouble(),
            (bounds.width() / 2).toDouble()
        )

        tracking.showPopupWindow(finalPosition.roundToInt())
    }

    private fun Double.limitToRange() = Math.max(Math.min(this, 1.0), 0.0)

    private fun Rect.containsXY(motionEvent: MotionEvent): Boolean =
        this.contains(motionEvent.x.toInt(), motionEvent.y.toInt())

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {

        if (isTouchDisabled) return false

        val x = motionEvent.x.toInt() - sliderBar.bounds.left
        val y = motionEvent.y.toInt() - sliderBar.bounds.top

        when (motionEvent.actionMasked) {
            MotionEvent.ACTION_DOWN -> {

                println("x: " + x + " y: " + y + " ThumbBounds: " + thumbDrawable.bounds.toShortString() + "drawSeekBounds: " + sliderBar.bounds.toShortString() + " left: " + sliderBar.bounds.left + " sliderHorizontalPadding: " + sliderHorizontalPadding)

                if (this.thumbDrawable.bounds.contains(x, y) ||
                    (isThumbAllowedToScrollEverywhere
                            && this.sliderBar.bounds.containsXY(motionEvent))
                ) {
                    isThumbSelected = true
                    tracking.onStartTrackingTouch()
                    mThumbSpring.endValue = 0.9
                }
            }

            MotionEvent.ACTION_UP -> {
                cancelMethod()
                view.performClick()
                invalidateSelf()
            }

            MotionEvent.ACTION_CANCEL -> cancelMethod()

            MotionEvent.ACTION_MOVE -> {
                if (isThumbSelected) {
                    updatePercentage(((x / sliderBar.bounds.width().toDouble()).limitToRange() * 100).roundToInt())
                    println("moving.. " + "x: " + x + " width: " + sliderBar.bounds.width() + " equals: " + (x.toFloat() / sliderBar.bounds.width().toFloat()).toDouble().limitToRange())
                }
            }
        }

        return true
    }

    //////////////////////////////////////////
    // Update methods
    //////////////////////////////////////////

    fun updatePercentage(progress: Int) {
        println("updatePercent: $progress")
        progressValue = progress

        sliderBar.percentProgress = progress / 100f
        sliderBar.invalidateSelf()
        tracking.onProgressChanged(progress)

        invalidateSelf()
    }

    private fun updateThumbBounds(widthPosition: Int) {

        val customIntrinsicWidth = thumbDrawable.intrinsicWidth / 2
        val customIntrinsicHeight = thumbDrawable.intrinsicHeight / 2
        val heightPosition = bounds.height() / 2

        this.thumbDrawable.setBounds(
            widthPosition - customIntrinsicWidth,
            heightPosition - customIntrinsicHeight,
            widthPosition + customIntrinsicWidth,
            heightPosition + customIntrinsicHeight
        )
    }

    fun cancelMethod() {
        mThumbSpring.endValue = 1.0

        if (isThumbSelected) {
            valueWasSelected()
            tracking.onStopTrackingTouch()
        }
        isThumbSelected = false
    }

    //////////////////////////////////////////
    // Lifecycle methods
    //////////////////////////////////////////

    override fun startAnimation() {
        mThumbSpring.addListener(mSpringListener)
        mAverageSpring.addListener(mSpringListener)
    }

    override fun stopAnimation() {
        mThumbSpring.removeListener(mSpringListener)
        mAverageSpring.removeListener(mSpringListener)
    }

    //////////////////////////////////////////
    // Initialization
    //////////////////////////////////////////


    init {
        startAnimation()

        this.drawableProfileImage.callback = this
        this.drawableAverageCircle.callback = this
        this.sliderBar.callback = this

        this.m18483a(context.resources.getDimensionPixelSize(R.dimen.slider_sticker_slider_handle_size))
        sliderBar.sliderHeight =
                context.resources.getDimensionPixelSize(R.dimen.slider_sticker_slider_height)
        sliderBar.configureHeight(context.resources.getDimensionPixelSize(R.dimen.slider_sticker_slider_track_height))
        sliderBar.invalidateSelf()
    }


    fun m18483a(size: Int) {
        drawableProfileImage.sizeHandle = size.toFloat()
        //        c7849d.f32860a.m10639a(c7849d.sizeHandle);
        val circleHandleC5190I = drawableProfileImage.drawableAverageHandle
        circleHandleC5190I.radius = drawableProfileImage.sizeHandle / 2.0f
        circleHandleC5190I.invalidateSelf()
//        val c7852l = drawableProfileImage
//        c7852l.f32890a = drawableProfileImage.sizeHandle
//        c7852l.invalidateSelf()
//        drawableProfileImage.invalidateSelf()
    }

    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT
    override fun scheduleDrawable(drawable: Drawable, runnable: Runnable, j: Long) = Unit
    override fun unscheduleDrawable(drawable: Drawable, runnable: Runnable) = Unit
    override fun invalidateDrawable(drawable: Drawable) = invalidateSelf()


    //////////////////////////////////////////
    // Draw
    //////////////////////////////////////////

    override fun draw(canvas: Canvas) {
        drawBar(canvas)
        if (this.averageShouldShow) drawAverage(canvas)
        drawThumb(canvas)
        if (this.averageShouldShow) drawProfilePicture(canvas)
    }

    private fun drawBar(canvas: Canvas) {
        this.sliderBar.draw(canvas)
    }

    private fun drawThumb(canvas: Canvas) {
        val positionWidth = progressValue * sliderBar.bounds.width() / 100f
        updateThumbBounds(positionWidth.roundToInt())

        val thumbScale = mThumbSpring.currentValue.toFloat()
        canvas.save()
        canvas.translate(sliderBar.bounds.left.toFloat(), sliderBar.bounds.top.toFloat())
        canvas.scale(thumbScale, thumbScale, positionWidth, bounds.exactCenterY())
        thumbDrawable.draw(canvas)
        canvas.restore()
    }

    private fun drawProfilePicture(canvas: Canvas) {
        var intrinsicWidth: Float = this.drawableProfileImage.intrinsicWidth.toFloat()
        val intrinsicHeight: Float = this.drawableProfileImage.intrinsicHeight.toFloat()

        val intrinsicWidth2 = progressValue * sliderBar.bounds.width() / 100f
        val height: Float = sliderBar.bounds.height() / 2f

        canvas.save()
        canvas.translate(sliderBar.bounds.left.toFloat(), sliderBar.bounds.top.toFloat())
        canvas.scale(1f, 1f, intrinsicWidth2, height)

        intrinsicWidth /= 2.0f

        this.drawableProfileImage.setBounds(
            (intrinsicWidth2 - intrinsicWidth).toInt(),
            (height - intrinsicHeight).toInt(),
            (intrinsicWidth2 + intrinsicWidth).toInt(),
            (height + intrinsicHeight).toInt()
        )

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
        val heightPosition = (bounds.height() / 2).toFloat()

        val intrinsicWidth = this.drawableAverageCircle.intrinsicWidth.toFloat() / 2
        val intrinsicHeight = this.drawableAverageCircle.intrinsicHeight.toFloat() / 2

        canvas.save()
        canvas.translate(sliderBar.bounds.left.toFloat(), sliderBar.bounds.top.toFloat())
        canvas.scale(scale, scale, widthPosition, heightPosition)

        this.drawableAverageCircle.setBounds(
            (widthPosition - intrinsicWidth).toInt(),
            (heightPosition - intrinsicHeight).toInt(),
            (widthPosition + intrinsicWidth).toInt(),
            (heightPosition + intrinsicHeight).toInt()
        )

        this.drawableAverageCircle.draw(canvas)
        canvas.restore()
    }

    //////////////////////////////////////////
    // Other
    //////////////////////////////////////////

    override fun setAlpha(i: Int) {
        this.thumbDrawable.alpha = i
        this.sliderBar.alpha = i
    }

    override fun setBounds(left: Int, top: Int, right: Int, bottom: Int) {
        super.setBounds(left, top, right, bottom)

        this.sliderBar.setBounds(
            left + sliderHorizontalPadding,
            top,
            right - sliderHorizontalPadding,
            bottom
        )
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        this.thumbDrawable.colorFilter = colorFilter
        this.sliderBar.colorFilter = colorFilter
    }

    private fun Spring.origamiConfig(tension: Double, friction: Double): Spring =
        this.setSpringConfig(SpringConfig.fromOrigamiTensionAndFriction(tension, friction))

    override fun getIntrinsicHeight(): Int {
        super.getIntrinsicHeight()
        return (barPaddingTop + sliderBar.intrinsicHeight + barPaddingBottom)
    }
}

