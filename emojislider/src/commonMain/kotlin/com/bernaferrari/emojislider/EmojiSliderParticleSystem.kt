package com.bernaferrari.emojislider

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Dp

/**
 * Floating Emoji System Provider. Put this around screen content when the emoji should fly outside
 * a clipped slider/card. Without it, [EmojiSlider] falls back to a local overlay.
 *
 * While a slider is tracking, particle sizes come from that slider's `minEmojiSize` / `maxEmojiSize`
 * (written onto the shared [FloatingEmojiController]). [minEmojiSize] / [maxEmojiSize] here are
 * idle defaults used before tracking starts.
 */
@Composable
fun EmojiSliderParticleSystem(
    modifier: Modifier = Modifier,
    minEmojiSize: Dp = DefaultMinEmojiSize,
    maxEmojiSize: Dp = DefaultMaxEmojiSize,
    content: @Composable () -> Unit,
) {
    val controller = rememberFloatingEmojiState()
    var coordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }

    SideEffect {
        if (!controller.isTracking) {
            controller.updateParticleSizes(minEmojiSize, maxEmojiSize)
        }
    }

    ProvideFloatingEmojiController(controller = controller, coordinates = coordinates) {
        Box(
            modifier = modifier.onGloballyPositioned {
                coordinates = it
            },
        ) {
            content()
            FloatingEmojiCanvas(
                modifier = Modifier.fillMaxSize(),
                isTracking = controller.isTracking,
                emoji = controller.emoji,
                progress = controller.progress,
                sliderPosition = controller.position,
                direction = controller.direction,
                minSize = controller.minSize,
                maxSize = controller.maxSize,
            )
        }
    }
}

val LocalFloatingEmojiController = compositionLocalOf<FloatingEmojiController?> { null }
val LocalFloatingEmojiCoordinates = compositionLocalOf<LayoutCoordinates?> { null }

@Composable
fun ProvideFloatingEmojiController(
    controller: FloatingEmojiController = rememberFloatingEmojiState(),
    coordinates: LayoutCoordinates? = null,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalFloatingEmojiController provides controller,
        LocalFloatingEmojiCoordinates provides coordinates,
    ) {
        content()
    }
}
