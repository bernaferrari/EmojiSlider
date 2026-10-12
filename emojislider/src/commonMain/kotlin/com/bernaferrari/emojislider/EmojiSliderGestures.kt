package com.bernaferrari.emojislider

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitHorizontalTouchSlopOrCancellation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.horizontalDrag
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.CancellationException

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

    fun startX(offset: Offset): Float = geometry(canvasSize().width.toFloat()).gestureStartX(offset, currentProgress())

    return this
        .pointerInput(canInteract, registerTouchOnTrack, allowReselection, thumbSizePx) {
            detectTapGestures(
                onTap = { offset ->
                    if (!hits(offset)) return@detectTapGestures
                    onBeginGesture(startX(offset))
                    onScheduleTapRelease()
                },
            )
        }
        .pointerInput(canInteract, registerTouchOnTrack, allowReselection, thumbSizePx) {
            // Hand-rolled instead of detectHorizontalDragGestures: that reports the post-slop position as the
            // start *and* then the over-slop again as the first delta, so the thumb ran ahead of the finger,
            // and a quick grab of the emoji was hit-tested off the thumb. Here the hit test uses the real down
            // position, and the thumb then follows the finger's movement exactly.
            awaitEachGesture {
                val down = awaitFirstDown()
                if (!hits(down.position)) return@awaitEachGesture
                val grabX = startX(down.position)
                val slopChange = awaitHorizontalTouchSlopOrCancellation(down.id) { change, _ -> change.consume() }
                    ?: return@awaitEachGesture

                var dragX = grabX + (slopChange.position.x - down.position.x)
                onBeginGesture(grabX)
                try {
                    onDrag(dragX)
                    val completed = horizontalDrag(slopChange.id) { change ->
                        dragX += change.positionChange().x
                        change.consume()
                        onDrag(dragX)
                    }
                    onEndGesture(completed)
                } catch (e: CancellationException) {
                    onEndGesture(false)
                    throw e
                }
            }
        }
}
