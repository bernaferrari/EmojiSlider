package com.bernaferrari.emojislider

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val DefaultEmoji = "😍"

/**
 * A Jetpack Compose EmojiSlider that provides the same functionality as the original View-based version.
 * Features floating emoji animations, customizable colors, and smooth spring animations.
 */
@Composable
fun EmojiSlider(
    modifier: Modifier = Modifier,
    emoji: String = DefaultEmoji,
    progress: Float = 0.25f,
    onProgressChange: (Float) -> Unit = {},
    onStartTracking: () -> Unit = {},
    onStopTracking: () -> Unit = {},

    // Appearance
    colorStart: Color = Color(0xFF6200EE),
    colorEnd: Color = Color(0xFFE91E63),
    colorTrack: Color = Color(0xFFE0E0E0),

    // Behavior
    isUserSeekable: Boolean = true,
    registerTouchOnTrack: Boolean = true,
    allowReselection: Boolean = false,

    // Floating emoji
    floatingEmojiDirection: FloatingEmojiDirection = FloatingEmojiDirection.UP,
    minEmojiSize: Dp = 24.dp,
    maxEmojiSize: Dp = 48.dp,
    sliderParticleSystem: (@Composable () -> Unit)? = null, // For external floating emoji system

    // Average/Result display
    averageProgressValue: Float = 0.5f,
    shouldDisplayAverage: Boolean = true,
    shouldDisplayResultPicture: Boolean = true,

    // Tooltip
    shouldDisplayTooltip: Boolean = true,
    tooltipText: String = "Average value",
    tooltipAutoDismissTimer: Long = 2500L,

    // Advanced behavior (matching original)
    thumbSizePercentWhenPressed: Float = 0.9f,
    resultBitmap: ImageBitmap? = null,

    // Sizes
    trackHeight: Dp = 4.dp,
    thumbSize: Dp = 56.dp,
    sliderHeight: Dp = 80.dp
) {
    val density = LocalDensity.current

    // State management - Enhanced to match original behavior
    var currentProgress by remember { mutableFloatStateOf(progress.coerceIn(0f, 1f)) }
    var isDragging by remember { mutableStateOf(false) }
    var isValueSelected by remember { mutableStateOf(false) }
    var sliderBounds by remember { mutableStateOf(IntOffset.Zero) }
    var sliderGlobalPosition by remember { mutableStateOf(IntOffset.Zero) }

    // Tooltip state
    val tooltipState = rememberTooltipState()

    // Floating emoji controller with global position tracking
    val floatingEmojiController = rememberFloatingEmojiState()

    // Sync internal progress with external prop
    LaunchedEffect(progress) {
        if (!isDragging) {
            currentProgress = progress.coerceIn(0f, 1f)
        }
    }

    // Reset when allowReselection changes - fix the logic
    LaunchedEffect(allowReselection) {
        if (allowReselection) {
            // When reselection is allowed, keep the slider interactive
            // Don't reset isValueSelected here as user might want to keep current state
        }
    }

    // Enhanced animations with spring characteristics matching Facebook Rebound
    val thumbScale by animateFloatAsState(
        targetValue = when {
            isValueSelected && !allowReselection -> 0f // Hide completely only when value is selected AND reselection not allowed
            isDragging -> thumbSizePercentWhenPressed
            else -> 1f
        },
        animationSpec = spring(
            dampingRatio = 0.6f,
            stiffness = 200f
        ),
        label = "thumb_scale"
    )

    val resultScale by animateFloatAsState(
        targetValue = if (isValueSelected && shouldDisplayResultPicture && !allowReselection) 1f else 0f,
        animationSpec = spring(
            dampingRatio = 0.4f,
            stiffness = 400f
        ),
        label = "result_scale"
    )

    // Enhanced average animation with proper spring behavior
    val averageScale by animateFloatAsState(
        targetValue = if (isValueSelected && shouldDisplayAverage && !allowReselection) 1f else 0f,
        animationSpec = spring(
            dampingRatio = 0.4f,
            stiffness = 400f
        ),
        label = "average_scale"
    )

    // Convert dp to px for calculations
    val trackHeightPx = with(density) { trackHeight.toPx() }
    val thumbSizePx = with(density) { thumbSize.toPx() }

    // Handle value selection completion (matching original behavior)
    fun valueSelectedAnimated() {
        if (allowReselection) {
            // When reselection is allowed, just mark as selected but keep interactive
            isValueSelected = true
            if (shouldDisplayAverage && shouldDisplayTooltip) {
                tooltipState.show()
            }
        } else {
            // When reselection is NOT allowed, lock the slider after selection
            isValueSelected = true
            if (shouldDisplayAverage && shouldDisplayTooltip) {
                tooltipState.show()
            }
        }
    }

    // Handle reset functionality
    fun resetAnimated() {
        isValueSelected = false
        tooltipState.hide()
    }

    // External floating emoji system (like original sliderParticleSystem)
    sliderParticleSystem?.invoke()

    // Main slider layout
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        // Tooltip positioned above slider with proper offset calculation
        if (shouldDisplayAverage && shouldDisplayTooltip) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-60).dp),
                contentAlignment = Alignment.CenterStart
            ) {
                val tooltipOffsetX = with(density) {
                    val sliderWidth = sliderBounds.x * 2
                    val trackStart = thumbSizePx / 2
                    val trackWidth = sliderWidth - thumbSizePx
                    val averagePosition = trackStart + averageProgressValue * trackWidth
                    (averagePosition - sliderWidth / 2).toDp()
                }

                EmojiSliderTooltip(
                    modifier = Modifier.offset(x = tooltipOffsetX),
                    visible = tooltipState.isVisible,
                    text = tooltipText,
                    autoDismissDelay = tooltipAutoDismissTimer,
                    onDismiss = { tooltipState.hide() }
                )
            }
        }

        // Main slider Canvas
        val textMeasurer = rememberTextMeasurer()

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(sliderHeight)
                .onGloballyPositioned { coordinates ->
                    sliderBounds = IntOffset(
                        coordinates.size.width / 2,
                        coordinates.size.height / 2
                    )

                    // Get global position for floating emoji coordination
                    val position = coordinates.localToWindow(Offset.Zero)
                    sliderGlobalPosition = IntOffset(position.x.toInt(), position.y.toInt())
                }
                .pointerInput(isUserSeekable, allowReselection, isValueSelected) {
                    // Fixed logic: Always allow interaction when isUserSeekable is true
                    // OR when allowReselection is true (even if value is selected)
                    if (!isUserSeekable && (!allowReselection || !isValueSelected)) return@pointerInput

                    detectDragGestures(
                        onDragStart = { offset ->
                            val trackStart = thumbSizePx / 2
                            val trackEnd = size.width - thumbSizePx / 2
                            val trackWidth = trackEnd - trackStart

                            val touchX = offset.x
                            val currentThumbX = trackStart + currentProgress * trackWidth
                            val thumbBounds =
                                (currentThumbX - thumbSizePx / 2)..(currentThumbX + thumbSizePx / 2)

                            // Fixed touch logic - simplified and more intuitive
                            val shouldStartDrag = when {
                                !isUserSeekable -> false // If not user seekable, no interaction allowed
                                isValueSelected && !allowReselection -> false // If locked (value selected + no reselection), no interaction
                                else -> {
                                    // Allow touch on thumb OR anywhere on track if registerTouchOnTrack is enabled
                                    touchX in thumbBounds || (registerTouchOnTrack && touchX in trackStart..trackEnd)
                                }
                            }

                            if (shouldStartDrag) {
                                isDragging = true

                                // Reset selection state when starting new interaction (if reselection allowed)
                                if (isValueSelected && allowReselection) {
                                    resetAnimated()
                                } else if (!isValueSelected) {
                                    tooltipState.hide()
                                }

                                onStartTracking()

                                // Calculate global position for floating emoji
                                val globalX = sliderGlobalPosition.x + touchX
                                val globalY = sliderGlobalPosition.y + size.height / 2f

                                floatingEmojiController.startTracking(
                                    emoji = emoji,
                                    position = Offset(globalX, globalY),
                                    direction = floatingEmojiDirection
                                )

                                // If touching on track (not thumb), jump to that position
                                if (touchX !in thumbBounds && registerTouchOnTrack) {
                                    val newProgress =
                                        ((touchX - trackStart) / trackWidth).coerceIn(0f, 1f)
                                    currentProgress = newProgress
                                    onProgressChange(newProgress)
                                }
                            }
                        },
                        onDrag = { change, _ ->
                            if (isDragging) {
                                val trackStart = thumbSizePx / 2
                                val trackEnd = size.width - thumbSizePx / 2
                                val trackWidth = trackEnd - trackStart

                                val newProgress =
                                    ((change.position.x - trackStart) / trackWidth).coerceIn(0f, 1f)
                                currentProgress = newProgress
                                onProgressChange(newProgress)

                                // Update floating emoji with global position
                                val globalX = sliderGlobalPosition.x + change.position.x
                                val globalY = sliderGlobalPosition.y + size.height / 2f

                                floatingEmojiController.updateProgress(
                                    progress = newProgress,
                                    position = Offset(globalX, globalY)
                                )
                            }
                        },
                        onDragEnd = {
                            if (isDragging) {
                                isDragging = false
                                onStopTracking()
                                floatingEmojiController.stopTracking()

                                // Trigger value selection animation
                                valueSelectedAnimated()
                            }
                        }
                    )
                }
        ) {
            drawSlider(
                progress = currentProgress,
                thumbScale = thumbScale,
                resultScale = resultScale,
                averageScale = averageScale,
                emoji = emoji,
                colorStart = colorStart,
                colorEnd = colorEnd,
                colorTrack = colorTrack,
                trackHeight = trackHeightPx,
                thumbSize = thumbSizePx,
                averageProgress = averageProgressValue,
                shouldDisplayAverage = shouldDisplayAverage,
                shouldDisplayResult = shouldDisplayResultPicture,
                isValueSelected = isValueSelected,
                resultBitmap = resultBitmap,
                textMeasurer = textMeasurer,
                isDragging = isDragging,
                allowReselection = allowReselection
            )
        }
    }
}

private fun DrawScope.drawSlider(
    progress: Float,
    thumbScale: Float,
    resultScale: Float,
    averageScale: Float,
    emoji: String,
    colorStart: Color,
    colorEnd: Color,
    colorTrack: Color,
    trackHeight: Float,
    thumbSize: Float,
    averageProgress: Float,
    shouldDisplayAverage: Boolean,
    shouldDisplayResult: Boolean,
    isValueSelected: Boolean,
    resultBitmap: ImageBitmap?,
    textMeasurer: TextMeasurer,
    isDragging: Boolean,
    allowReselection: Boolean
) {
    val trackStart = thumbSize / 2
    val trackEnd = size.width - thumbSize / 2
    val trackWidth = trackEnd - trackStart
    val trackCenterY = size.height / 2f
    val thumbX = trackStart + progress * trackWidth

    // Draw track background
    drawRoundRect(
        color = colorTrack,
        topLeft = Offset(trackStart, trackCenterY - trackHeight / 2),
        size = Size(trackWidth, trackHeight),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(trackHeight / 2)
    )

    // Draw progress track with improved gradient
    val progressWidth = progress * trackWidth
    if (progressWidth > 0) {
        val brush = Brush.horizontalGradient(
            colors = listOf(colorStart, colorEnd),
            startX = trackStart,
            endX = trackStart + progressWidth
        )

        drawRoundRect(
            brush = brush,
            topLeft = Offset(trackStart, trackCenterY - trackHeight / 2),
            size = Size(progressWidth, trackHeight),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(trackHeight / 2)
        )
    }

    // Draw average indicator (only show when value is selected and reselection is not allowed)
    if (shouldDisplayAverage && averageScale > 0f && isValueSelected && !allowReselection) {
        val averageX = trackStart + averageProgress * trackWidth
        val averageColor = lerp(colorStart, colorEnd, averageProgress)

        // Outer ring with proper scaling
        drawCircle(
            color = averageColor.copy(alpha = 0.3f),
            radius = 18.dp.toPx() * averageScale,
            center = Offset(averageX, trackCenterY)
        )
        // Inner circle
        drawCircle(
            color = averageColor,
            radius = 8.dp.toPx() * averageScale,
            center = Offset(averageX, trackCenterY)
        )
    }

    // Draw thumb or result based on state (enhanced to match original)
    when {
        // Show result when value is selected, result should be displayed, and reselection is not allowed
        isValueSelected && shouldDisplayResult && !allowReselection && resultScale > 0f -> {
            drawResultCircle(
                center = Offset(thumbX, trackCenterY),
                scale = resultScale,
                progress = progress,
                colorStart = colorStart,
                colorEnd = colorEnd,
                thumbSize = thumbSize,
                resultBitmap = resultBitmap
            )
        }
        // Show thumb in all other cases (with proper scaling)
        thumbScale > 0f -> {
            drawEmojiThumb(
                emoji = emoji,
                center = Offset(thumbX, trackCenterY),
                size = thumbSize * thumbScale,
                textMeasurer = textMeasurer,
                isDragging = isDragging
            )
        }
    }
}

private fun DrawScope.drawResultCircle(
    center: Offset,
    scale: Float,
    progress: Float,
    colorStart: Color,
    colorEnd: Color,
    thumbSize: Float,
    resultBitmap: ImageBitmap?
) {
    val resultColor = lerp(colorStart, colorEnd, progress)
    val radius = thumbSize / 2 * scale

    // Draw glow effect
    drawCircle(
        color = resultColor.copy(alpha = 0.3f),
        radius = radius * 1.3f,
        center = center
    )

    if (resultBitmap != null) {
        // Draw bitmap if provided - Fixed type conversions
        val bitmapSize = radius * 2
        drawImage(
            image = resultBitmap,
            dstOffset = IntOffset(
                x = (center.x - bitmapSize / 2).toInt(),
                y = (center.y - bitmapSize / 2).toInt()
            ),
            dstSize = androidx.compose.ui.unit.IntSize(
                width = bitmapSize.toInt(),
                height = bitmapSize.toInt()
            )
        )
    } else {
        // Draw colored circle
        drawCircle(
            color = resultColor,
            radius = radius,
            center = center
        )
    }
}

private fun DrawScope.drawEmojiThumb(
    emoji: String,
    center: Offset,
    size: Float,
    textMeasurer: TextMeasurer,
    isDragging: Boolean = false
) {
    // Draw background circle with subtle border
    val backgroundColor = if (isDragging) Color.White.copy(alpha = 0.95f) else Color.White
    val borderColor = Color.Black.copy(alpha = 0.1f)

    // Border
    drawCircle(
        color = borderColor,
        radius = size / 2 + 1.dp.toPx(),
        center = center
    )

    // Background
    drawCircle(
        color = backgroundColor,
        radius = size / 2,
        center = center
    )

    // Draw emoji with improved sizing
    val textLayoutResult = textMeasurer.measure(
        text = AnnotatedString(emoji),
        style = TextStyle(
            fontSize = (size * 0.65f / density).sp, // Slightly larger emoji
            fontWeight = FontWeight.Normal
        )
    )

    drawText(
        textLayoutResult = textLayoutResult,
        topLeft = Offset(
            x = center.x - textLayoutResult.size.width / 2f,
            y = center.y - textLayoutResult.size.height / 2f
        ),
        alpha = if (isDragging) 0.8f else 1f // Slightly fade when dragging since floating emoji takes over
    )
}


/**
 * Floating Emoji System Provider - matches original sliderParticleSystem concept
 * This should be placed at the root of your screen/activity to allow unrestricted emoji floating
 */
@Composable
fun EmojiSliderParticleSystem(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val floatingEmojiController = LocalFloatingEmojiController.current

    Box(modifier = modifier) {
        // Your regular content
        content()

        // Global floating emoji overlay that can escape any clipping
        FloatingEmojiCanvas(
            modifier = Modifier.fillMaxSize(),
            isTracking = floatingEmojiController?.isTracking ?: false,
            emoji = floatingEmojiController?.emoji ?: "😀",
            progress = floatingEmojiController?.progress ?: 0f,
            sliderPosition = floatingEmojiController?.position ?: Offset.Zero,
            direction = floatingEmojiController?.direction ?: FloatingEmojiDirection.UP,
            minSize = 24.dp,
            maxSize = 48.dp
        )
    }
}

/**
 * Composition Local for sharing floating emoji state across the app
 */
val LocalFloatingEmojiController = compositionLocalOf<FloatingEmojiController?> { null }

/**
 * Provider for the floating emoji system
 */
@Composable
fun ProvideFloatingEmojiController(
    controller: FloatingEmojiController = rememberFloatingEmojiState(),
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalFloatingEmojiController provides controller) {
        content()
    }
}

// Enhanced public API functions (matching original)
@Composable
fun rememberEmojiSliderState(
    initialProgress: Float = 0.25f
): EmojiSliderState {
    return remember { EmojiSliderState(initialProgress) }
}

class EmojiSliderState(initialProgress: Float) {
    var progress by mutableFloatStateOf(initialProgress.coerceIn(0f, 1f))
    var isValueSelected by mutableStateOf(false)
    var isUserSeekable by mutableStateOf(true)

    fun valueSelectedAnimated() {
        isValueSelected = true
        isUserSeekable = false
    }

    fun valueSelectedNow() {
        isValueSelected = true
        isUserSeekable = false
    }

    fun resetAnimated() {
        isValueSelected = false
        isUserSeekable = true
    }

    fun resetNow() {
        isValueSelected = false
        isUserSeekable = true
    }
}

@Composable
@Preview(showBackground = true)
private fun EmojiSliderPreview() {
    var progress by remember { mutableStateOf(0.3f) }

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Emoji Slider: ${(progress * 100).toInt()}%")

        EmojiSlider(
            progress = progress,
            onProgressChange = { progress = it },
            emoji = "😍"
        )

        EmojiSlider(
            progress = 0.7f,
            emoji = "🎉",
            colorStart = Color.Green,
            colorEnd = Color.Blue
        )
    }
}

