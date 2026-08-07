package com.bernaferrari.emojislider

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.util.lerp
import kotlin.math.PI
import kotlin.math.sin

internal const val FLY_AWAY_DURATION_MILLIS = 1500L
internal const val MIN_DRAWABLE_EMOJI_ALPHA = 0.02f

private val FlyAwayTravelEasing = CubicBezierEasing(0.25f, 0.46f, 0.45f, 0.94f)
private val FlyAwayShrinkEasing = CubicBezierEasing(0.33f, 0f, 0.2f, 1f)
private val FlyAwayFadeEasing = CubicBezierEasing(0.33f, 0f, 0.67f, 1f)

internal data class FloatingEmojiParticle(
    val id: Long,
    val emoji: String,
    val startPosition: Offset,
    val startSize: Float,
    val direction: FloatingEmojiDirection,
    val startTime: Long,
)

internal data class FloatingEmojiPose(
    val offsetX: Float,
    val offsetY: Float,
    val size: Float,
    val alpha: Float,
    val rotation: Float,
) {
    val isVisible: Boolean get() = alpha > MIN_DRAWABLE_EMOJI_ALPHA

    fun position(start: Offset): Offset = Offset(start.x + offsetX, start.y + offsetY)
}

internal fun trackingEmojiSizePx(minPx: Float, maxPx: Float, progress: Float): Float = minPx + progress.limitToRange() * (maxPx - minPx)

internal fun floatingEmojiBreathingOffsetY(phase: Float): Float = sin(phase) * 5f

internal fun floatingEmojiBreathingRotation(phase: Float): Float = sin(phase * 0.5f) * 3f

internal fun spawnFlyAwayParticle(
    nowMs: Long,
    emoji: String,
    position: Offset,
    displayedSize: Float,
    progress: Float,
    minPx: Float,
    maxPx: Float,
    direction: FloatingEmojiDirection,
): FloatingEmojiParticle {
    val targetSize = trackingEmojiSizePx(minPx, maxPx, progress)
    return FloatingEmojiParticle(
        id = nowMs,
        emoji = emoji,
        startPosition = position,
        startSize = maxOf(displayedSize, targetSize),
        direction = direction,
        startTime = nowMs,
    )
}

internal fun floatingEmojiFlyAwayPose(
    particle: FloatingEmojiParticle,
    nowMs: Long,
): FloatingEmojiPose {
    val duration = FLY_AWAY_DURATION_MILLIS.toFloat()
    val progress = ((nowMs - particle.startTime) / duration).coerceIn(0f, 1f)
    val travel = FlyAwayTravelEasing.transform(progress)
    val shrink = FlyAwayShrinkEasing.transform(progress)
    val fade = FlyAwayFadeEasing.transform(((progress - 0.68f) / 0.32f).coerceIn(0f, 1f))
    val drift = sin(progress * PI.toFloat() * 2.5f) * 25f * (1f - progress)

    return FloatingEmojiPose(
        offsetX = drift,
        offsetY = lerp(0f, floatingEmojiTravelTargetY(particle.direction), travel),
        size = lerp(particle.startSize, particle.startSize * 0.52f, shrink),
        alpha = lerp(1f, 0f, fade),
        rotation = lerp(0f, floatingEmojiTravelRotationDegrees(particle.direction), travel),
    )
}

internal fun pruneExpiredParticles(
    particles: List<FloatingEmojiParticle>,
    nowMs: Long,
): List<FloatingEmojiParticle> = particles.filter { nowMs - it.startTime < FLY_AWAY_DURATION_MILLIS }
