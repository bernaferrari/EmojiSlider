package com.bernaferrari.emojislider

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
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
import androidx.compose.ui.unit.dp
import com.bernaferrari.emojislider.generated.resources.Res
import com.bernaferrari.emojislider.generated.resources.noto_emoji_regular
import org.jetbrains.compose.resources.Font

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
    colorStart: Color = Color(0xFF6200EE),
    colorEnd: Color = Color(0xFFE91E63),
    colorTrack: Color = Color(0xFFE0E0E0),
    activeTrackGradient: Brush = Brush.horizontalGradient(listOf(colorStart, colorEnd)),
    isUserSeekable: Boolean = true,
    registerTouchOnTrack: Boolean = true,
    allowReselection: Boolean = false,
    floatingDirection: FloatingEmojiDirection = FloatingEmojiDirection.UP,
    minEmojiSize: Dp = DefaultMinEmojiSize,
    maxEmojiSize: Dp = DefaultMaxEmojiSize,
    sliderParticleSystem: (@Composable () -> Unit)? = null,
    averageProgressValue: Float = DEFAULT_AVERAGE_PROGRESS,
    shouldDisplayAverage: Boolean = true,
    shouldDisplayResultPicture: Boolean = true,
    shouldDisplayTooltip: Boolean = true,
    tooltipText: String = "Average value",
    tooltipAutoDismissTimer: Long = DEFAULT_TOOLTIP_DISMISS_MILLIS,
    thumbSizePercentWhenPressed: Float = DEFAULT_THUMB_PRESSED_SCALE,
    resultBitmap: ImageBitmap? = null,
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
    val scope = rememberCoroutineScope()

    val ambientController = LocalFloatingEmojiController.current
    val ambientCoordinates = LocalFloatingEmojiCoordinates.current
    val localController = rememberFloatingEmojiState()
    val floating = ambientController ?: localController
    val tooltip = rememberTooltipState()
    val handle = remember(floating, tooltip) {
        EmojiSliderHandle(value.limitToRange(), floating, tooltip)
    }

    handle.metrics = with(density) {
        EmojiSliderMetrics(
            trackHeightPx = trackHeight.toPx(),
            thumbSizePx = thumbSize.toPx(),
            sliderHeightPx = sliderHeight.toPx(),
            trackInsetPx = trackInset.toPx(),
        )
    }
    handle.isUserSeekable = isUserSeekable
    handle.allowReselection = allowReselection
    handle.shouldDisplayAverage = shouldDisplayAverage
    handle.shouldDisplayTooltip = shouldDisplayTooltip
    handle.minEmojiSize = minEmojiSize
    handle.maxEmojiSize = maxEmojiSize
    handle.emoji = emoji
    handle.direction = floatingDirection
    handle.onValueChange = onValueChange
    handle.onStartTracking = onStartTracking
    handle.onStopTracking = onStopTracking
    handle.onHaptic = { haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove) }
    handle.mapToOverlay = { local ->
        if (ambientController == null) {
            local
        } else {
            mapOffsetToOverlay(local, handle.canvasCoordinates, ambientCoordinates)
        }
    }

    LaunchedEffect(value) { handle.syncFromValue(value) }
    LaunchedEffect(allowReselection) { handle.onAllowReselectionChanged() }

    val averageProgress = averageProgressValue.limitToRange()
    val scales = animateEmojiSliderScales(
        isValueSelected = handle.isValueSelected,
        isDragging = handle.isDragging,
        allowReselection = allowReselection,
        shouldDisplayResultPicture = shouldDisplayResultPicture,
        shouldDisplayAverage = shouldDisplayAverage,
        pressedThumbScale = thumbSizePercentWhenPressed,
    )

    Box(
        modifier = modifier
            .testTag(EMOJI_SLIDER_TEST_TAG)
            .fillMaxWidth()
            .height(sliderHeight)
            .semantics {
                progressBarRangeInfo = ProgressBarRangeInfo(handle.progress, 0f..1f)
                setProgress { handle.applySemanticsProgress(it) }
            },
        contentAlignment = Alignment.Center,
    ) {
        val canvasModifier = Modifier
            .fillMaxWidth()
            .height(sliderHeight)
            .onGloballyPositioned {
                handle.canvasSize = it.size
                handle.canvasCoordinates = it
            }
            .emojiSliderGestures(
                canInteract = handle.canInteract,
                registerTouchOnTrack = registerTouchOnTrack,
                allowReselection = allowReselection,
                thumbSizePx = handle.metrics.thumbSizePx,
                canvasSize = { handle.canvasSize },
                currentProgress = { handle.progress },
                geometry = handle::geometry,
                onBeginGesture = handle::beginAt,
                onDrag = handle::dragTo,
                onEndGesture = handle::end,
                coroutineScope = scope,
            )

        Box(Modifier.fillMaxSize()) {
            Canvas(canvasModifier) {
                drawEmojiSlider(
                    progress = handle.progress,
                    geometry = handle.geometry(size.width),
                    emoji = emoji,
                    activeTrackGradient = activeTrackGradient,
                    colorStart = colorStart,
                    colorEnd = colorEnd,
                    colorTrack = colorTrack,
                    averageProgress = averageProgress,
                    shouldDisplayAverage = shouldDisplayAverage,
                    shouldDisplayResult = shouldDisplayResultPicture,
                    averageScale = scales.average,
                    resultScale = scales.result,
                    thumbScale = scales.thumb,
                    resultBitmap = resultBitmap,
                    textMeasurer = textMeasurer,
                    emojiFontFamily = emojiFontFamily,
                    isDragging = handle.isDragging,
                    allowReselection = allowReselection,
                )
            }
            if (ambientController == null) {
                FloatingEmojiCanvas(controller = localController, modifier = Modifier.matchParentSize())
            }
        }

        sliderParticleSystem?.invoke()

        val averageAnchorX = handle.canvasSize.width
            .takeIf { it > 0 }
            ?.let { handle.geometry(it.toFloat()).thumbCenter(averageProgress).x }
            ?: 0f

        AnimatedVisibility(
            visible = tooltip.isVisible && shouldDisplayAverage && shouldDisplayTooltip,
            modifier = Modifier.align(Alignment.TopStart),
            enter = fadeIn(animationSpec = tween(220)),
            exit = fadeOut(animationSpec = tween(260)),
        ) {
            AverageTooltip(
                text = tooltipText,
                anchorX = averageAnchorX,
                autoDismissDelay = tooltipAutoDismissTimer,
                onDismiss = tooltip::hide,
                showGeneration = tooltip.showGeneration,
            )
        }
    }
}
