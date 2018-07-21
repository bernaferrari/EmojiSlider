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
import com.facebook.rebound.SimpleSpringListener
import com.facebook.rebound.Spring
import com.facebook.rebound.SpringConfig
import com.facebook.rebound.SpringSystem
import kotlin.math.roundToInt

interface Lifecycles {
    fun startAnimation()
    fun stopAnimation()
}

class SliderDrawable(
    context: Context,
    private val tracking: TrackingTouch
) : Drawable(), Callback, Lifecycles, View.OnTouchListener {

    lateinit var thumb: Drawable
    val sliderBar: DrawSeekBar = DrawSeekBar()

    var isThumbAllowedToScrollEverywhere = true
    var sliderPadding: Int = 0
    var radiusRelation = 0

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

    private val mAverageSpring: Spring = mSpringSystem.createSpring()
        .origamiConfig(40.0, 7.0)
        .setCurrentValue(0.0)

    //////////////////////////////////////////
    // Average
    //////////////////////////////////////////

    var averagePercentValue = 0.20f
    var averageShouldShow: Boolean = true
    var isThumbSelected = false

    val averageCircle: AverageCircle by lazy {
        AverageCircle(context).apply {
            val voteAverageHandleSize =
                context.resources.getDimensionPixelSize(R.dimen.slider_sticker_slider_vote_average_handle_size)
            this.radius = voteAverageHandleSize.toFloat() / 2.0f
            this.invalidateSelf()
        }
    }

    var thumbAllowReselection: Boolean = true
    var progressValue = 0

    val profileImage: ProfilePicture = ProfilePicture(context)


    fun valueWasSelected() {
        if (thumbAllowReselection) return

        mAverageSpring.endValue = 1.0
        mThumbSpring.endValue = 0.0
        isTouchDisabled = true
        profileImage.updateThis()

        invalidateSelf()
    }

    var isTouchDisabled = false

    internal var colorStart: Int = 0
    internal var colorEnd: Int = 0

    private fun Double.limitToRange() = Math.max(Math.min(this, 1.0), 0.0)

    private fun Rect.containsXY(motionEvent: MotionEvent): Boolean =
        this.contains(motionEvent.x.toInt(), motionEvent.y.toInt())

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {

        if (isTouchDisabled) return false

        val x = motionEvent.x.toInt() - sliderBar.bounds.left
        val y = motionEvent.y.toInt() - sliderBar.bounds.top

        when (motionEvent.actionMasked) {
            MotionEvent.ACTION_DOWN -> {

                println("x: " + x + " y: " + y + " ThumbBounds: " + thumb.bounds.toShortString() + "drawSeekBounds: " + sliderBar.bounds.toShortString() + " left: " + sliderBar.bounds.left + " sliderHorizontalPadding: " + sliderPadding)

                if (this.thumb.bounds.contains(x, y) ||
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

    fun updatePercentage(progress: Int) {
        println("updatePercent: $progress")
        progressValue = progress

        sliderBar.percentProgress = progress / 100f
        sliderBar.invalidateSelf()
        tracking.onProgressChanged(progress)

        invalidateSelf()
    }


    private fun updateThumbBounds(intrinsicWidth2: Int) {

        val customIntrinsicWidth = thumb.intrinsicWidth / 2
        val customIntrinsicHeight = thumb.intrinsicHeight / 2
        val customHeight = bounds.height() / 2

        this.thumb.setBounds(
            intrinsicWidth2 - customIntrinsicWidth,
            customHeight - customIntrinsicHeight,
            intrinsicWidth2 + customIntrinsicWidth,
            customHeight + customIntrinsicHeight
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

    override fun startAnimation() {
        mThumbSpring.addListener(mSpringListener)
        mAverageSpring.addListener(mSpringListener)
    }

    override fun stopAnimation() {
        mThumbSpring.removeListener(mSpringListener)
        mAverageSpring.removeListener(mSpringListener)
    }

    init {
        configureHandle()

        this.profileImage.callback = this
        this.sliderBar.callback = this

        sliderBar.invalidateSelf()
        m18483a(context.resources.getDimensionPixelSize(R.dimen.slider_sticker_tray_slider_handle_size))
        sliderBar.configureHeight(context.resources.getDimensionPixelSize(R.dimen.slider_sticker_tray_track_height))
    }


    fun m18483a(size: Int) {
        profileImage.sizeHandle = size.toFloat()
        //        c7849d.f32860a.m10639a(c7849d.sizeHandle);
        val circleHandleC5190I = profileImage.averageHandle
        circleHandleC5190I.radius = profileImage.sizeHandle / 2.0f
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

    override fun draw(canvas: Canvas) {
        drawBar(canvas)
        if (this.averageShouldShow) drawAverage(canvas)
        drawThumb(canvas)
        if (this.averageShouldShow) drawProfilePicture(canvas)
    }

    private fun configureHandle() {
        mThumbSpring.isOvershootClampingEnabled = true
        mThumbSpring.endValue = 1.0
        startAnimation()
    }

    private fun drawThumb(canvas: Canvas) {

        val intrinsicWidth2 = progressValue * sliderBar.bounds.width() / 100f
        updateThumbBounds(intrinsicWidth2.roundToInt())

        val thumbScale = mThumbSpring.currentValue.toFloat()
        canvas.save()
        canvas.translate(sliderBar.bounds.left.toFloat(), sliderBar.bounds.top.toFloat())
        canvas.scale(thumbScale, thumbScale, intrinsicWidth2, bounds.exactCenterY())
        thumb.draw(canvas)
        canvas.restore()
    }

    private fun drawBar(canvas: Canvas) {
        this.sliderBar.draw(canvas)
    }

    override fun setAlpha(i: Int) {
        this.thumb.alpha = i
        this.sliderBar.alpha = i
    }

    override fun setBounds(left: Int, top: Int, right: Int, bottom: Int) {
        super.setBounds(left, top, right, bottom)

        this.sliderBar.setBounds(
            left + sliderPadding,
            top,
            right - sliderPadding,
            bottom
        )
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        this.thumb.colorFilter = colorFilter
        this.sliderBar.colorFilter = colorFilter
    }

    private val barPaddingTop: Int =
        context.resources.getDimensionPixelSize(R.dimen.slider_sticker_padding_top_without_question)
    private val barPaddingBottom: Int =
        context.resources.getDimensionPixelSize(R.dimen.slider_sticker_padding_bottom_without_question)

    init {
        this.sliderBar.callback = this
        this.m18483a(context.resources.getDimensionPixelSize(R.dimen.slider_sticker_slider_handle_size))

        sliderBar.sliderHeight =
                context.resources.getDimensionPixelSize(R.dimen.slider_sticker_slider_height)
        sliderBar.invalidateSelf()
        sliderBar.configureHeight(context.resources.getDimensionPixelSize(R.dimen.slider_sticker_slider_track_height))
    }


    private fun drawProfilePicture(canvas: Canvas) {
        var intrinsicWidth: Float = this.profileImage.intrinsicWidth.toFloat()
        val intrinsicHeight: Float = this.profileImage.intrinsicHeight.toFloat()

        val intrinsicWidth2 = progressValue * sliderBar.bounds.width() / 100f
        val height: Float = sliderBar.bounds.height() / 2f

        canvas.save()
        canvas.translate(sliderBar.bounds.left.toFloat(), sliderBar.bounds.top.toFloat())
        canvas.scale(1f, 1f, intrinsicWidth2, height)

        intrinsicWidth /= 2.0f

        this.profileImage.setBounds(
            (intrinsicWidth2 - intrinsicWidth).toInt(),
            (height - intrinsicHeight).toInt(),
            (intrinsicWidth2 + intrinsicWidth).toInt(),
            (height + intrinsicHeight).toInt()
        )

        this.profileImage.draw(canvas)
        canvas.restore()
    }

    private fun drawAverage(canvas: Canvas) {
        this.averageCircle.outerColor = ColorHelper.getCorrectColor(
            this.colorStart,
            this.colorEnd,
            this.averagePercentValue
        )

        this.averageCircle.invalidateSelf()

        val scale = this.mAverageSpring.currentValue.toFloat()

        var intrinsicWidth = this.averageCircle.intrinsicWidth.toFloat()
        var intrinsicHeight = this.averageCircle.intrinsicHeight.toFloat()

        val widthPosition =
            this.averagePercentValue * (bounds.width().toFloat() - intrinsicWidth) + intrinsicWidth / 2.0f
        val height = (bounds.height() / 2).toFloat()
        intrinsicWidth /= 2f
        intrinsicHeight /= 2f

        canvas.save()
        canvas.scale(scale, scale, widthPosition, height)

        this.averageCircle.setBounds(
            (widthPosition - intrinsicWidth).toInt(),
            (height - intrinsicHeight).toInt(),
            (widthPosition + intrinsicWidth).toInt(),
            (height + intrinsicHeight).toInt()
        )

        this.averageCircle.draw(canvas)
        canvas.restore()
    }

    private fun Spring.origamiConfig(tension: Double, friction: Double): Spring =
        this.setSpringConfig(SpringConfig.fromOrigamiTensionAndFriction(tension, friction))

    override fun getIntrinsicHeight(): Int {
        super.getIntrinsicHeight()
        return (barPaddingTop + sliderBar.intrinsicHeight + barPaddingBottom)
    }
}

fun Spring.origamiConfig(tension: Double, friction: Double): Spring =
    this.setSpringConfig(SpringConfig.fromOrigamiTensionAndFriction(tension, friction))

