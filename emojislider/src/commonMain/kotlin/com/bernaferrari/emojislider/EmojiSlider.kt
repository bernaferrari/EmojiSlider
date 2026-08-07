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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
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
import com.bernaferrari.emojislider.generated.resources.Res
import com.bernaferrari.emojislider.generated.resources.noto_emoji_regular
import org.jetbrains.compose.resources.Font

/**
 * Compose EmojiSlider inspired by the original View implementation.
 *
 * Value is fully controlled via [value] / [onValueChange] (`0f..1f`).
 */
@Composable
fun EmojiSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    emoji: String = DEFAULT_EMOJI,
    onStartTracking: () -> Unit = {},
    onStopTracking: () -> Unit = {},
    colors: EmojiSliderColors = EmojiSliderColors(),
    behavior: EmojiSliderBehavior = EmojiSliderBehavior(),
    sizes: EmojiSliderSizes = EmojiSliderSizes(),
    averageProgress: Float = DEFAULT_AVERAGE_PROGRESS,
    resultBitmap: ImageBitmap? = null,
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
    val tooltip = remember { TooltipState() }
    val handle = remember(floating, tooltip) {
        EmojiSliderHandle(value.limitToRange(), floating, tooltip)
    }

    val trackInset = sizes.resolvedTrackInset
    handle.config = with(density) {
        EmojiSliderConfig(
            trackHeightPx = sizes.trackHeight.toPx(),
            thumbSizePx = sizes.thumbSize.toPx(),
            sliderHeightPx = sizes.sliderHeight.toPx(),
            trackInsetPx = trackInset.toPx(),
            isUserSeekable = behavior.isUserSeekable,
            allowReselection = behavior.allowReselection,
            shouldDisplayAverage = behavior.displayAverage,
            shouldDisplayTooltip = behavior.displayTooltip,
            minEmojiSize = sizes.minEmojiSize,
            maxEmojiSize = sizes.maxEmojiSize,
            emoji = emoji,
            direction = behavior.floatingDirection,
            onValueChange = onValueChange,
            onStartTracking = onStartTracking,
            onStopTracking = onStopTracking,
            haptic = haptics,
            mapToOverlay = { local ->
                if (ambientController == null) {
                    local
                } else {
                    mapOffsetToOverlay(local, handle.canvasCoordinates, ambientCoordinates)
                }
            },
        )
    }

    LaunchedEffect(value) { handle.syncFromValue(value) }
    LaunchedEffect(behavior.allowReselection) { handle.onAllowReselectionChanged() }

    val average = averageProgress.limitToRange()
    val thumbScale by animateFloatAsState(
        targetValue = when {
            handle.isValueSelected && !behavior.allowReselection -> 0f
            handle.isDragging -> sizes.thumbPressedScale.limitToRange()
            else -> 1f
        },
        animationSpec = spring(dampingRatio = 0.72f, stiffness = Spring.StiffnessMediumLow),
        label = "emoji_thumb_scale",
    )
    val resultScale by animateFloatAsState(
        targetValue = if (handle.isValueSelected && behavior.displayResult && !behavior.allowReselection) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.62f, stiffness = Spring.StiffnessLow),
        label = "result_scale",
    )
    val averageScale by animateFloatAsState(
        targetValue = if (handle.isValueSelected && behavior.displayAverage && !behavior.allowReselection) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.62f, stiffness = Spring.StiffnessLow),
        label = "average_scale",
    )

    Box(
        modifier = modifier
            .testTag(EMOJI_SLIDER_TEST_TAG)
            .fillMaxWidth()
            .height(sizes.sliderHeight)
            .semantics {
                progressBarRangeInfo = ProgressBarRangeInfo(handle.progress, 0f..1f)
                setProgress { handle.applySemanticsProgress(it) }
            },
        contentAlignment = Alignment.Center,
    ) {
        val canvasModifier = Modifier
            .fillMaxWidth()
            .height(sizes.sliderHeight)
            .onGloballyPositioned {
                handle.canvasSize = it.size
                handle.canvasCoordinates = it
            }
            .emojiSliderGestures(
                canInteract = handle.canInteract,
                registerTouchOnTrack = behavior.registerTouchOnTrack,
                allowReselection = behavior.allowReselection,
                thumbSizePx = handle.config.thumbSizePx,
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
                    activeTrackGradient = colors.activeTrack,
                    colorStart = colors.start,
                    colorEnd = colors.end,
                    colorTrack = colors.track,
                    averageProgress = average,
                    shouldDisplayAverage = behavior.displayAverage,
                    shouldDisplayResult = behavior.displayResult,
                    averageScale = averageScale,
                    resultScale = resultScale,
                    thumbScale = thumbScale,
                    resultBitmap = resultBitmap,
                    textMeasurer = textMeasurer,
                    emojiFontFamily = emojiFontFamily,
                    isDragging = handle.isDragging,
                    allowReselection = behavior.allowReselection,
                )
            }
            if (ambientController == null) {
                FloatingEmojiCanvas(controller = localController, modifier = Modifier.matchParentSize())
            }
        }

        val averageAnchorX = handle.canvasSize.width
            .takeIf { it > 0 }
            ?.let { handle.geometry(it.toFloat()).thumbCenter(average).x }
            ?: 0f

        AnimatedVisibility(
            visible = tooltip.isVisible && behavior.displayAverage && behavior.displayTooltip,
            modifier = Modifier.align(Alignment.TopStart),
            enter = fadeIn(animationSpec = tween(220)),
            exit = fadeOut(animationSpec = tween(260)),
        ) {
            AverageTooltip(
                text = behavior.tooltipText,
                anchorX = averageAnchorX,
                autoDismissDelay = behavior.tooltipAutoDismissMillis,
                onDismiss = tooltip::hide,
                showGeneration = tooltip.showGeneration,
            )
        }
    }
}
