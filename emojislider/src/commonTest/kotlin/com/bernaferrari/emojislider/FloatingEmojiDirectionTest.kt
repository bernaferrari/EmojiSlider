package com.bernaferrari.emojislider

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FloatingEmojiDirectionTest {

    @Test
    fun enum_containsUpAndDown() {
        val values = FloatingEmojiDirection.entries
        assertEquals(2, values.size)
        assertTrue(FloatingEmojiDirection.UP in values)
        assertTrue(FloatingEmojiDirection.DOWN in values)
    }

    @Test
    fun flyAway_targetY_isNegativeForUpAndPositiveForDown() {
        assertTrue(floatingEmojiTravelTargetY(FloatingEmojiDirection.UP) < 0f)
        assertTrue(floatingEmojiTravelTargetY(FloatingEmojiDirection.DOWN) > 0f)
        assertEquals(
            FLOATING_EMOJI_TRAVEL_DISTANCE_PX,
            floatingEmojiTravelTargetY(FloatingEmojiDirection.DOWN),
        )
        assertEquals(
            -FLOATING_EMOJI_TRAVEL_DISTANCE_PX,
            floatingEmojiTravelTargetY(FloatingEmojiDirection.UP),
        )
    }

    @Test
    fun flyAway_rotation_signMatchesDirection() {
        assertTrue(floatingEmojiTravelRotationDegrees(FloatingEmojiDirection.UP) < 0f)
        assertTrue(floatingEmojiTravelRotationDegrees(FloatingEmojiDirection.DOWN) > 0f)
    }
}
