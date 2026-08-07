package com.bernaferrari.emojislider

import androidx.compose.ui.unit.dp

internal const val DEFAULT_PROGRESS = 0.25f
internal const val DEFAULT_AVERAGE_PROGRESS = 0.5f
internal const val DEFAULT_TOOLTIP_DISMISS_MILLIS = 2500L
internal const val DEFAULT_THUMB_PRESSED_SCALE = 0.9f
internal const val TAP_RELEASE_PARTICLE_DELAY_MILLIS = 90L
internal const val DEFAULT_EMOJI = "😍"
internal val DefaultMinEmojiSize = 24.dp
internal val DefaultMaxEmojiSize = 48.dp
internal const val EMOJI_SLIDER_TEST_TAG = "emoji_slider"
internal const val EMOJI_SLIDER_TOOLTIP_TEST_TAG = "emoji_slider_tooltip"

/** Absolute Y travel (px) for released floating-emoji fly-away. Sign depends on direction. */
internal const val FLOATING_EMOJI_TRAVEL_DISTANCE_PX = 430f
