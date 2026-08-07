package com.bernaferrari.emojislider

import androidx.compose.ui.unit.IntSize
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class EmojiSliderHandleTest {

    private fun handle(allowReselection: Boolean = false): EmojiSliderHandle {
        val session = EmojiSliderHandle(0.25f, FloatingEmojiController(), TooltipState())
        session.metrics = EmojiSliderMetrics(
            trackHeightPx = 8f,
            thumbSizePx = 40f,
            sliderHeightPx = 80f,
            trackInsetPx = 0f,
        )
        session.canvasSize = IntSize(200, 80)
        session.allowReselection = allowReselection
        return session
    }

    @Test
    fun beginAt_startsTrackingAndNotifies() {
        val session = handle()
        var value = 0.25f
        var starts = 0
        session.onValueChange = { value = it }
        session.onStartTracking = { starts += 1 }

        session.beginAt(160f)

        assertTrue(session.isDragging)
        assertTrue(session.floating.isTracking)
        assertEquals(1, starts)
        assertTrue(value > 0.7f, "expected beginAt near the right edge, got $value")
    }

    @Test
    fun end_oneShot_locksAndShowsTooltip() {
        val session = handle(allowReselection = false)
        session.beginAt(160f)
        session.end(commitSelection = true)

        assertFalse(session.isDragging)
        assertTrue(session.isValueSelected)
        assertTrue(session.tooltip.isVisible)
        assertFalse(session.canInteract)
        assertFalse(session.applySemanticsProgress(0.1f))
        assertTrue(session.progress > 0.7f)
    }

    @Test
    fun end_reselection_staysInteractiveWithoutTooltip() {
        val session = handle(allowReselection = true)
        session.beginAt(160f)
        session.end(commitSelection = true)

        assertFalse(session.isValueSelected)
        assertTrue(session.canInteract)
        assertFalse(session.tooltip.isVisible)
        assertTrue(session.applySemanticsProgress(0.4f))
        assertEquals(0.4f, session.progress)
    }
}
