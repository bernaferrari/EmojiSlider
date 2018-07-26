package com.bernaferrari.emojislidersample

import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.core.view.updatePadding
import com.bernaferrari.emojislider.DpToPx
import com.bernaferrari.emojislider.FlyingEmoji
import com.bernaferrari.emojislider.textToDrawable


class EmojiSeekBar : SeekBar, OnSeekBarChangeListener {

    var emoji = "😍"
        set(value) {
            field = value
            updateThumb(field)
        }

    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
    ) : super(context, attrs, defStyleAttr, 0) {


        this.flyingEmoji = FlyingEmoji(this.context)
        this.splitTrack = false
        this.progressDrawable =
                ContextCompat.getDrawable(this.context, R.drawable.slider_sticker_gradient)
        this.progress = 10

        this.updatePadding(
            left = resources.getDimensionPixelSize(R.dimen.slider_sticker_padding_horizontal),
            right = resources.getDimensionPixelSize(R.dimen.slider_sticker_padding_horizontal)
        )

        this.setOnSeekBarChangeListener(this)
        updateThumb("😍")
    }

    constructor(
        context: Context,
        attrs: AttributeSet? = null
    ) : super(context, attrs, 0, 0) {


        this.flyingEmoji = com.bernaferrari.emojislider.FlyingEmoji(this.context)
        this.splitTrack = false
        this.progressDrawable =
                ContextCompat.getDrawable(this.context, R.drawable.slider_sticker_gradient)
        this.progress = 10

        this.updatePadding(
            left = resources.getDimensionPixelSize(R.dimen.slider_sticker_padding_horizontal),
            right = resources.getDimensionPixelSize(R.dimen.slider_sticker_padding_horizontal)
        )

        this.setOnSeekBarChangeListener(this)
        updateThumb("😍")
    }

    constructor(
        context: Context
    ) : super(context, null, 0, 0) {

        this.flyingEmoji = com.bernaferrari.emojislider.FlyingEmoji(this.context)
        this.splitTrack = false
        this.progressDrawable =
                ContextCompat.getDrawable(this.context, R.drawable.slider_sticker_gradient)
        this.progress = 10

        this.updatePadding(
            left = resources.getDimensionPixelSize(R.dimen.slider_sticker_padding_horizontal),
            right = resources.getDimensionPixelSize(R.dimen.slider_sticker_padding_horizontal)
        )

        this.setOnSeekBarChangeListener(this)
        updateThumb("😍")
    }

    var flyingEmoji: com.bernaferrari.emojislider.FlyingEmoji
    private val sliderStickerSlider by lazy { this }

    var sliderParticleSystem: View? = null
        set(value) {
            field = value

            if (value?.background !is com.bernaferrari.emojislider.FlyingEmoji) {
                value?.background = flyingEmoji
            } else {
                flyingEmoji = value.background as FlyingEmoji
            }
        }

    override fun onStartTrackingTouch(seekBar: SeekBar) = progressStarted()

    override fun onStopTrackingTouch(seekBar: SeekBar) = flyingEmoji.onStopTrackingTouch()

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {

        if (sliderParticleSystem == null) return

        val sliderLocation = IntArray(2)
        sliderStickerSlider.getLocationOnScreen(sliderLocation)

        val particleLocation = IntArray(2)
        sliderParticleSystem!!.getLocationOnScreen(particleLocation)

        if (fromUser) {
            this.flyingEmoji.onProgressChanged(
                percent = progress.toFloat() / 100.0f,
                paddingLeft = sliderLocation[0].toFloat() + sliderStickerSlider.paddingLeft + sliderStickerSlider.thumb.bounds.left - particleLocation[0],
                paddingTop = sliderLocation[1].toFloat() + DpToPx(
                    this.context,
                    32f
                ) - particleLocation[1]
            )
        }
    }

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
            direction = FlyingEmoji.Direction.UP,
            paddingLeft = paddingLeft,
            paddingTop = paddingTop
        )
    }

    private fun getPaddingForFlyingEmoji(): Pair<Float, Float> {
        val sliderLocation = IntArray(2)
        sliderStickerSlider.getLocationOnScreen(sliderLocation)

        val particleLocation = IntArray(2)
        sliderParticleSystem!!.getLocationOnScreen(particleLocation)

        return Pair(
            sliderLocation[0].toFloat() + sliderStickerSlider.paddingLeft + sliderStickerSlider.thumb.bounds.left - particleLocation[0],
            sliderLocation[1].toFloat() + DpToPx(this.context, 32f) - particleLocation[1]
        )
    }

    private fun updateThumb(emoji: String) {
        sliderStickerSlider.thumb = textToDrawable(
            context = this.context,
            text = emoji,
            size = com.bernaferrari.emojislider.R.dimen.slider_sticker_slider_handle_size
        )
    }
}