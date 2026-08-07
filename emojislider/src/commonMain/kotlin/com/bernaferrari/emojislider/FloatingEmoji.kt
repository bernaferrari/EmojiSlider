package com.bernaferrari.emojislider

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import com.bernaferrari.emojislider.generated.resources.Res
import com.bernaferrari.emojislider.generated.resources.noto_emoji_regular
import org.jetbrains.compose.resources.Font
import kotlin.math.PI

/**
 * Canvas that draws the tracking emoji and released fly-away particles.
 */
@Composable
fun FloatingEmojiCanvas(
    modifier: Modifier = Modifier,
    isTracking: Boolean = false,
    emoji: String = DEFAULT_EMOJI,
    progress: Float = 0f,
    sliderPosition: Offset = Offset.Zero,
    direction: FloatingEmojiDirection = FloatingEmojiDirection.UP,
    minSize: Dp = DefaultMinEmojiSize,
    maxSize: Dp = DefaultMaxEmojiSize,
    onAnimationComplete: () -> Unit = {},
) {
    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()
    val emojiFontFamily = FontFamily(Font(Res.font.noto_emoji_regular))
    val minSizePx = with(density) { minSize.toPx() }
    val maxSizePx = with(density) { maxSize.toPx() }

    var particles by remember { mutableStateOf(listOf<FloatingEmojiParticle>()) }
    var frameTime by remember { mutableStateOf(0L) }

    val displayedSize by animateFloatAsState(
        targetValue = if (isTracking) trackingEmojiSizePx(minSizePx, maxSizePx, progress) else 0f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 300f),
        label = "emoji_size",
    )

    val infiniteTransition = rememberInfiniteTransition(label = "floating_emoji_breathing")
    val breathPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "floating_emoji_breathing_cycle",
    )
    val livePosition = sliderPosition.copy(y = sliderPosition.y + floatingEmojiBreathingOffsetY(breathPhase))
    val liveRotation = floatingEmojiBreathingRotation(breathPhase)

    LaunchedEffect(isTracking) {
        if (!isTracking && displayedSize > 0f) {
            particles = particles + spawnFlyAwayParticle(
                nowMs = frameTime,
                emoji = emoji,
                position = livePosition,
                displayedSize = displayedSize,
                progress = progress,
                minPx = minSizePx,
                maxPx = maxSizePx,
                direction = direction,
            )
            onAnimationComplete()
        }
    }

    LaunchedEffect(isTracking, particles.size) {
        while (isTracking || particles.isNotEmpty()) {
            frameTime = withFrameMillis { it }
            particles = pruneExpiredParticles(particles, frameTime)
        }
    }

    Canvas(modifier = modifier) {
        if (isTracking && displayedSize > 0f) {
            drawCenteredEmoji(
                emoji = emoji,
                center = livePosition,
                size = displayedSize,
                rotation = liveRotation,
                textMeasurer = textMeasurer,
                emojiFontFamily = emojiFontFamily,
            )
        }
        for (particle in particles) {
            val pose = floatingEmojiFlyAwayPose(particle, frameTime)
            if (!pose.isVisible) continue
            drawCenteredEmoji(
                emoji = particle.emoji,
                center = pose.position(particle.startPosition),
                size = pose.size,
                alpha = pose.alpha,
                rotation = pose.rotation,
                textMeasurer = textMeasurer,
                emojiFontFamily = emojiFontFamily,
            )
        }
    }
}

@Composable
fun FloatingEmojiCanvas(
    controller: FloatingEmojiController,
    modifier: Modifier = Modifier,
) {
    FloatingEmojiCanvas(
        modifier = modifier,
        isTracking = controller.isTracking,
        emoji = controller.emoji,
        progress = controller.progress,
        sliderPosition = controller.position,
        direction = controller.direction,
        minSize = controller.minSize,
        maxSize = controller.maxSize,
    )
}
