package com.bernaferrari.emojislider

import androidx.compose.ui.geometry.Offset
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FloatingEmojiControllerTest {

    @Test
    fun initialState_isNotTrackingWithDefaultProgress() {
        val controller = FloatingEmojiController()
        assertFalse(controller.isTracking)
        assertEquals(0f, controller.progress)
        assertEquals(Offset.Zero, controller.position)
        assertEquals(FloatingEmojiDirection.UP, controller.direction)
        assertEquals(DEFAULT_EMOJI, controller.emoji)
        assertEquals(DefaultMinEmojiSize, controller.minSize)
        assertEquals(DefaultMaxEmojiSize, controller.maxSize)
    }

    @Test
    fun startTracking_setsEmojiPositionDirectionAndTracking() {
        val controller = FloatingEmojiController()
        val pos = Offset(12f, 34f)
        controller.startTracking(
            emoji = "🔥",
            position = pos,
            direction = FloatingEmojiDirection.DOWN,
            minSize = DefaultMinEmojiSize * 2,
            maxSize = DefaultMaxEmojiSize * 2,
        )

        assertTrue(controller.isTracking)
        assertEquals("🔥", controller.emoji)
        assertEquals(pos, controller.position)
        assertEquals(FloatingEmojiDirection.DOWN, controller.direction)
        assertEquals(DefaultMinEmojiSize * 2, controller.minSize)
        assertEquals(DefaultMaxEmojiSize * 2, controller.maxSize)
    }

    @Test
    fun updateParticleSizes_setsSizesWithoutTracking() {
        val controller = FloatingEmojiController()
        controller.updateParticleSizes(DefaultMinEmojiSize * 3, DefaultMaxEmojiSize * 3)
        assertFalse(controller.isTracking)
        assertEquals(DefaultMinEmojiSize * 3, controller.minSize)
        assertEquals(DefaultMaxEmojiSize * 3, controller.maxSize)
    }

    @Test
    fun startTracking_defaultsDirectionToUp() {
        val controller = FloatingEmojiController()
        controller.startTracking(emoji = "😀", position = Offset(1f, 2f))
        assertEquals(FloatingEmojiDirection.UP, controller.direction)
        assertTrue(controller.isTracking)
    }

    @Test
    fun updateProgress_clampsBelowZeroAndAboveOne() {
        val controller = FloatingEmojiController()
        val pos = Offset(5f, 6f)

        controller.updateProgress(progress = -0.25f, position = pos)
        assertEquals(0f, controller.progress)
        assertEquals(pos, controller.position)

        controller.updateProgress(progress = 1.75f, position = Offset(7f, 8f))
        assertEquals(1f, controller.progress)
        assertEquals(Offset(7f, 8f), controller.position)
    }

    @Test
    fun updateProgress_mapsNaNToZero() {
        val controller = FloatingEmojiController()
        controller.updateProgress(progress = Float.NaN, position = Offset.Zero)
        assertEquals(0f, controller.progress)
    }

    @Test
    fun updateProgress_preservesInRangeValues() {
        val controller = FloatingEmojiController()
        controller.updateProgress(0.33f, Offset(9f, 10f))
        assertEquals(0.33f, controller.progress)
        assertEquals(Offset(9f, 10f), controller.position)
    }

    @Test
    fun stopTracking_clearsTrackingFlag() {
        val controller = FloatingEmojiController()
        controller.startTracking("😍", Offset.Zero)
        assertTrue(controller.isTracking)

        controller.stopTracking()
        assertFalse(controller.isTracking)
    }

    @Test
    fun stopTracking_preservesLastProgressAndPosition() {
        val controller = FloatingEmojiController()
        controller.startTracking("😍", Offset(1f, 1f))
        controller.updateProgress(0.6f, Offset(2f, 3f))
        controller.stopTracking()

        assertFalse(controller.isTracking)
        assertEquals(0.6f, controller.progress)
        assertEquals(Offset(2f, 3f), controller.position)
        assertEquals("😍", controller.emoji)
    }
}
