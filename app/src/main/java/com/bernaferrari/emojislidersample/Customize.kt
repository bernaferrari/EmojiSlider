package com.bernaferrari.emojislidersample

import android.graphics.Bitmap
import android.os.Bundle
import android.support.transition.AutoTransition
import android.support.transition.TransitionManager
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SimpleItemAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.bernaferrari.emojislider.FlyingEmoji
import com.bernaferrari.emojislidersample.extensions.doOnChanged
import com.bernaferrari.emojislidersample.extensions.inc
import com.bernaferrari.emojislidersample.groupie.ColorPickerItem
import com.bernaferrari.emojislidersample.groupie.EmojiPickerItem
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.control_bar.*
import kotlinx.android.synthetic.main.control_bar_colors.*
import kotlinx.android.synthetic.main.control_bar_emoji.*
import kotlinx.android.synthetic.main.control_bar_thumb.*
import kotlinx.android.synthetic.main.frag_customization.*
import kotlin.properties.ObservableProperty
import kotlin.reflect.KProperty

class Customize : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.frag_customization, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        slider.sliderParticleSystem = slider_particle_system

        setUpRecyclerPicker()
        setUpRecyclerEmoji()

        controlDownToggle.setOnClickListener { uiState.isFlyingDirectionUp = false }
        thumbAllowReselection.setOnClickListener { uiState.thumbAllowReselection++ }

        controlUpToggle.isActivated = true
        controlUpToggle.setOnClickListener { uiState.isFlyingDirectionUp = true }

        shouldShowAverage.isActivated = true
        shouldShowAverage.setOnClickListener { uiState.showAverage++ }

        shouldDisplayPopup.isActivated = true
        shouldDisplayPopup.setOnClickListener { uiState.showPopover++ }

        shouldDisplayPicture.isActivated = true
        shouldDisplayPicture.setOnClickListener { uiState.showPicture++ }

        slider.stopTrackingListener = {
            if (!uiState.thumbAllowReselection) {
                uiState.isValueSet = true
            }
        }

        resetButton.setOnClickListener {
            uiState.isValueSet = false
            slider.resetAnimated()
        }

        averageSeekBar.doOnChanged { _, progress, _ ->
            slider.averagePercentValue = progress / 100f
        }

        updateUiFromState()
    }

    private fun setUpRecyclerPicker() {
        recyclerPicker.apply {
            paintColorList()

            this.layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            this.adapter = GroupAdapter<ViewHolder>()
                .apply { add(Section(colorSelectorList)) }
            this.itemAnimator = this.itemAnimator.apply {
                // From https://stackoverflow.com/a/33302517/4418073
                if (this is SimpleItemAnimator) {
                    this.supportsChangeAnimations = false
                }
            }
        }
    }

    private fun setUpRecyclerEmoji() {
        recyclerEmoji.apply {
            paintEmojiList()

            this.layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            this.adapter = GroupAdapter<ViewHolder>()
                .apply { add(Section(emoijSelectorList)) }
            this.itemAnimator = this.itemAnimator.apply {
                // From https://stackoverflow.com/a/33302517/4418073
                if (this is SimpleItemAnimator) {
                    this.supportsChangeAnimations = false
                }
            }
        }
    }

    private val colorsList = Constants.getGradients()
    private val colorSelectorList = mutableListOf<ColorPickerItem>()

    private val emojisList = Constants.getEmojis()
    private val emoijSelectorList = mutableListOf<EmojiPickerItem>()

    private fun paintColorList(selectedColor: Pair<Int, Int> = colorsList.first()) {
        // Create each color picker item, checking for the first (because it needs extra margin)
        // and checking for the one which is selected (so it becomes selected)
        Constants.getGradients().mapIndexedTo(colorSelectorList) { index, it ->

            ColorPickerItem(
                index == 0,
                selectedColor == it,
                it
            ) { itemClicked ->

                // When a value is selected, all others must be unselected.
                colorSelectorList.forEach { listItem ->
                    if (listItem != itemClicked && listItem.isSwitchOn) {
                        listItem.deselectAndNotify()
                    }
                }

                uiState.colorStart = itemClicked.gradientColor.first
                uiState.colorEnd = itemClicked.gradientColor.second

                paintColorList(itemClicked.gradientColor)
            }
        }
    }

    private fun paintEmojiList(selectedEmoji: String = emojisList.first()) {
        // Create each color picker item, checking for the first (because it needs extra margin)
        // and checking for the one which is selected (so it becomes selected)
        emojisList.mapIndexedTo(emoijSelectorList) { index, it ->

            EmojiPickerItem(
                it,
                index == 0,
                selectedEmoji == it
            ) { itemClicked ->

                // When a value is selected, all others must be unselected.
                emoijSelectorList.forEach { listItem ->
                    if (listItem != itemClicked && listItem.isSwitchOn) {
                        listItem.deselectAndNotify()
                    }
                }

                slider.emoji = itemClicked.emoji

                paintEmojiList(itemClicked.emoji)
            }
        }
    }

    private val transition = AutoTransition().apply { duration = 175 }
    private val uiState = UiState { updateUiFromState() }

    private fun updateUiFromState() {
        beginDelayedTransition()

        slider.colorStart = uiState.colorStart
        slider.colorEnd = uiState.colorEnd

        slider.thumbAllowReselection = uiState.thumbAllowReselection
        thumbAllowReselection.isActivated = uiState.thumbAllowReselection

        slider.shouldDisplayAverage = uiState.showAverage
        shouldShowAverage.isActivated = uiState.showAverage
        shouldShowAverage.isVisible = !thumbAllowReselection.isActivated

        slider.shouldDisplayTooltip = uiState.showPopover && uiState.showAverage
        shouldDisplayPopup.isVisible = uiState.showAverage && !thumbAllowReselection.isActivated &&
                !uiState.isValueSet
        shouldDisplayPopup.isActivated = uiState.showPopover

        controlUpToggle.isActivated = uiState.isFlyingDirectionUp
        controlDownToggle.isActivated = !uiState.isFlyingDirectionUp

        if (uiState.isFlyingDirectionUp) {
            slider.flyingEmojiDirection = FlyingEmoji.Direction.UP
        } else {
            slider.flyingEmojiDirection = FlyingEmoji.Direction.DOWN
        }

        updateIsValueSet(uiState.isValueSet)

        shouldDisplayPicture.isActivated = uiState.showPicture

        if (uiState.showPicture) {
            showBitmapFromGlide()
        } else {
            slider.resultDrawable.imageDrawable.drawable = null
        }
    }

    private fun updateIsValueSet(value: Boolean) {
        resetButton.isVisible = value
        transparentReset.isVisible = value
        shouldDisplayPicture.isVisible = value

        controlDownToggle.isVisible = !value
        controlUpToggle.isVisible = !value
        thumbAllowReselection.isVisible = !value
    }

    private fun showBitmapFromGlide() {
        Glide.with(this)
            .asBitmap()
            .load(
                "https://scontent.fbfh2-1.fna.fbcdn.net" +
                        "/v/t1.0-9/14563570_10205302598764315_2795817981757247033_n.jpg" +
                        "?_nc_cat=0&oh=e5e866251c1ce98e9a3299bb30ed8d6d&oe=5BD56A64"
            )
            .into(object : SimpleTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    slider.setResultDrawable(resource)
                }
            })
    }

    private fun beginDelayedTransition() =
        TransitionManager.beginDelayedTransition(container, transition)

    private class UiState(private val callback: () -> Unit) {

        private inner class BooleanProperty(initialValue: Boolean) :
            ObservableProperty<Boolean>(initialValue) {
            override fun afterChange(property: KProperty<*>, oldValue: Boolean, newValue: Boolean) {
                callback()
            }
        }

        private inner class IntProperty(initialValue: Int) : ObservableProperty<Int>(initialValue) {
            override fun afterChange(property: KProperty<*>, oldValue: Int, newValue: Int) {
                callback()
            }
        }

        var showAverage by BooleanProperty(true)
        var showPopover by BooleanProperty(true)
        var showPicture by BooleanProperty(true)
        var thumbAllowReselection by BooleanProperty(false)
        var colorStart by IntProperty(Constants.getGradients().first().first)
        var colorEnd by IntProperty(Constants.getGradients().first().second)

        var isFlyingDirectionUp by BooleanProperty(true)
        var isValueSet by BooleanProperty(false)
    }
}
