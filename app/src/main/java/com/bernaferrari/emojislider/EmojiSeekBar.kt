package com.bernaferrari.emojislider

import android.content.Context
import android.text.SpannableString
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import com.bernaferrari.emojislider.arrowpopupwindow.utils.Util
import com.orhanobut.logger.Logger


class EmojiSeekBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), OnSeekBarChangeListener {

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

            Logger.d("value background is emojihelper? " + (value?.background !is EmojiHelper))
            if (value?.background !is EmojiHelper) {
                value?.background = emojiHelper
            }
        }

    init {
        LayoutInflater.from(context).inflate(R.layout.emoji_slider, this, true)
        initializer()
        secondView()
    }

    fun secondView() {

        seekView.invalidateSelf()
        seekView.f32871e = C5179d()
        seekView.m18503c()

        slider2.background = seekView
    }

    fun initializer() {
        sliderStickerSlider.setOnSeekBarChangeListener(this)
        updateThumb("😍")
        sliderParticleSystem?.background = emojiHelper
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {

        seekView.sliderBar.percentage_progress_f32847n = (progress / 100.0).toFloat()

        if (sliderParticleSystem == null) return

        Logger.d("slider [top]: " + sliderStickerSlider.top + " // slider [paddingTop]: " + sliderStickerSlider.paddingTop + " // slider [bounds top]: " + sliderStickerSlider.thumb.bounds.top)

        val location = IntArray(2)
        sliderStickerSlider.getLocationOnScreen(location)

        Logger.d("location [x]: " + location[0] + " --- location [y]: " + location[1])
        Logger.d(
            "PARTICLE - relative1 [left]: " + getRelativeLeft(sliderParticleSystem!!) + " --- relative1 [top]: " + getRelativeTop(
                sliderParticleSystem!!
            )
        )
        Logger.d(
            "SLIDER - relative2 [left]: " + getRelativeLeft(sliderStickerSlider) + " --- relative2 [top]: " + getRelativeTop(
                sliderStickerSlider
            )
        )

        // sliderStickerSlider.top = 87

        if (fromUser) {
            this.emojiHelper.onProgressChanged(
                paddingLeft = location[0].toFloat() + sliderStickerSlider.paddingLeft + sliderStickerSlider.thumb.bounds.left,
                paddingTop = location[1].toFloat() - 87 - Util.DpToPx(this.context, 20f)
            )
            this.emojiHelper.updateProgress(progress.toFloat() / 100.0f)
        }
    }

    private fun getRelativeLeft(myView: View): Float = if (myView.parent === myView.rootView) {
        myView.x
    } else {
        myView.x + getRelativeLeft(myView.parent as View)
    }

    private fun getRelativeTop(myView: View): Float = if (myView.parent === myView.rootView) {
        myView.y
    } else {
        myView.y + getRelativeTop(myView.parent as View)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) = emojiHelper.progressStarted()

    override fun onStopTrackingTouch(seekBar: SeekBar) {
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

    private fun generateThumb(context: Context, text: String, size: Int): textDrawable {
        return textDrawable(context, getWidthPixels(context)).apply {
            setSpannable(SpannableString(text))
            setTextSize(context.resources.getDimensionPixelSize(size).toFloat())
        }
    }

    companion object {

        fun getWidthPixels(context: Context): Int {
            return context.resources.displayMetrics.widthPixels
        }
    }
}