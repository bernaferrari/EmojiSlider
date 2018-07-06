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

class EmojiSeekBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), OnSeekBarChangeListener {

    var emojiHelper: EmojiHelper = EmojiHelper(this.context)

    // seekBarSlider contains only the thumb
    private val seekBarSlider by lazy { findViewById<SeekBar>(R.id.slider_sticker_slider) }

    var sliderParticleSystem: View? = null
        set(value) {
            field = value

            Logger.d("value background is emojihelper? " + (value?.background !is EmojiHelper))
            if (value?.background !is EmojiHelper) {
                value?.background = emojiHelper
            }
        }

    fun setBackgroundView(backgroundView: View, emojiHelper: EmojiHelper? = null) {
        sliderParticleSystem = backgroundView
        if (emojiHelper != null) {
            this.emojiHelper = emojiHelper
        }
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.emoji_seekbar, this, true)

        seekBarSlider.setOnSeekBarChangeListener(this)
        updateThumb("😍")
        sliderParticleSystem?.background = emojiHelper
    }

    // TAKE CARE OF THE THUMB AND PROGRESS CHANGES

    override fun onStartTrackingTouch(seekBar: SeekBar) = emojiHelper.progressStarted()

    override fun onStopTrackingTouch(seekBar: SeekBar) = emojiHelper.onStopTrackingTouch()

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        if (sliderParticleSystem == null) return

        val location = IntArray(2)
        seekBarSlider.getLocationOnScreen(location)

        // seekBarSlider.top = 87
        if (fromUser) {
            Logger.d("paddingTop: " + (location[1].toFloat() - 87 - Util.DpToPx(this.context, 20f)))

            this.emojiHelper.onProgressChanged(
                paddingLeft = location[0].toFloat() + seekBarSlider.paddingLeft + seekBarSlider.thumb.bounds.left,
                paddingTop = location[1].toFloat() - 87 - Util.DpToPx(this.context, 20f)
            )
            this.emojiHelper.updateProgress(progress.toFloat() / 100.0f)
        }
    }

    private fun updateThumb(emoji: String) {
        emojiHelper.emoji = emoji
        seekBarSlider.thumb = generateThumb(
            context = this.context,
            text = emoji,
            size = R.dimen.slider_sticker_slider_handle_size
        )
    }
}