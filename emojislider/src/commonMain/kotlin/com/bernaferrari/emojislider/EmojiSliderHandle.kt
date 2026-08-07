package com.bernaferrari.emojislider

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize

internal data class EmojiSliderConfig(
    val trackHeightPx: Float = 0f,
    val thumbSizePx: Float = 0f,
    val sliderHeightPx: Float = 0f,
    val trackInsetPx: Float = 0f,
    val isUserSeekable: Boolean = true,
    val allowReselection: Boolean = false,
    val shouldDisplayAverage: Boolean = true,
    val shouldDisplayTooltip: Boolean = true,
    val minEmojiSize: Dp = DefaultMinEmojiSize,
    val maxEmojiSize: Dp = DefaultMaxEmojiSize,
    val emoji: String = DEFAULT_EMOJI,
    val direction: FloatingEmojiDirection = FloatingEmojiDirection.UP,
    val onValueChange: (Float) -> Unit = {},
    val onStartTracking: () -> Unit = {},
    val onStopTracking: () -> Unit = {},
    val haptic: HapticFeedback? = null,
    val mapToOverlay: (Offset) -> Offset = { it },
) {
    fun geometry(width: Float): SliderGeometry = sliderGeometry(
        width = width,
        trackHeightPx = trackHeightPx,
        thumbSizePx = thumbSizePx,
        sliderHeightPx = sliderHeightPx,
        trackInsetPx = trackInsetPx,
    )
}

internal fun sliderCanInteract(
    isUserSeekable: Boolean,
    isValueSelected: Boolean,
    allowReselection: Boolean,
): Boolean = isUserSeekable && (!isValueSelected || allowReselection)

internal fun shouldCommitSelection(
    selectValue: Boolean,
    allowReselection: Boolean,
): Boolean = selectValue && !allowReselection

internal fun shouldShowAverageTooltip(
    selectionCommitted: Boolean,
    shouldDisplayAverage: Boolean,
    shouldDisplayTooltip: Boolean,
): Boolean = selectionCommitted && shouldDisplayAverage && shouldDisplayTooltip

/** Mutable session for one [EmojiSlider]: progress, selection, and floating-emoji tracking. */
@Stable
internal class EmojiSliderHandle(
    initialProgress: Float,
    val floating: FloatingEmojiController,
    val tooltip: TooltipState,
) {
    var config = EmojiSliderConfig()

    var progress by mutableFloatStateOf(initialProgress.limitToRange())
    var isDragging by mutableStateOf(false)
    var isValueSelected by mutableStateOf(false)
    var canvasSize by mutableStateOf(IntSize.Zero)
    var canvasCoordinates by mutableStateOf<LayoutCoordinates?>(null)

    val canInteract: Boolean
        get() = sliderCanInteract(config.isUserSeekable, isValueSelected, config.allowReselection)

    fun geometry(width: Float = canvasSize.width.toFloat()): SliderGeometry = config.geometry(width)

    fun syncFromValue(value: Float) {
        if (!isDragging) progress = value.limitToRange()
    }

    fun onAllowReselectionChanged() {
        if (config.allowReselection && isValueSelected) {
            isValueSelected = false
            tooltip.hide()
        }
    }

    fun applySemanticsProgress(target: Float): Boolean {
        if (!canInteract) return false
        progress = target.limitToRange()
        config.onValueChange(progress)
        return true
    }

    fun beginAt(x: Float) {
        if (canvasSize.width == 0) return
        tooltip.hide()
        config.haptic?.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        setProgressFromX(x)
        isDragging = true
        publishHover(start = true)
        config.onStartTracking()
    }

    fun dragTo(x: Float) {
        if (canvasSize.width == 0) return
        setProgressFromX(x)
        publishHover(start = false)
    }

    fun end(commitSelection: Boolean) {
        if (!isDragging) return
        if (canvasSize.width > 0) publishHover(start = false)
        isDragging = false
        floating.stopTracking()
        config.onStopTracking()

        if (shouldCommitSelection(commitSelection, config.allowReselection)) {
            isValueSelected = true
            if (shouldShowAverageTooltip(true, config.shouldDisplayAverage, config.shouldDisplayTooltip)) {
                tooltip.show()
            }
        }
    }

    private fun setProgressFromX(x: Float) {
        progress = geometry().progressFor(x)
        config.onValueChange(progress)
    }

    private fun publishHover(start: Boolean) {
        val hover = config.mapToOverlay(geometry().hoverEmojiCenter(progress))
        if (start) {
            floating.startTracking(
                emoji = config.emoji,
                position = hover,
                direction = config.direction,
                minSize = config.minEmojiSize,
                maxSize = config.maxEmojiSize,
                progress = progress,
            )
        } else {
            floating.updateProgress(progress, hover)
        }
    }
}
