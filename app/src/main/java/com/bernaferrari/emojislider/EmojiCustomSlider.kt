package com.bernaferrari.emojislider

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import com.bernaferrari.emojislider.arrowpopupwindow.utils.Util
import com.orhanobut.logger.Logger


class EmojiCustomSlider @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), OnSeekBarChangeListener {

    var emojiHelper: EmojiHelper = EmojiHelper(this.context)

    // viewSlider contains everything except the thumb
    private val viewSlider by lazy { findViewById<View>(R.id.slider_sticker_slider2) }

    // seekBarSlider contains only the thumb
    private val seekBarSlider by lazy { findViewById<SeekBar>(R.id.slider_sticker_slider) }

//    private val seekView = SliderDrawable(context)

//    fun gradientColors(first: Int, second: Int) {
//        seekView.sliderBar.color0_f32845l = first
//        seekView.sliderBar.color1_f32846m = second
//    }

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

//        seekView.invalidateSelf()
//        seekView.f32871e = C5179d()
//        seekView.m18503c()

//        viewSlider.background = seekView
    }

    // TAKE CARE OF THE THUMB AND PROGRESS CHANGES

    fun initializer() {
        seekBarSlider.setOnSeekBarChangeListener(this)
        updateThumb("😍")
        sliderParticleSystem?.background = emojiHelper
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {

//        seekView.sliderBar.percentage_progress_f32847n = (progress / 100.0).toFloat()
//        seekView.updateShader()
//        seekView.invalidateSelf()

        if (sliderParticleSystem == null) return

        Logger.d("slider [top]: " + seekBarSlider.top + " // slider [paddingTop]: " + seekBarSlider.paddingTop + " // slider [bounds top]: " + seekBarSlider.thumb.bounds.top)

        val location = IntArray(2)
        seekBarSlider.getLocationOnScreen(location)

        Logger.d("location [x]: " + location[0] + " --- location [y]: " + location[1])
        Logger.d(
            "PARTICLE - relative1 [left]: " + getRelativeLeft(sliderParticleSystem!!) + " --- relative1 [top]: " + getRelativeTop(
                sliderParticleSystem!!
            )
        )
        Logger.d(
            "SLIDER - relative2 [left]: " + getRelativeLeft(seekBarSlider) + " --- relative2 [top]: " + getRelativeTop(
                seekBarSlider
            )
        )

        // seekBarSlider.top = 87

        if (fromUser) {
            this.emojiHelper.onProgressChanged(
                paddingLeft = location[0].toFloat() + seekBarSlider.paddingLeft + seekBarSlider.thumb.bounds.left,
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
//        seekView.sliderBar.cancelMethod()
    }

    private fun updateThumb(emoji: String) {
//        seekBarSlider.thumb = generateThumb(
//            context = this.context,
//            text = emoji,
//            size = R.dimen.slider_sticker_slider_handle_size
//        )
        emojiHelper.emoji = emoji
    }
}