package com.bernaferrari.emojislider

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.setProgress
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.bernaferrari.emojislider.generated.resources.Res
import com.bernaferrari.emojislider.generated.resources.noto_emoji_regular
import org.jetbrains.compose.resources.Font

internal const val EMOJI_SLIDER_TEST_TAG = "emoji_slider"

/**
 * Compose EmojiSlider inspired by the original View implementation.
 *
 * The slider keeps the original behavioral contract: optional whole-track touch target, optional
 * one-shot selection, average/result reveal after release, and a floating emoji while tracking.
 *
 * Value is fully controlled via [value] / [onValueChange] (`0f..1f`).
 */
@Composable
fun EmojiSlider(
    modifier: Modifier = Modifier,
    emoji: String = DEFAULT_EMOJI,
    value: Float = DEFAULT_PROGRESS,
    onValueChange: (Float) -> Unit = {},
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
    floatingDirection: FloatingEmojiDirection = FloatingEmojiDirection.UP,
    minEmojiSize: Dp = DefaultMinEmojiSize,
    maxEmojiSize: Dp = DefaultMaxEmojiSize,
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
    val canInteract = sliderCanInteract(isUserSeekable, isValueSelected, allowReselection)
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

    fun startTrackingAtCurrentProgress(sliderGeometry: SliderGeometry) {
        val hoverPosition = particlePosition(sliderGeometry.hoverEmojiCenter(currentProgress))
        floatingController.startTracking(
            emoji = currentGestureEmoji,
            position = hoverPosition,
            direction = currentGestureDirection,
            minSize = minEmojiSize,
            maxSize = maxEmojiSize,
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
                position = particlePosition(sliderGeometry.hoverEmojiCenter(currentProgress)),
            )
        }

        isDragging = false
        floatingController.stopTracking()
        currentOnStopTracking()

        val committed = shouldCommitSelection(selectValue, allowReselection)
        if (committed) {
            isValueSelected = true
            if (shouldShowAverageTooltip(true, shouldDisplayAverage, shouldDisplayTooltip)) {
                tooltipState.show()
            }
        }
    }

    fun beginGestureAt(x: Float) {
        tooltipState.hide()
        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        updateProgressFromTouch(x, canvasSize.width.toFloat())
        isDragging = true
        startTrackingAtCurrentProgress(geometry(canvasSize.width.toFloat()))
        currentOnStartTracking()
    }

    fun dragTo(x: Float) {
        if (canvasSize.width == 0) return
        updateProgressFromTouch(x, canvasSize.width.toFloat())
        val sliderGeometry = geometry(canvasSize.width.toFloat())
        floatingController.updateProgress(
            progress = currentProgress,
            position = particlePosition(sliderGeometry.hoverEmojiCenter(currentProgress)),
        )
    }

    Box(
        modifier = modifier
            .testTag(EMOJI_SLIDER_TEST_TAG)
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
            .emojiSliderGestures(
                canInteract = canInteract,
                registerTouchOnTrack = registerTouchOnTrack,
                allowReselection = allowReselection,
                thumbSizePx = thumbSizePx,
                canvasSize = { canvasSize },
                currentProgress = { currentProgress },
                geometry = ::geometry,
                onBeginGesture = ::beginGestureAt,
                onDrag = ::dragTo,
                onEndGesture = ::stopDragging,
                coroutineScope = coroutineScope,
            )

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
                    modifier = Modifier.matchParentSize(),
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
                showGeneration = tooltipState.showGeneration,
            )
        }
    }
}
