package com.bernaferrari.emojislider

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.SeekBar
import com.bernaferrari.emojislider.arrowpopupwindow.utils.Util
import com.orhanobut.logger.Logger


class EmojiSliderView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    var emojiHelper: EmojiHelper = EmojiHelper(this.context)
    private val slider2 by lazy { findViewById<View>(R.id.slider_sticker_slider2) }
    private val sliderStickerSlider by lazy { findViewById<SeekBar>(R.id.slider_sticker_slider) }

    val seekView = SliderDrawable(context)

    fun gradientColors(first: Int, second: Int) {
        seekView.sliderBar.color0_f32845l = first
        seekView.sliderBar.color1_f32846m = second
    }

    var sliderParticleSystem: View? = null
        set(value) {
            field = value

            if (value?.background !is EmojiHelper) {
                value?.background = emojiHelper
            }
        }

    init {
        LayoutInflater.from(context).inflate(R.layout.emoji_slider, this, true)
        initializer()
        secondView()
    }


    val bigCircleThumb_f32834a = C7849d(context)



    fun secondView() {
        seekView.callback = this
        seekView.f32871e = C5179d()
        seekView.m18503c()
        seekView.invalidateSelf()

        slider2.background = seekView
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        seekView.onTouch(this, event)
        seekView.invalidateSelf()

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {

            }

            MotionEvent.ACTION_UP -> {

            }


            MotionEvent.ACTION_MOVE -> {
            }
        }

        return super.onTouchEvent(event)
    }


    fun m18483a(i: Int) {
        val c7849d = this.bigCircleThumb_f32834a
        c7849d.f32864e = i.toFloat()
        //        c7849d.f32860a.m10639a(c7849d.f32864e);
        val circleHandleC5190I = c7849d.imageHandle_f32861b
        circleHandleC5190I.radius_f20901a = c7849d.f32864e / 2.0f
        circleHandleC5190I.invalidateSelf()
        val c7852l = c7849d.f32862c
        c7852l.f32890a = c7849d.f32864e
        c7852l.invalidateSelf()
        c7849d.invalidateSelf()
    }


    fun initializer() {
//        sliderStickerSlider.setOnSeekBarChangeListener(this)
        updateThumb("😍")
    }

    fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {

        seekView.sliderBar.percentage_progress_f32847n = (progress / 100.0).toFloat()
//        seekView.updateShader()
        seekView.invalidateSelf()

        if (sliderParticleSystem == null) return

        Logger.d("slider [top]: " + sliderStickerSlider.top + " // slider [paddingTop]: " + sliderStickerSlider.paddingTop + " // slider [bounds top]: " + sliderStickerSlider.thumb.bounds.top)

        val sliderLocation = IntArray(2)
        sliderStickerSlider.getLocationOnScreen(sliderLocation)

        val particleLocation = IntArray(2)
        sliderParticleSystem!!.getLocationOnScreen(particleLocation)

        Logger.d("SLIDER - location [x]: " + sliderLocation[0] + " --- location [y]: " + sliderLocation[1])
        Logger.d("PARTICLE - location [x]:" + particleLocation[0] + " --- location [y]: " + particleLocation[1])

        if (fromUser) {
            this.emojiHelper.onProgressChanged(
                paddingLeft = sliderLocation[0].toFloat() + sliderStickerSlider.paddingLeft + sliderStickerSlider.thumb.bounds.left - particleLocation[0],
                paddingTop = sliderLocation[1].toFloat() + Util.DpToPx(
                    this.context,
                    32f
                ) - particleLocation[1]
            )
            this.emojiHelper.updateProgress(progress.toFloat() / 100.0f)
        }
    }

    fun onStartTrackingTouch(seekBar: SeekBar) = emojiHelper.progressStarted()

    fun onStopTrackingTouch(seekBar: SeekBar) {
        emojiHelper.onStopTrackingTouch()
        seekView.sliderBar.cancelMethod()
    }

    private fun updateThumb(emoji: String) {
        sliderStickerSlider.thumb = generateThumb(
            context = this.context,
            text = emoji,
            size = R.dimen.slider_sticker_slider_handle_size
        )
        emojiHelper.emoji = emoji
    }
}