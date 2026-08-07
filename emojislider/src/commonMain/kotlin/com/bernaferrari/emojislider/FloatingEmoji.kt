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
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import com.bernaferrari.emojislider.generated.resources.Res
import com.bernaferrari.emojislider.generated.resources.noto_emoji_regular
import org.jetbrains.compose.resources.Font
import kotlin.math.PI
import kotlin.math.max
import kotlin.math.sin

private const val FLY_AWAY_DURATION_MILLIS = 1500L
private const val MIN_DRAWABLE_EMOJI_ALPHA = 0.02f

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

    var floatingEmojis by remember { mutableStateOf(listOf<FloatingEmojiState>()) }
    var frameTime by remember { mutableStateOf(0L) }

    val currentEmojiSize by animateFloatAsState(
        targetValue = if (isTracking) minSizePx + progress * (maxSizePx - minSizePx) else 0f,
        animationSpec = spring(
            dampingRatio = 0.7f,
            stiffness = 300f,
        ),
        label = "emoji_size",
    )

    val infiniteTransition = rememberInfiniteTransition(label = "floating_emoji_breathing")
    val breathingOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "floating_emoji_breathing_cycle",
    )
    val breathingValue = sin(breathingOffset) * 5f
    val breathingRotation = sin(breathingOffset * 0.5f) * 3f
    val liveEmojiPosition = sliderPosition.copy(y = sliderPosition.y + breathingValue)

    // Spawn a fly-away particle when tracking ends.
    LaunchedEffect(isTracking) {
        if (!isTracking && currentEmojiSize > 0) {
            val releaseTime = frameTime
            val targetEmojiSize = minSizePx + progress * (maxSizePx - minSizePx)
            val newFloatingEmoji = FloatingEmojiState(
                id = releaseTime,
                emoji = emoji,
                startPosition = liveEmojiPosition,
                startSize = max(currentEmojiSize, targetEmojiSize),
                direction = direction,
                startTime = releaseTime,
            )
            floatingEmojis = floatingEmojis + newFloatingEmoji
            onAnimationComplete()
        }
    }

    LaunchedEffect(isTracking, floatingEmojis.size) {
        while (isTracking || floatingEmojis.isNotEmpty()) {
            frameTime = withFrameMillis { it }
            floatingEmojis = floatingEmojis.filter { frameTime - it.startTime < FLY_AWAY_DURATION_MILLIS }
        }
    }

    Canvas(
        modifier = modifier
            .graphicsLayer {
                compositingStrategy = CompositingStrategy.Offscreen
                clip = false
            },
    ) {
        if (isTracking && currentEmojiSize > 0) {
            drawEmoji(
                emoji = emoji,
                position = liveEmojiPosition,
                size = currentEmojiSize,
                alpha = 1f,
                rotation = breathingRotation,
                textMeasurer = textMeasurer,
                emojiFontFamily = emojiFontFamily,
            )
        }

        floatingEmojis.forEach { floatingEmoji ->
            val animatedValues = getFloatingEmojiAnimatedValues(floatingEmoji, frameTime)

            if (animatedValues.isVisible) {
                drawEmoji(
                    emoji = floatingEmoji.emoji,
                    position = Offset(
                        x = floatingEmoji.startPosition.x + animatedValues.offsetX,
                        y = floatingEmoji.startPosition.y + animatedValues.offsetY,
                    ),
                    size = animatedValues.size,
                    alpha = animatedValues.alpha,
                    rotation = animatedValues.rotation,
                    textMeasurer = textMeasurer,
                    emojiFontFamily = emojiFontFamily,
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
    val rotation: Float,
) {
    val isVisible: Boolean
        get() = alpha > MIN_DRAWABLE_EMOJI_ALPHA
}

private fun getFloatingEmojiAnimatedValues(
    floatingEmoji: FloatingEmojiState,
    frameTime: Long,
): FloatingEmojiAnimatedValues {
    val animationDuration = FLY_AWAY_DURATION_MILLIS.toFloat()
    val currentTime = frameTime - floatingEmoji.startTime
    val progress = (currentTime / animationDuration).coerceIn(0f, 1f)

    val targetY = floatingEmojiTravelTargetY(floatingEmoji.direction)

    val travelProgress = CubicBezierEasing(0.25f, 0.46f, 0.45f, 0.94f).transform(progress)
    val shrinkProgress = CubicBezierEasing(0.33f, 0f, 0.2f, 1f).transform(progress)
    val fadeProgress = ((progress - 0.68f) / 0.32f).coerceIn(0f, 1f)
    val easedFadeProgress = CubicBezierEasing(0.33f, 0f, 0.67f, 1f).transform(fadeProgress)

    val horizontalDrift = sin(progress * PI.toFloat() * 2.5f) * 25f * (1f - progress)

    val rotationMultiplier = floatingEmojiTravelRotationDegrees(floatingEmoji.direction)

    return FloatingEmojiAnimatedValues(
        offsetX = horizontalDrift,
        offsetY = lerp(0f, targetY, travelProgress),
        size = lerp(floatingEmoji.startSize, floatingEmoji.startSize * 0.52f, shrinkProgress),
        alpha = lerp(1f, 0f, easedFadeProgress),
        rotation = lerp(0f, rotationMultiplier, travelProgress),
    )
}

private fun DrawScope.drawEmoji(
    emoji: String,
    position: Offset,
    size: Float,
    alpha: Float = 1f,
    rotation: Float = 0f,
    textMeasurer: TextMeasurer,
    emojiFontFamily: FontFamily,
) {
    val textLayoutResult = textMeasurer.measure(
        text = AnnotatedString(emoji),
        style = TextStyle(
            fontSize = (size * 0.8f / density).sp,
            fontFamily = emojiFontFamily,
            fontWeight = FontWeight.Normal,
        ),
    )

    val topLeft = Offset(
        x = position.x - textLayoutResult.size.width / 2f,
        y = position.y - textLayoutResult.size.height / 2f,
    )
    val layerBounds = Rect(
        left = topLeft.x,
        top = topLeft.y,
        right = topLeft.x + textLayoutResult.size.width,
        bottom = topLeft.y + textLayoutResult.size.height,
    )
    val layerPaint = Paint().apply { this.alpha = alpha.coerceIn(0f, 1f) }

    fun DrawScope.drawMeasuredEmoji() {
        drawText(
            textLayoutResult = textLayoutResult,
            topLeft = topLeft,
            alpha = 1f,
        )
    }

    drawContext.canvas.saveLayer(layerBounds, layerPaint)
    if (rotation != 0f) {
        rotate(rotation, pivot = position) {
            drawMeasuredEmoji()
        }
    } else {
        drawMeasuredEmoji()
    }
    drawContext.canvas.restore()
}

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

private data class FloatingEmojiState(
    val id: Long,
    val emoji: String,
    val startPosition: Offset,
    val startSize: Float,
    val direction: FloatingEmojiDirection,
    val startTime: Long,
)

/**
 * Remembers a [FloatingEmojiController] for driving [FloatingEmojiCanvas].
 */
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
