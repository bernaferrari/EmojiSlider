package com.bernaferrari.emojislider

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.LayoutCoordinates

/** Clamps to `0f..1f`. `NaN` → `0f`; infinities map to the matching endpoint. */
internal fun Float.limitToRange(): Float {
    if (isNaN()) return 0f
    return coerceIn(0f, 1f)
}

internal fun Offset.distanceSquaredTo(other: Offset): Float {
    val dx = x - other.x
    val dy = y - other.y
    return dx * dx + dy * dy
}

internal fun mapOffsetToOverlay(
    local: Offset,
    slider: LayoutCoordinates?,
    overlay: LayoutCoordinates?,
): Offset {
    if (slider == null || overlay == null) return local
    if (!slider.isAttached || !overlay.isAttached) return local
    return overlay.localPositionOf(slider, local)
}

internal fun sliderGeometry(
    width: Float,
    trackHeightPx: Float,
    thumbSizePx: Float,
    sliderHeightPx: Float,
    trackInsetPx: Float,
): SliderGeometry {
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

    /**
     * The thumb is always centred on the end of the filled track: on the track start at 0 and the track end at 1.
     * With a [EmojiSliderSizes.trackInset] smaller than half the thumb, the emoji overhangs the slider bounds there.
     */
    fun thumbCenter(progress: Float): Offset = Offset(x = trackStart + activeTrackWidth(progress), y = centerY)

    /** Filled part of the track: empty at 0, full at 1, always ending at the thumb centre. */
    fun activeTrackWidth(progress: Float): Float = progress.limitToRange() * trackWidth

    fun hoverEmojiCenter(progress: Float): Offset {
        val thumb = thumbCenter(progress)
        return Offset(x = thumb.x, y = thumb.y - thumbSize * 0.82f)
    }

    fun hitsThumb(offset: Offset, progress: Float, hitRadius: Float = thumbSize): Boolean {
        val center = thumbCenter(progress)
        return offset.distanceSquaredTo(center) <= hitRadius * hitRadius
    }

    /**
     * Where a press starts the gesture. Grabbing the thumb keeps it where it is (dragging continues from the
     * thumb, no jump to the finger); pressing the track elsewhere seeks to that point.
     */
    fun gestureStartX(offset: Offset, progress: Float): Float = if (hitsThumb(offset, progress, hitRadius = thumbSize * THUMB_GRAB_RADIUS_FACTOR)) thumbCenter(progress).x else offset.x

    fun hitsTrack(offset: Offset, registerTouchOnTrack: Boolean): Boolean = registerTouchOnTrack && offset.x in trackStart..trackEnd

    fun hitsInteractiveTarget(
        offset: Offset,
        progress: Float,
        registerTouchOnTrack: Boolean,
        hitRadius: Float = thumbSize,
    ): Boolean = hitsThumb(offset, progress, hitRadius) || hitsTrack(offset, registerTouchOnTrack)
}

/** Grab radius for the thumb, relative to its size: the emoji plus a little slack around it. */
private const val THUMB_GRAB_RADIUS_FACTOR = 0.6f
