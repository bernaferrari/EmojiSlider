package com.bernaferrari.emojislider

import androidx.compose.animation.core.CubicBezierEasing
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.sin

/**
 * Modern Floating Emoji Canvas with improved performance and animations
 */
@Composable
fun FloatingEmojiCanvas(
    modifier: Modifier = Modifier,
    isTracking: Boolean = false,
    emoji: String = "😀",
    progress: Float = 0f,
    sliderPosition: Offset = Offset.Zero,
    direction: FloatingEmojiDirection = FloatingEmojiDirection.UP,
    minSize: Dp = 24.dp,
    maxSize: Dp = 48.dp,
    onAnimationComplete: () -> Unit = {}
) {
    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()

    // Convert dp to px for calculations
    val minSizePx = with(density) { minSize.toPx() }
    val maxSizePx = with(density) { maxSize.toPx() }

    // State for floating emojis - enhanced for better performance
    var floatingEmojis by remember { mutableStateOf(listOf<FloatingEmojiState>()) }

    // Size animation with improved responsiveness (matching original spring behavior)
    val currentEmojiSize by animateFloatAsState(
        targetValue = if (isTracking) minSizePx + progress * (maxSizePx - minSizePx) else 0f,
        animationSpec = spring(
            dampingRatio = 0.7f,
            stiffness = 300f
        ),
        label = "emoji_size"
    )

    // Enhanced breathing animation (matching original behavior)
    val infiniteTransition = rememberInfiniteTransition(label = "breathing")
    val breathingOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "breathing_cycle"
    )
    val breathingValue = if (isTracking) sin(breathingOffset) * 5f else 0f
    val breathingRotation = if (isTracking) sin(breathingOffset * 0.5f) * 3f else 0f

    // Handle emoji release (enhanced to match original onStopTrackingTouch behavior)
    LaunchedEffect(isTracking) {
        if (!isTracking && currentEmojiSize > 0) {
            val newFloatingEmoji = FloatingEmojiState(
                id = System.currentTimeMillis(),
                emoji = emoji,
                startPosition = sliderPosition,
                startSize = currentEmojiSize,
                direction = direction,
                startTime = System.currentTimeMillis()
            )
            floatingEmojis = floatingEmojis + newFloatingEmoji
            onAnimationComplete()
        }
    }

    // Clean up completed animations (optimized timing)
    LaunchedEffect(floatingEmojis.size) {
        if (floatingEmojis.isNotEmpty()) {
            delay(2200) // Slightly longer to ensure all animations complete
            floatingEmojis = floatingEmojis.filter {
                System.currentTimeMillis() - it.startTime < 2000
            }
        }
    }

    Canvas(
        modifier = modifier
            .graphicsLayer {
                compositingStrategy = CompositingStrategy.Offscreen
                clip = false // Critical: no clipping at all
                alpha = 0.99f // Ensure proper compositing without affecting visibility
            }
    ) {
        // Draw current tracking emoji with enhanced breathing effect
        if (isTracking && currentEmojiSize > 0) {
            drawEmoji(
                emoji = emoji,
                position = sliderPosition.copy(
                    y = sliderPosition.y + breathingValue
                ),
                size = currentEmojiSize,
                alpha = 0.98f, // Slightly transparent to show it's floating
                rotation = breathingRotation,
                textMeasurer = textMeasurer
            )
        }

        // Draw floating emojis with enhanced animation
        floatingEmojis.forEach { floatingEmoji ->
            val animatedValues = getFloatingEmojiAnimatedValues(floatingEmoji)

            if (animatedValues.alpha > 0.01f) {
                drawEmoji(
                    emoji = floatingEmoji.emoji,
                    position = Offset(
                        x = floatingEmoji.startPosition.x + animatedValues.offsetX,
                        y = floatingEmoji.startPosition.y + animatedValues.offsetY
                    ),
                    size = animatedValues.size,
                    alpha = animatedValues.alpha,
                    rotation = animatedValues.rotation,
                    textMeasurer = textMeasurer
                )
            }
        }
    }
}

private data class FloatingEmojiAnimatedValues(
    val offsetX: Float,
    val offsetY: Float,
    val size: Float,
    val alpha: Float,
    val rotation: Float
)

private fun getFloatingEmojiAnimatedValues(
    floatingEmoji: FloatingEmojiState
): FloatingEmojiAnimatedValues {
    val animationDuration = 1800f // Increased for better visibility
    val currentTime = System.currentTimeMillis() - floatingEmoji.startTime
    val progress = (currentTime / animationDuration).coerceIn(0f, 1f)

    val targetY = when (floatingEmoji.direction) {
        FloatingEmojiDirection.UP -> -600f // Increased distance for dramatic effect
        FloatingEmojiDirection.DOWN -> 600f
    }

    // Enhanced easing curve for more natural floating motion
    val easedProgress = CubicBezierEasing(0.25f, 0.46f, 0.45f, 0.94f).transform(progress)

    // More pronounced horizontal drift for lifelike movement
    val horizontalDrift = sin(progress * PI.toFloat() * 2.5f) * 25f * (1f - progress)

    // Enhanced rotation for more dynamic movement
    val rotationMultiplier = when (floatingEmoji.direction) {
        FloatingEmojiDirection.UP -> -35f
        FloatingEmojiDirection.DOWN -> 35f
    }

    return FloatingEmojiAnimatedValues(
        offsetX = horizontalDrift,
        offsetY = lerp(0f, targetY, easedProgress),
        size = lerp(floatingEmoji.startSize, floatingEmoji.startSize * 0.4f, easedProgress),
        alpha = lerp(1f, 0f, (progress * 1.05f).coerceAtMost(1f)),
        rotation = lerp(0f, rotationMultiplier, easedProgress)
    )
}

private fun DrawScope.drawEmoji(
    emoji: String,
    position: Offset,
    size: Float,
    alpha: Float = 1f,
    rotation: Float = 0f,
    textMeasurer: TextMeasurer
) {
    val textLayoutResult = textMeasurer.measure(
        text = AnnotatedString(emoji),
        style = TextStyle(
            fontSize = (size * 0.8f / density).sp,
            fontWeight = FontWeight.Normal
        )
    )

    // Apply rotation if needed
    if (rotation != 0f) {
        rotate(rotation, pivot = position) {
            drawText(
                textLayoutResult = textLayoutResult,
                topLeft = Offset(
                    x = position.x - textLayoutResult.size.width / 2f,
                    y = position.y - textLayoutResult.size.height / 2f
                ),
                alpha = alpha
            )
        }
    } else {
        drawText(
            textLayoutResult = textLayoutResult,
            topLeft = Offset(
                x = position.x - textLayoutResult.size.width / 2f,
                y = position.y - textLayoutResult.size.height / 2f
            ),
            alpha = alpha
        )
    }
}

/**
 * Direction for floating emoji animation
 */
enum class FloatingEmojiDirection {
    UP, DOWN
}

/**
 * Enhanced state class with unique ID for better tracking
 */
private data class FloatingEmojiState(
    val id: Long,
    val emoji: String,
    val startPosition: Offset,
    val startSize: Float,
    val direction: FloatingEmojiDirection,
    val startTime: Long
)

/**
 * Hook for using FloatingEmoji in your slider composable
 */
@Composable
fun rememberFloatingEmojiState(): FloatingEmojiController {
    return remember { FloatingEmojiController() }
}

/**
 * Controller class for managing floating emoji state
 */
class FloatingEmojiController {
    private var _isTracking by mutableStateOf(false)
    private var _progress by mutableStateOf(0f)
    private var _position by mutableStateOf(Offset.Zero)
    private var _emoji by mutableStateOf("😀")
    private var _direction by mutableStateOf(FloatingEmojiDirection.UP)

    val isTracking: Boolean get() = _isTracking
    val progress: Float get() = _progress
    val position: Offset get() = _position
    val emoji: String get() = _emoji
    val direction: FloatingEmojiDirection get() = _direction

    fun startTracking(
        emoji: String,
        position: Offset,
        direction: FloatingEmojiDirection = FloatingEmojiDirection.UP
    ) {
        _emoji = emoji
        _position = position
        _direction = direction
        _isTracking = true
    }

    fun updateProgress(progress: Float, position: Offset) {
        _progress = progress.coerceIn(0f, 1f)
        _position = position
    }

    fun stopTracking() {
        _isTracking = false
    }
}
