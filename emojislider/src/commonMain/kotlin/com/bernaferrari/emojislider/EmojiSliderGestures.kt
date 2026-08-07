package com.bernaferrari.emojislider

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Tap + horizontal-drag handling for [EmojiSlider].
 *
 * [currentProgress] and [canvasSize] are read from the latest composition (not restart keys) so
 * dragging does not tear down the pointer input each frame.
 */
internal fun Modifier.emojiSliderGestures(
    canInteract: Boolean,
    registerTouchOnTrack: Boolean,
    allowReselection: Boolean,
    thumbSizePx: Float,
    canvasSize: () -> IntSize,
    currentProgress: () -> Float,
    geometry: (Float) -> SliderGeometry,
    onBeginGesture: (x: Float) -> Unit,
    onDrag: (x: Float) -> Unit,
    onEndGesture: (commitSelection: Boolean) -> Unit,
    coroutineScope: CoroutineScope,
): Modifier {
    return this
        .pointerInput(canInteract, registerTouchOnTrack, allowReselection, thumbSizePx) {
            detectTapGestures(
                onTap = { offset ->
                    val size = canvasSize()
                    if (!canInteract || size.width == 0) return@detectTapGestures

                    val width = size.width.toFloat()
                    val sliderGeometry = geometry(width)
                    if (!sliderGeometry.hitsInteractiveTarget(
                            offset = offset,
                            progress = currentProgress(),
                            registerTouchOnTrack = registerTouchOnTrack,
                            hitRadius = thumbSizePx,
                        )
                    ) {
                        return@detectTapGestures
                    }

                    onBeginGesture(offset.x)
                    coroutineScope.launch {
                        delay(TAP_RELEASE_PARTICLE_DELAY_MILLIS)
                        onEndGesture(true)
                    }
                },
            )
        }
        .pointerInput(canInteract, registerTouchOnTrack, allowReselection, thumbSizePx) {
            var dragX = 0f
            var dragActive = false
            detectHorizontalDragGestures(
                onDragStart = { offset ->
                    val size = canvasSize()
                    if (!canInteract || size.width == 0) {
                        dragActive = false
                        return@detectHorizontalDragGestures
                    }

                    val sliderGeometry = geometry(size.width.toFloat())
                    if (!sliderGeometry.hitsInteractiveTarget(
                            offset = offset,
                            progress = currentProgress(),
                            registerTouchOnTrack = registerTouchOnTrack,
                            hitRadius = thumbSizePx,
                        )
                    ) {
                        dragActive = false
                        return@detectHorizontalDragGestures
                    }

                    dragActive = true
                    dragX = offset.x
                    onBeginGesture(offset.x)
                },
                onHorizontalDrag = { change, dragAmount ->
                    if (!dragActive || canvasSize().width == 0) return@detectHorizontalDragGestures
                    change.consume()
                    dragX += dragAmount
                    onDrag(dragX)
                },
                onDragCancel = {
                    if (!dragActive) return@detectHorizontalDragGestures
                    dragActive = false
                    onEndGesture(false)
                },
                onDragEnd = {
                    if (!dragActive) return@detectHorizontalDragGestures
                    dragActive = false
                    onEndGesture(true)
                },
            )
        }
}
