package com.bernaferrari.emojislider

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize

/** Mutable session for one [EmojiSlider]: progress, selection, and floating-emoji tracking. */
@Stable
internal class EmojiSliderHandle(
    initialProgress: Float,
    val floating: FloatingEmojiController,
    val tooltip: TooltipState,
) {
    var metrics = EmojiSliderMetrics(0f, 0f, 0f, 0f)
    var isUserSeekable = true
    var allowReselection = false
    var shouldDisplayAverage = true
    var shouldDisplayTooltip = true
    var minEmojiSize: Dp = DefaultMinEmojiSize
    var maxEmojiSize: Dp = DefaultMaxEmojiSize
    var emoji: String = DEFAULT_EMOJI
    var direction: FloatingEmojiDirection = FloatingEmojiDirection.UP
    var onValueChange: (Float) -> Unit = {}
    var onStartTracking: () -> Unit = {}
    var onStopTracking: () -> Unit = {}
    var onHaptic: () -> Unit = {}
    var mapToOverlay: (Offset) -> Offset = { it }

    var progress by mutableFloatStateOf(initialProgress.limitToRange())
    var isDragging by mutableStateOf(false)
    var isValueSelected by mutableStateOf(false)
    var canvasSize by mutableStateOf(IntSize.Zero)
    var canvasCoordinates by mutableStateOf<LayoutCoordinates?>(null)

    val canInteract: Boolean
        get() = sliderCanInteract(isUserSeekable, isValueSelected, allowReselection)

    fun geometry(width: Float = canvasSize.width.toFloat()): SliderGeometry = metrics.geometry(width)

    fun syncFromValue(value: Float) {
        if (!isDragging) progress = value.limitToRange()
    }

    fun onAllowReselectionChanged() {
        if (allowReselection && isValueSelected) {
            isValueSelected = false
            tooltip.hide()
        }
    }

    fun applySemanticsProgress(target: Float): Boolean {
        if (!canInteract) return false
        progress = target.limitToRange()
        onValueChange(progress)
        return true
    }

    fun beginAt(x: Float) {
        if (canvasSize.width == 0) return
        tooltip.hide()
        onHaptic()
        setProgressFromX(x)
        isDragging = true
        publishTracking(start = true)
        onStartTracking()
    }

    fun dragTo(x: Float) {
        if (canvasSize.width == 0) return
        setProgressFromX(x)
        publishTracking(start = false)
    }

    fun end(commitSelection: Boolean) {
        if (!isDragging) return
        if (canvasSize.width > 0) publishTracking(start = false)
        isDragging = false
        floating.stopTracking()
        onStopTracking()

        if (shouldCommitSelection(commitSelection, allowReselection)) {
            isValueSelected = true
            if (shouldShowAverageTooltip(true, shouldDisplayAverage, shouldDisplayTooltip)) {
                tooltip.show()
            }
        }
    }

    private fun setProgressFromX(x: Float) {
        progress = geometry().progressFor(x)
        onValueChange(progress)
    }

    private fun publishTracking(start: Boolean) {
        val hover = mapToOverlay(geometry().hoverEmojiCenter(progress))
        if (start) {
            floating.startTracking(
                emoji = emoji,
                position = hover,
                direction = direction,
                minSize = minEmojiSize,
                maxSize = maxEmojiSize,
            )
        }
        floating.updateProgress(progress, hover)
    }
}
