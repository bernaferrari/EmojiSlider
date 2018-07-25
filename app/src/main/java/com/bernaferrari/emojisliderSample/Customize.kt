package com.bernaferrari.emojisliderSample

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
import com.bernaferrari.emojisliderSample.extensions.doOnChanged
import com.bernaferrari.emojisliderSample.extensions.inc
import com.bernaferrari.emojisliderSample.groupie.ColorPickerItem
import com.bernaferrari.emojisliderSample.groupie.EmojiPickerItem
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

        spring.sliderParticleSystem = slider_particle_system

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

        controlDownToggle.setOnClickListener { uiState.isFlyingDirectionUp = false }
        thumbAllowReselection.setOnClickListener { uiState.thumbAllowReselection++ }

        controlUpToggle.isActivated = true
        controlUpToggle.setOnClickListener { uiState.isFlyingDirectionUp = true }

        showAverage.isActivated = true
        showAverage.setOnClickListener { uiState.showAverage++ }

        showPopOver.isActivated = true
        showPopOver.setOnClickListener { uiState.showPopover++ }

        spring.stopTrackingListener = {
            if (!uiState.thumbAllowReselection) {
                uiState.isValueSet = true
            }
        }

        resetButton.setOnClickListener {
            uiState.isValueSet = false
            spring.resetAnimated()
        }

        averageSeekBar.doOnChanged { _, progress, _ ->
            spring.averagePercentValue = progress / 100f
        }

        updateUiFromState()
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

                spring.emoji = itemClicked.emoji

                paintEmojiList(itemClicked.emoji)
            }
        }
    }

    private val transition = AutoTransition().apply { duration = 175 }
    private val uiState = UiState { updateUiFromState() }

    private fun updateUiFromState() {
        beginDelayedTransition()

        spring.colorStart = uiState.colorStart
        spring.colorEnd = uiState.colorEnd

        spring.thumbAllowReselection = uiState.thumbAllowReselection
        thumbAllowReselection.isActivated = uiState.thumbAllowReselection

        spring.shouldDisplayAverage = uiState.showAverage
        showAverage.isActivated = uiState.showAverage
        showAverage.isVisible = !thumbAllowReselection.isActivated

        spring.shouldDisplayPopup = uiState.showPopover && uiState.showAverage
        showPopOver.isVisible = uiState.showAverage && !thumbAllowReselection.isActivated
        showPopOver.isActivated = uiState.showPopover

        controlUpToggle.isActivated = uiState.isFlyingDirectionUp
        controlDownToggle.isActivated = !uiState.isFlyingDirectionUp

        if (uiState.isFlyingDirectionUp) {
            spring.flyingEmojiDirection = FlyingEmoji.Direction.UP
        } else {
            spring.flyingEmojiDirection = FlyingEmoji.Direction.DOWN
        }

        resetButton.isVisible = uiState.isValueSet

        controlDownToggle.isVisible = !uiState.isValueSet
        controlUpToggle.isVisible = !uiState.isValueSet

        transparentReset.isVisible = uiState.isValueSet
        thumbAllowReselection.isVisible = !uiState.isValueSet
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
        var thumbAllowReselection by BooleanProperty(false)
        var colorStart by IntProperty(Constants.getGradients().first().first)
        var colorEnd by IntProperty(Constants.getGradients().first().second)

        var isFlyingDirectionUp by BooleanProperty(true)
        var isValueSet by BooleanProperty(false)
    }
}