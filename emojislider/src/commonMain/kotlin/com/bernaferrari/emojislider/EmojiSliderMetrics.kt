package com.bernaferrari.emojislider

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.LayoutCoordinates

internal data class EmojiSliderMetrics(
    val trackHeightPx: Float,
    val thumbSizePx: Float,
    val sliderHeightPx: Float,
    val trackInsetPx: Float,
) {
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
