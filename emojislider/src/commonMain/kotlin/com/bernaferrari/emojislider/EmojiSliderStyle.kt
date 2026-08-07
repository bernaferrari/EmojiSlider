package com.bernaferrari.emojislider

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Colors and active-track brush for [EmojiSlider]. */
data class EmojiSliderColors(
    val start: Color = Color(0xFF6200EE),
    val end: Color = Color(0xFFE91E63),
    val track: Color = Color(0xFFE0E0E0),
    val activeTrack: Brush = Brush.horizontalGradient(listOf(start, end)),
)

/** Interaction and reveal policy for [EmojiSlider]. */
data class EmojiSliderBehavior(
    val isUserSeekable: Boolean = true,
    val registerTouchOnTrack: Boolean = true,
    val allowReselection: Boolean = false,
    val floatingDirection: FloatingEmojiDirection = FloatingEmojiDirection.UP,
    val displayAverage: Boolean = true,
    val displayResult: Boolean = true,
    val displayTooltip: Boolean = true,
    val tooltipText: String = "Average value",
    val tooltipAutoDismissMillis: Long = DEFAULT_TOOLTIP_DISMISS_MILLIS,
)

/**
 * Layout and particle sizes for [EmojiSlider].
 *
 * [trackInset] `null` means half of [thumbSize], so the emoji thumb stays on-canvas at 0 and 1.
 * Pass `0.dp` for an edge-to-edge track.
 */
data class EmojiSliderSizes(
    val trackHeight: Dp = 16.dp,
    val thumbSize: Dp = 56.dp,
    val sliderHeight: Dp = 80.dp,
    val trackInset: Dp? = null,
    val minEmojiSize: Dp = DefaultMinEmojiSize,
    val maxEmojiSize: Dp = DefaultMaxEmojiSize,
    val thumbPressedScale: Float = DEFAULT_THUMB_PRESSED_SCALE,
) {
    val resolvedTrackInset: Dp get() = trackInset ?: thumbSize / 2
}
