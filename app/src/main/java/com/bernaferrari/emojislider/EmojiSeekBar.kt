package com.bernaferrari.emojislider

import android.content.Context
import android.graphics.drawable.LayerDrawable
import android.text.SpannableString
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import com.orhanobut.logger.Logger


class EmojiSeekBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), OnSeekBarChangeListener {

    var emojiHelper: EmojiHelper = EmojiHelper(this.context)
    private var bool1: Boolean = false

    private val slider2 by lazy { findViewById<View>(R.id.slider_sticker_slider2) }
    private val sliderStickerEditor by lazy { findViewById<View>(R.id.slider_sticker_editor2) }
    private val sliderStickerSlider by lazy { findViewById<SeekBar>(R.id.slider_sticker_slider) }

    var sliderParticleSystem: View? = null
        set(value) {
            field = value
            value?.background = emojiHelper
        }

    init {
        LayoutInflater.from(context).inflate(R.layout.emoji_slider, this, true)
        initializer()
        mo1583a()
    }

    fun mo1583a() {
        val c5179d = C5179d()

        val c7850j = Bolinha(context)
        c7850j.f32869c = true
        c7850j.invalidateSelf()
        c7850j.f32871e = c5179d
        c7850j.m18503c()

        slider2.background = c7850j
    }

    fun m17121a() {
        if (this.bool1) {
            m17115a()
        }
    }

    private fun m17115a() {

        val c5179d = C5179d()
        val c7850j = Bolinha(context)
        c7850j.f32869c = true
        c7850j.invalidateSelf()
        c7850j.f32871e = c5179d
        c7850j.m18503c()

        val layerdrawable = sliderStickerSlider.progressDrawable as LayerDrawable
        layerdrawable.setDrawableByLayerId(-1, c7850j)
        //        C0790f.m2003a(((LayerDrawable) this.sliderStickerSlider.getProgressDrawable()).getDrawable(i), i2);
    }

    fun initializer() {

        val c5179d = C5179d()
        val c7850j = C7850j(context)
        c7850j.f32869c = true
        c7850j.invalidateSelf()
        c7850j.f32871e = c5179d
        c7850j.m18503c()

        m17121a()

        sliderStickerSlider.setOnSeekBarChangeListener(this)
        updateThumb("😍")
        sliderParticleSystem?.background = emojiHelper

        EmojiSeekBar.m17117b(this)
    }

    override fun onProgressChanged(seekBar: SeekBar, i: Int, z: Boolean) {

        Logger.d("y: " + sliderStickerEditor.y + " // paddingTop: " + sliderStickerEditor.paddingTop + " // top: " + sliderStickerSlider.top + " bounds.top " + sliderStickerSlider.thumb.bounds.top)
        Logger.d("totalsum: " + (sliderStickerEditor.y + sliderStickerEditor.paddingTop + sliderStickerSlider.top + sliderStickerSlider.thumb.bounds.top))

        val location = IntArray(2)
        sliderStickerSlider.getLocationOnScreen(location)
        Logger.d("location.x: " + location[0] + " --- location.y: " + location[1])
        Logger.d(
            "relative.x: " + getRelativeLeft(sliderParticleSystem!!) + " --- relative.top: " + getRelativeTop(
                sliderParticleSystem!!
            )
        )

        if (z) {
            this.emojiHelper.onProgressChanged(
                paddingLeft = location[0].toFloat() + sliderStickerSlider.paddingLeft + sliderStickerSlider.thumb.bounds.left,
                paddingTop = location[1].toFloat() - sliderStickerSlider.top - sliderStickerSlider.thumb.bounds.top
            )
            this.emojiHelper.updateProgress(i.toFloat() / 100.0f)
        }
    }

    private fun getRelativeLeft(myView: View): Float {
        return if (myView.parent === myView.rootView)
            myView.x + myView.paddingLeft
        else
            myView.x + myView.paddingLeft + getRelativeLeft(myView.parent as View)
    }

    private fun getRelativeTop(myView: View): Float {
        return if (myView.parent === myView.rootView)
            myView.y + myView.paddingTop
        else
            myView.y + myView.paddingTop + getRelativeTop(myView.parent as View)
    }


    override fun onStartTrackingTouch(seekBar: SeekBar) = emojiHelper.progressStarted()

    override fun onStopTrackingTouch(seekBar: SeekBar) = emojiHelper.onStopTrackingTouch()

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

        fun m17117b(EmojiSeekBar: EmojiSeekBar) {
            if (EmojiSeekBar.bool1) {
                //            EmojiSeekBar.sliderStickerBackgroundButton.setImageResource(R.drawable.text_bg_on);
            } else {
                //            EmojiSeekBar.sliderStickerBackgroundButton.setImageResource(R.drawable.text_bg_off);
            }
        }

        fun getWidthPixels(context: Context): Int {
            return context.resources.displayMetrics.widthPixels
        }
    }
}