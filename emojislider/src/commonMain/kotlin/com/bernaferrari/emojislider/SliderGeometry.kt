package com.bernaferrari.emojislider

import androidx.compose.ui.geometry.Offset

internal data class SliderGeometry(
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

    fun hoverEmojiCenter(progress: Float): Offset {
        val thumb = thumbCenter(progress)
        return Offset(
            x = thumb.x,
            y = thumb.y - thumbSize * 0.82f,
        )
    }

    fun hitsThumb(offset: Offset, progress: Float, hitRadius: Float = thumbSize): Boolean {
        val center = thumbCenter(progress)
        return offset.distanceSquaredTo(center) <= hitRadius * hitRadius
    }

    fun hitsTrack(offset: Offset, registerTouchOnTrack: Boolean): Boolean = registerTouchOnTrack && offset.x in trackStart..trackEnd

    fun hitsInteractiveTarget(
        offset: Offset,
        progress: Float,
        registerTouchOnTrack: Boolean,
        hitRadius: Float = thumbSize,
    ): Boolean = hitsThumb(offset, progress, hitRadius) || hitsTrack(offset, registerTouchOnTrack)
}
