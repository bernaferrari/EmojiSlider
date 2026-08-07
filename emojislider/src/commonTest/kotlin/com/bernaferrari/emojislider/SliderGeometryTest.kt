package com.bernaferrari.emojislider

import androidx.compose.ui.geometry.Offset
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SliderGeometryTest {

    private fun geometry(
        trackStart: Float = 20f,
        trackEnd: Float = 220f,
        centerY: Float = 40f,
        trackHeight: Float = 8f,
        thumbSize: Float = 40f,
        width: Float = 240f,
    ) = SliderGeometry(
        trackStart = trackStart,
        trackEnd = trackEnd,
        centerY = centerY,
        trackHeight = trackHeight,
        thumbSize = thumbSize,
        width = width,
    )

    @Test
    fun trackWidth_isEndMinusStart() {
        val g = geometry(trackStart = 10f, trackEnd = 110f)
        assertEquals(100f, g.trackWidth)
    }

    @Test
    fun trackWidth_isAtLeastOneWhenDegenerate() {
        val g = geometry(trackStart = 50f, trackEnd = 50f)
        assertEquals(1f, g.trackWidth)
    }

    @Test
    fun progressFor_mapsXAlongTrack() {
        val g = geometry(trackStart = 20f, trackEnd = 220f) // width 200
        assertEquals(0f, g.progressFor(20f))
        assertEquals(0.5f, g.progressFor(120f))
        assertEquals(1f, g.progressFor(220f))
    }

    @Test
    fun progressFor_clampsOutsideTrack() {
        val g = geometry(trackStart = 20f, trackEnd = 220f)
        assertEquals(0f, g.progressFor(-100f))
        assertEquals(0f, g.progressFor(0f))
        assertEquals(1f, g.progressFor(300f))
        assertEquals(1f, g.progressFor(999f))
    }

    @Test
    fun thumbCenter_usesProgressAlongTrackWhenNotClipped() {
        // Wide enough that half-thumb does not clamp interior positions.
        val g = geometry(
            trackStart = 40f,
            trackEnd = 360f,
            thumbSize = 40f,
            width = 400f,
            centerY = 50f,
        )
        val mid = g.thumbCenter(0.5f)
        assertEquals(200f, mid.x)
        assertEquals(50f, mid.y)
    }

    @Test
    fun thumbCenter_neverPlacesCenterOutsideHalfThumbBounds() {
        val width = 200f
        val thumbSize = 48f
        val half = thumbSize / 2f
        val g = geometry(
            trackStart = 0f,
            trackEnd = width,
            thumbSize = thumbSize,
            width = width,
            centerY = 30f,
        )

        for (progress in listOf(-1f, 0f, 0.25f, 0.5f, 0.75f, 1f, 2f)) {
            val center = g.thumbCenter(progress)
            assertTrue(
                center.x >= half - 0.001f,
                "progress=$progress: x=${center.x} should be >= $half",
            )
            assertTrue(
                center.x <= width - half + 0.001f,
                "progress=$progress: x=${center.x} should be <= ${width - half}",
            )
            assertEquals(30f, center.y)
        }
    }

    @Test
    fun hitsThumb_isTrueInsideRadiusAndFalseOutside() {
        val g = geometry(trackStart = 40f, trackEnd = 360f, thumbSize = 40f, width = 400f, centerY = 50f)
        val center = g.thumbCenter(0.5f)
        assertTrue(g.hitsThumb(center, progress = 0.5f, hitRadius = 40f))
        assertTrue(g.hitsThumb(center.copy(x = center.x + 20f), progress = 0.5f, hitRadius = 40f))
        assertFalse(g.hitsThumb(center.copy(x = center.x + 41f), progress = 0.5f, hitRadius = 40f))
    }

    @Test
    fun hitsTrack_respectsRegisterTouchOnTrack() {
        val g = geometry(trackStart = 20f, trackEnd = 220f, centerY = 40f)
        val onTrack = Offset(120f, 40f)
        val offTrack = Offset(5f, 40f)
        assertTrue(g.hitsTrack(onTrack, registerTouchOnTrack = true))
        assertFalse(g.hitsTrack(onTrack, registerTouchOnTrack = false))
        assertFalse(g.hitsTrack(offTrack, registerTouchOnTrack = true))
    }

    @Test
    fun hitsInteractiveTarget_thumbOrTrack() {
        val g = geometry(trackStart = 40f, trackEnd = 360f, thumbSize = 40f, width = 400f, centerY = 50f)
        val thumb = g.thumbCenter(0.1f)
        val midTrack = Offset(200f, 50f)
        assertTrue(
            g.hitsInteractiveTarget(thumb, progress = 0.1f, registerTouchOnTrack = false, hitRadius = 40f),
        )
        assertFalse(
            g.hitsInteractiveTarget(midTrack, progress = 0.1f, registerTouchOnTrack = false, hitRadius = 40f),
        )
        assertTrue(
            g.hitsInteractiveTarget(midTrack, progress = 0.1f, registerTouchOnTrack = true, hitRadius = 40f),
        )
    }

    @Test
    fun hoverEmojiCenter_isAboveThumb() {
        val g = geometry(trackStart = 40f, trackEnd = 360f, thumbSize = 40f, width = 400f, centerY = 50f)
        val thumb = g.thumbCenter(0.5f)
        val hover = g.hoverEmojiCenter(0.5f)
        assertEquals(thumb.x, hover.x)
        assertTrue(hover.y < thumb.y)
    }

    @Test
    fun thumbCenter_clampsProgressBeforePlacing() {
        val g = geometry(
            trackStart = 50f,
            trackEnd = 250f,
            thumbSize = 20f,
            width = 300f,
            centerY = 10f,
        )
        assertEquals(g.thumbCenter(0f).x, g.thumbCenter(-5f).x)
        assertEquals(g.thumbCenter(1f).x, g.thumbCenter(5f).x)
    }
}
