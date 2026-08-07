package com.bernaferrari.emojislider

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntSize

/**
 * Tap + horizontal-drag handling for [EmojiSlider].
 *
 * Size/progress are read live (not restart keys) so a drag does not rebuild pointer input each frame.
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
    onScheduleTapRelease: () -> Unit,
): Modifier {
    fun hits(offset: Offset): Boolean {
        val size = canvasSize()
        if (!canInteract || size.width == 0) return false
        return geometry(size.width.toFloat()).hitsInteractiveTarget(
            offset = offset,
            progress = currentProgress(),
            registerTouchOnTrack = registerTouchOnTrack,
            hitRadius = thumbSizePx,
        )
    }

    return this
        .pointerInput(canInteract, registerTouchOnTrack, allowReselection, thumbSizePx) {
            detectTapGestures(
                onTap = { offset ->
                    if (!hits(offset)) return@detectTapGestures
                    onBeginGesture(offset.x)
                    onScheduleTapRelease()
                },
            )
        }
        .pointerInput(canInteract, registerTouchOnTrack, allowReselection, thumbSizePx) {
            var dragX = 0f
            var dragActive = false
            detectHorizontalDragGestures(
                onDragStart = { offset ->
                    dragActive = hits(offset)
                    if (!dragActive) return@detectHorizontalDragGestures
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
