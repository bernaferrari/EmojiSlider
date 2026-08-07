package com.bernaferrari.emojislider

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp

/**
 * Vertical direction used when a released emoji flies away.
 */
enum class FloatingEmojiDirection {
    UP,
    DOWN,
}

/** Signed Y travel (px) for a released particle: negative = up, positive = down. */
internal fun floatingEmojiTravelTargetY(direction: FloatingEmojiDirection): Float = when (direction) {
    FloatingEmojiDirection.UP -> -FLOATING_EMOJI_TRAVEL_DISTANCE_PX
    FloatingEmojiDirection.DOWN -> FLOATING_EMOJI_TRAVEL_DISTANCE_PX
}

/** Signed rotation (degrees) applied over the fly-away animation. */
internal fun floatingEmojiTravelRotationDegrees(direction: FloatingEmojiDirection): Float = when (direction) {
    FloatingEmojiDirection.UP -> -35f
    FloatingEmojiDirection.DOWN -> 35f
}

/** Remembers a [FloatingEmojiController] for driving [FloatingEmojiCanvas]. */
@Composable
fun rememberFloatingEmojiState(): FloatingEmojiController = remember { FloatingEmojiController() }

/**
 * Mutable tracking state shared between [EmojiSlider] and the floating emoji canvas/overlay.
 *
 * When using [EmojiSliderParticleSystem], [minSize] / [maxSize] set on [startTracking] are
 * observed by the ambient [FloatingEmojiCanvas] so slider size params are not ignored.
 */
class FloatingEmojiController {
    private var _isTracking by mutableStateOf(false)
    private var _progress by mutableStateOf(0f)
    private var _position by mutableStateOf(Offset.Zero)
    private var _emoji by mutableStateOf(DEFAULT_EMOJI)
    private var _direction by mutableStateOf(FloatingEmojiDirection.UP)
    private var _minSize by mutableStateOf(DefaultMinEmojiSize)
    private var _maxSize by mutableStateOf(DefaultMaxEmojiSize)

    val isTracking: Boolean get() = _isTracking
    val progress: Float get() = _progress
    val position: Offset get() = _position
    val emoji: String get() = _emoji
    val direction: FloatingEmojiDirection get() = _direction
    val minSize: Dp get() = _minSize
    val maxSize: Dp get() = _maxSize

    fun startTracking(
        emoji: String,
        position: Offset,
        direction: FloatingEmojiDirection = FloatingEmojiDirection.UP,
        minSize: Dp = DefaultMinEmojiSize,
        maxSize: Dp = DefaultMaxEmojiSize,
    ) {
        _emoji = emoji
        _position = position
        _direction = direction
        _minSize = minSize
        _maxSize = maxSize
        _isTracking = true
    }

    /**
     * Updates particle size bounds without starting a tracking session.
     * Used by [EmojiSliderParticleSystem] for idle defaults; [startTracking] overwrites these
     * with the active slider's sizes.
     */
    fun updateParticleSizes(minSize: Dp, maxSize: Dp) {
        _minSize = minSize
        _maxSize = maxSize
    }

    fun updateProgress(progress: Float, position: Offset) {
        _progress = progress.limitToRange()
        _position = position
    }

    fun stopTracking() {
        _isTracking = false
    }
}
