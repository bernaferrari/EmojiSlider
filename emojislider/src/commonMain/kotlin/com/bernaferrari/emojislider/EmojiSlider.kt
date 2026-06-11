package com.bernaferrari.emojislider

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.setProgress
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bernaferrari.emojislider.generated.resources.Res
import com.bernaferrari.emojislider.generated.resources.noto_emoji_regular
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.Font
import kotlin.math.roundToInt

private const val DEFAULT_PROGRESS = 0.25f
private const val DEFAULT_AVERAGE_PROGRESS = 0.5f
private const val DEFAULT_TOOLTIP_DISMISS_MILLIS = 2500L
private const val DEFAULT_THUMB_PRESSED_SCALE = 0.9f
private const val TAP_RELEASE_PARTICLE_DELAY_MILLIS = 90L

private const val DEFAULT_EMOJI = "😍"

/**
 * Compose EmojiSlider inspired by the original View implementation.
 *
 * The slider keeps the original behavioral contract: optional whole-track touch target, optional
 * one-shot selection, average/result reveal after release, and a floating emoji while tracking.
 */
@Composable
fun EmojiSlider(
    modifier: Modifier = Modifier,
    emoji: String = DEFAULT_EMOJI,
    progress: Float = DEFAULT_PROGRESS,
    value: Float = progress,
    onProgressChange: (Float) -> Unit = {},
    onValueChange: (Float) -> Unit = onProgressChange,
    onStartTracking: () -> Unit = {},
    onStopTracking: () -> Unit = {},

    // Appearance
    colorStart: Color = Color(0xFF6200EE),
    colorEnd: Color = Color(0xFFE91E63),
    colorTrack: Color = Color(0xFFE0E0E0),
    activeTrackGradient: Brush = Brush.horizontalGradient(listOf(colorStart, colorEnd)),

    // Behavior
    isUserSeekable: Boolean = true,
    registerTouchOnTrack: Boolean = true,
    allowReselection: Boolean = false,

    // Floating emoji
    floatingEmojiDirection: FloatingEmojiDirection = FloatingEmojiDirection.UP,
    floatingDirection: FloatingEmojiDirection = floatingEmojiDirection,
    minEmojiSize: Dp = 24.dp,
    maxEmojiSize: Dp = 48.dp,
    sliderParticleSystem: (@Composable () -> Unit)? = null,

    // Average/Result display
    averageProgressValue: Float = DEFAULT_AVERAGE_PROGRESS,
    shouldDisplayAverage: Boolean = true,
    shouldDisplayResultPicture: Boolean = true,

    // Tooltip
    shouldDisplayTooltip: Boolean = true,
    tooltipText: String = "Average value",
    tooltipAutoDismissTimer: Long = DEFAULT_TOOLTIP_DISMISS_MILLIS,

    // Advanced behavior
    thumbSizePercentWhenPressed: Float = DEFAULT_THUMB_PRESSED_SCALE,
    resultBitmap: ImageBitmap? = null,

    // Sizes
    trackHeight: Dp = 16.dp,
    thumbSize: Dp = 56.dp,
    sliderHeight: Dp = 80.dp,
    /**
     * How far the track is inset from each horizontal edge. Defaults to half the thumb so the emoji
     * never clips. Pass [0.dp] for an edge-to-edge track; the thumb is then clamped so it stays in view.
     */
    trackInset: Dp = thumbSize / 2,
) {
    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()
    val emojiFontFamily = FontFamily(Font(Res.font.noto_emoji_regular))
    val haptics = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()

    val ambientFloatingController = LocalFloatingEmojiController.current
    val ambientFloatingCoordinates = LocalFloatingEmojiCoordinates.current
    val localFloatingController = rememberFloatingEmojiState()
    val floatingController = ambientFloatingController ?: localFloatingController
    val tooltipState = rememberTooltipState()
    val currentGestureEmoji by rememberUpdatedState(emoji)
    val currentGestureDirection by rememberUpdatedState(floatingDirection)
    val currentOnValueChange by rememberUpdatedState(onValueChange)
    val currentOnStartTracking by rememberUpdatedState(onStartTracking)
    val currentOnStopTracking by rememberUpdatedState(onStopTracking)

    var currentProgress by remember { mutableFloatStateOf(value.limitToRange()) }
    var isDragging by remember { mutableStateOf(false) }
    var isValueSelected by remember { mutableStateOf(false) }
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }
    var canvasCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }

    val trackHeightPx = with(density) { trackHeight.toPx() }
    val thumbSizePx = with(density) { thumbSize.toPx() }
    val sliderHeightPx = with(density) { sliderHeight.toPx() }
    val trackInsetPx = with(density) { trackInset.toPx() }
    val canInteract = isUserSeekable && (!isValueSelected || allowReselection)
    val averageProgress = averageProgressValue.limitToRange()

    LaunchedEffect(value) {
        if (!isDragging) {
            currentProgress = value.limitToRange()
        }
    }

    LaunchedEffect(allowReselection) {
        if (allowReselection && isValueSelected) {
            isValueSelected = false
            tooltipState.hide()
        }
    }

    val thumbScale by animateFloatAsState(
        targetValue = when {
            isValueSelected && !allowReselection -> 0f
            isDragging -> thumbSizePercentWhenPressed.limitToRange()
            else -> 1f
        },
        animationSpec = spring(
            dampingRatio = 0.72f,
            stiffness = Spring.StiffnessMediumLow,
        ),
        label = "emoji_thumb_scale",
    )

    val resultScale by animateFloatAsState(
        targetValue = if (isValueSelected && shouldDisplayResultPicture && !allowReselection) 1f else 0f,
        animationSpec = spring(
            dampingRatio = 0.62f,
            stiffness = Spring.StiffnessLow,
        ),
        label = "result_scale",
    )

    val averageScale by animateFloatAsState(
        targetValue = if (isValueSelected && shouldDisplayAverage && !allowReselection) 1f else 0f,
        animationSpec = spring(
            dampingRatio = 0.62f,
            stiffness = Spring.StiffnessLow,
        ),
        label = "average_scale",
    )

    fun geometry(width: Float): SliderGeometry {
        val trackStart = trackInsetPx
        val trackEnd = (width - trackInsetPx).coerceAtLeast(trackStart)
        return SliderGeometry(
            trackStart = trackStart,
            trackEnd = trackEnd,
            centerY = sliderHeightPx / 2f,
            trackHeight = trackHeightPx,
            thumbSize = thumbSizePx,
            width = width,
        )
    }

    fun updateProgressFromTouch(x: Float, width: Float) {
        val newValue = geometry(width).progressFor(x)
        currentProgress = newValue
        currentOnValueChange(newValue)
    }

    fun particlePosition(localPosition: Offset): Offset {
        if (ambientFloatingController == null) return localPosition

        val sliderCoordinates = canvasCoordinates ?: return localPosition
        val floatingCoordinates = ambientFloatingCoordinates ?: return localPosition
        if (!sliderCoordinates.isAttached || !floatingCoordinates.isAttached) return localPosition

        return floatingCoordinates.localPositionOf(sliderCoordinates, localPosition)
    }

    fun hoverEmojiCenter(sliderGeometry: SliderGeometry, progress: Float): Offset {
        val thumbCenter = sliderGeometry.thumbCenter(progress)
        return Offset(
            x = thumbCenter.x,
            y = thumbCenter.y - sliderGeometry.thumbSize * 0.82f,
        )
    }

    fun startTrackingAtCurrentProgress(sliderGeometry: SliderGeometry) {
        val hoverPosition = particlePosition(hoverEmojiCenter(sliderGeometry, currentProgress))
        floatingController.startTracking(
            emoji = currentGestureEmoji,
            position = hoverPosition,
            direction = currentGestureDirection,
        )
        floatingController.updateProgress(
            progress = currentProgress,
            position = hoverPosition,
        )
    }

    fun stopDragging(selectValue: Boolean) {
        if (!isDragging) return

        if (canvasSize.width > 0) {
            val sliderGeometry = geometry(canvasSize.width.toFloat())
            floatingController.updateProgress(
                progress = currentProgress,
                position = particlePosition(hoverEmojiCenter(sliderGeometry, currentProgress)),
            )
        }

        isDragging = false
        floatingController.stopTracking()
        currentOnStopTracking()

        if (selectValue && !allowReselection) {
            isValueSelected = true
            if (shouldDisplayAverage && shouldDisplayTooltip) {
                tooltipState.show()
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(sliderHeight)
            .semantics {
                progressBarRangeInfo = ProgressBarRangeInfo(currentProgress, 0f..1f)
                setProgress { targetValue ->
                    if (!canInteract) return@setProgress false
                    val newValue = targetValue.limitToRange()
                    currentProgress = newValue
                    currentOnValueChange(newValue)
                    true
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        val canvasModifier = Modifier
            .fillMaxWidth()
            .height(sliderHeight)
            .onGloballyPositioned { coordinates ->
                canvasSize = coordinates.size
                canvasCoordinates = coordinates
            }
            .pointerInput(canInteract, registerTouchOnTrack, allowReselection, thumbSizePx, emoji, floatingDirection) {
                detectTapGestures(
                    onTap = { offset ->
                        if (!canInteract || canvasSize.width == 0) return@detectTapGestures

                        val sliderGeometry = geometry(canvasSize.width.toFloat())
                        val thumbCenter = sliderGeometry.thumbCenter(currentProgress)
                        val hitThumb = offset.distanceSquaredTo(thumbCenter) <= (thumbSizePx * thumbSizePx)
                        val hitTrack = registerTouchOnTrack && offset.x in sliderGeometry.trackStart..sliderGeometry.trackEnd

                        if (!hitThumb && !hitTrack) return@detectTapGestures

                        tooltipState.hide()
                        haptics.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
                        updateProgressFromTouch(offset.x, canvasSize.width.toFloat())
                        isDragging = true
                        startTrackingAtCurrentProgress(geometry(canvasSize.width.toFloat()))
                        currentOnStartTracking()

                        coroutineScope.launch {
                            delay(TAP_RELEASE_PARTICLE_DELAY_MILLIS)
                            stopDragging(selectValue = true)
                        }
                    },
                )
            }
            .pointerInput(canInteract, registerTouchOnTrack, thumbSizePx, emoji, floatingDirection) {
                var dragX = 0f
                detectHorizontalDragGestures(
                    onDragStart = { offset ->
                        if (!canInteract || canvasSize.width == 0) return@detectHorizontalDragGestures

                        val sliderGeometry = geometry(canvasSize.width.toFloat())
                        val thumbCenter = sliderGeometry.thumbCenter(currentProgress)
                        val hitThumb = offset.distanceSquaredTo(thumbCenter) <= (thumbSizePx * thumbSizePx)
                        val hitTrack = registerTouchOnTrack && offset.x in sliderGeometry.trackStart..sliderGeometry.trackEnd

                        if (!hitThumb && !hitTrack) return@detectHorizontalDragGestures

                        isDragging = true
                        tooltipState.hide()
                        haptics.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
                        dragX = offset.x
                        updateProgressFromTouch(offset.x, canvasSize.width.toFloat())

                        val sliderGeometryAfterJump = geometry(canvasSize.width.toFloat())
                        startTrackingAtCurrentProgress(sliderGeometryAfterJump)
                        currentOnStartTracking()
                    },
                    onHorizontalDrag = { change, dragAmount ->
                        if (!isDragging || canvasSize.width == 0) return@detectHorizontalDragGestures

                        change.consume()
                        dragX += dragAmount
                        updateProgressFromTouch(dragX, canvasSize.width.toFloat())
                        val sliderGeometry = geometry(canvasSize.width.toFloat())
                        floatingController.updateProgress(
                            progress = currentProgress,
                            position = particlePosition(hoverEmojiCenter(sliderGeometry, currentProgress)),
                        )
                    },
                    onDragCancel = {
                        stopDragging(selectValue = false)
                    },
                    onDragEnd = {
                        stopDragging(selectValue = true)
                    },
                )
            }

        Box(modifier = Modifier.fillMaxSize()) {
            Canvas(modifier = canvasModifier) {
                drawEmojiSlider(
                    progress = currentProgress,
                    geometry = geometry(size.width),
                    emoji = emoji,
                    activeTrackGradient = activeTrackGradient,
                    colorStart = colorStart,
                    colorEnd = colorEnd,
                    colorTrack = colorTrack,
                    averageProgress = averageProgress,
                    shouldDisplayAverage = shouldDisplayAverage,
                    shouldDisplayResult = shouldDisplayResultPicture,
                    averageScale = averageScale,
                    resultScale = resultScale,
                    thumbScale = thumbScale,
                    resultBitmap = resultBitmap,
                    textMeasurer = textMeasurer,
                    emojiFontFamily = emojiFontFamily,
                    isDragging = isDragging,
                    allowReselection = allowReselection,
                )
            }

            if (ambientFloatingController == null) {
                FloatingEmojiCanvas(
                    modifier = Modifier
                        .matchParentSize(),
                    isTracking = localFloatingController.isTracking,
                    emoji = localFloatingController.emoji,
                    progress = localFloatingController.progress,
                    sliderPosition = localFloatingController.position,
                    direction = localFloatingController.direction,
                    minSize = minEmojiSize,
                    maxSize = maxEmojiSize,
                )
            }
        }

        sliderParticleSystem?.invoke()

        val averageAnchorX = if (canvasSize.width > 0) {
            geometry(canvasSize.width.toFloat()).thumbCenter(averageProgress).x
        } else {
            0f
        }

        AnimatedVisibility(
            visible = tooltipState.isVisible && shouldDisplayAverage && shouldDisplayTooltip,
            modifier = Modifier.align(Alignment.TopStart),
            enter = fadeIn(animationSpec = tween(durationMillis = 220)),
            exit = fadeOut(animationSpec = tween(durationMillis = 260)),
        ) {
            AverageTooltip(
                text = tooltipText,
                anchorX = averageAnchorX,
                autoDismissDelay = tooltipAutoDismissTimer,
                onDismiss = tooltipState::hide,
            )
        }
    }
}

private data class SliderGeometry(
    val trackStart: Float,
    val trackEnd: Float,
    val centerY: Float,
    val trackHeight: Float,
    val thumbSize: Float,
    val width: Float,
) {
    val trackWidth: Float = (trackEnd - trackStart).coerceAtLeast(1f)

    fun progressFor(x: Float): Float = ((x - trackStart) / trackWidth).limitToRange()

    /** Thumb centre travels along the track but stays clamped so the emoji never clips off-screen. */
    fun thumbCenter(progress: Float): Offset {
        val raw = trackStart + progress.limitToRange() * trackWidth
        val half = thumbSize / 2f
        return Offset(
            x = raw.coerceIn(half, (width - half).coerceAtLeast(half)),
            y = centerY,
        )
    }
}

private fun DrawScope.drawEmojiSlider(
    progress: Float,
    geometry: SliderGeometry,
    emoji: String,
    activeTrackGradient: Brush,
    colorStart: Color,
    colorEnd: Color,
    colorTrack: Color,
    averageProgress: Float,
    shouldDisplayAverage: Boolean,
    shouldDisplayResult: Boolean,
    averageScale: Float,
    resultScale: Float,
    thumbScale: Float,
    resultBitmap: ImageBitmap?,
    textMeasurer: TextMeasurer,
    emojiFontFamily: FontFamily,
    isDragging: Boolean,
    allowReselection: Boolean,
) {
    val trackTopLeft = Offset(
        x = geometry.trackStart,
        y = geometry.centerY - geometry.trackHeight / 2f,
    )

    drawRoundRect(
        color = colorTrack,
        topLeft = trackTopLeft,
        size = Size(geometry.trackWidth, geometry.trackHeight),
        cornerRadius = CornerRadius(geometry.trackHeight / 2f),
    )

    val progressWidth = progress.limitToRange() * geometry.trackWidth
    if (progressWidth > 0f) {
        drawRoundRect(
            brush = activeTrackGradient,
            topLeft = trackTopLeft,
            size = Size(progressWidth, geometry.trackHeight),
            cornerRadius = CornerRadius(geometry.trackHeight / 2f),
        )
    }

    val thumbCenter = geometry.thumbCenter(progress)
    if (shouldDisplayResult && resultScale > 0f && !allowReselection) {
        drawResult(
            center = thumbCenter,
            radius = geometry.thumbSize / 2f * resultScale,
            progress = progress,
            colorStart = colorStart,
            colorEnd = colorEnd,
            resultBitmap = resultBitmap,
        )
    }

    if (shouldDisplayAverage && averageScale > 0f && !allowReselection) {
        val averageCenter = geometry.thumbCenter(averageProgress)
        drawAverageIndicator(
            center = averageCenter,
            outerColor = lerp(colorStart, colorEnd, averageProgress),
            scale = averageScale,
        )
    }

    if (thumbScale > 0f) {
        drawEmojiThumb(
            emoji = emoji,
            center = thumbCenter,
            size = geometry.thumbSize * thumbScale,
            textMeasurer = textMeasurer,
            emojiFontFamily = emojiFontFamily,
            alpha = if (isDragging) 0.82f else 1f,
        )
    }
}

private fun DrawScope.drawResult(
    center: Offset,
    radius: Float,
    progress: Float,
    colorStart: Color,
    colorEnd: Color,
    resultBitmap: ImageBitmap?,
) {
    val resultColor = lerp(colorStart, colorEnd, progress.limitToRange())

    drawCircle(
        color = resultColor.copy(alpha = 0.20f),
        radius = radius * 1.34f,
        center = center,
    )

    if (resultBitmap == null) {
        drawCircle(
            color = resultColor,
            radius = radius,
            center = center,
        )
        return
    }

    val clip = Path().apply {
        addOval(
            androidx.compose.ui.geometry.Rect(
                left = center.x - radius,
                top = center.y - radius,
                right = center.x + radius,
                bottom = center.y + radius,
            ),
        )
    }

    clipPath(clip) {
        drawImage(
            image = resultBitmap,
            dstOffset = IntOffset((center.x - radius).roundToInt(), (center.y - radius).roundToInt()),
            dstSize = IntSize((radius * 2f).roundToInt(), (radius * 2f).roundToInt()),
        )
    }
}

private fun DrawScope.drawAverageIndicator(
    center: Offset,
    outerColor: Color,
    scale: Float,
) {
    val radius = 10.dp.toPx() * scale
    val ringThickness = 2.dp.toPx() * scale
    if (radius <= 0f) return

    drawCircle(
        color = outerColor,
        radius = radius,
        center = center,
    )
    drawCircle(
        color = Color.White,
        radius = (radius - ringThickness).coerceAtLeast(0f),
        center = center,
    )
}

private fun DrawScope.drawEmojiThumb(
    emoji: String,
    center: Offset,
    size: Float,
    textMeasurer: TextMeasurer,
    emojiFontFamily: FontFamily,
    alpha: Float,
) {
    val textLayout = textMeasurer.measure(
        text = AnnotatedString(emoji),
        style = TextStyle(
            fontSize = (size * 0.86f / density).sp,
            fontFamily = emojiFontFamily,
            fontWeight = FontWeight.Normal,
        ),
    )

    drawText(
        textLayoutResult = textLayout,
        topLeft = Offset(
            x = center.x - textLayout.size.width / 2f,
            y = center.y - textLayout.size.height / 2f,
        ),
        alpha = alpha,
    )
}

@Composable
private fun AverageTooltip(
    text: String,
    anchorX: Float,
    autoDismissDelay: Long,
    onDismiss: () -> Unit,
) {
    val density = LocalDensity.current
    val tooltipColor = MaterialTheme.colorScheme.inverseSurface
    var containerSize by remember { mutableStateOf(IntSize.Zero) }
    var tooltipSize by remember { mutableStateOf(IntSize.Zero) }

    LaunchedEffect(autoDismissDelay) {
        kotlinx.coroutines.delay(autoDismissDelay)
        onDismiss()
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(46.dp)
            .onSizeChanged { containerSize = it },
    ) {
        val fallbackTooltipWidth = with(density) { 132.dp.toPx() }
        val currentTooltipWidth = tooltipSize.width.takeIf { it > 0 }?.toFloat() ?: fallbackTooltipWidth
        val maxTooltipX = (containerSize.width - currentTooltipWidth).coerceAtLeast(0f)
        val tooltipX = (anchorX - currentTooltipWidth / 2f).coerceIn(0f, maxTooltipX)

        Surface(
            modifier = Modifier
                .offset { IntOffset(tooltipX.roundToInt(), 0) }
                .size(width = 132.dp, height = 34.dp)
                .onSizeChanged { tooltipSize = it },
            shape = MaterialTheme.shapes.medium,
            color = tooltipColor,
            tonalElevation = 6.dp,
            shadowElevation = 6.dp,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = text.ifBlank { "Average value" },
                    color = MaterialTheme.colorScheme.inverseOnSurface,
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1,
                )
            }
        }

        Canvas(
            modifier = Modifier
                .offset {
                    val arrowHalfWidth = with(density) { 7.dp.toPx() }
                    val arrowX = (anchorX - arrowHalfWidth).coerceIn(
                        0f,
                        (containerSize.width - arrowHalfWidth * 2f).coerceAtLeast(0f),
                    )
                    IntOffset(arrowX.roundToInt(), with(density) { 30.dp.toPx() }.roundToInt())
                }
                .size(width = 14.dp, height = 8.dp),
        ) {
            val arrow = Path().apply {
                moveTo(size.width / 2f, size.height)
                lineTo(0f, 0f)
                lineTo(size.width, 0f)
                close()
            }
            drawPath(arrow, tooltipColor)
        }
    }
}

/**
 * Floating Emoji System Provider. Put this around screen content when the emoji should fly outside
 * a clipped slider/card. Without it, [EmojiSlider] falls back to a local overlay.
 */
@Composable
fun EmojiSliderParticleSystem(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val controller = rememberFloatingEmojiState()
    var coordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }

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
                minSize = 24.dp,
                maxSize = 48.dp,
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

@Composable
fun rememberEmojiSliderState(
    initialProgress: Float = DEFAULT_PROGRESS,
): EmojiSliderState = remember { EmojiSliderState(initialProgress) }

class EmojiSliderState(initialProgress: Float) {
    var progress by mutableFloatStateOf(initialProgress.limitToRange())
    var isValueSelected by mutableStateOf(false)
    var isUserSeekable by mutableStateOf(true)
    internal val selection = Animatable(if (isValueSelected) 1f else 0f)

    suspend fun valueSelectedAnimated() {
        isValueSelected = true
        isUserSeekable = false
        selection.animateToSelected()
    }

    suspend fun resetAnimated() {
        isValueSelected = false
        isUserSeekable = true
        selection.animateToResting()
    }

    fun valueSelectedNow() {
        isValueSelected = true
        isUserSeekable = false
    }

    fun resetNow() {
        isValueSelected = false
        isUserSeekable = true
    }
}

private suspend fun Animatable<Float, AnimationVector1D>.animateToSelected() {
    animateTo(
        targetValue = 1f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = Spring.StiffnessMedium),
    )
}

private suspend fun Animatable<Float, AnimationVector1D>.animateToResting() {
    animateTo(
        targetValue = 0f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = Spring.StiffnessMedium),
    )
}

private fun Float.limitToRange(): Float = coerceIn(0f, 1f)

private fun Offset.distanceSquaredTo(other: Offset): Float {
    val dx = x - other.x
    val dy = y - other.y
    return dx * dx + dy * dy
}
