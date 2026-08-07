package com.bernaferrari.emojislider

import androidx.compose.ui.geometry.Offset
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FloatingEmojiPhysicsTest {

    @Test
    fun trackingSize_lerpsBetweenMinAndMax() {
        assertEquals(10f, trackingEmojiSizePx(10f, 30f, 0f))
        assertEquals(20f, trackingEmojiSizePx(10f, 30f, 0.5f))
        assertEquals(30f, trackingEmojiSizePx(10f, 30f, 1f))
        assertEquals(10f, trackingEmojiSizePx(10f, 30f, -2f))
        assertEquals(30f, trackingEmojiSizePx(10f, 30f, 4f))
    }

    @Test
    fun flyAway_atStart_isFullyVisibleAtOrigin() {
        val particle = spawnFlyAwayParticle(
            nowMs = 1000L,
            emoji = "😍",
            position = Offset(10f, 20f),
            displayedSize = 40f,
            progress = 0.5f,
            minPx = 20f,
            maxPx = 40f,
            direction = FloatingEmojiDirection.UP,
        )
        val pose = floatingEmojiFlyAwayPose(particle, nowMs = 1000L)
        assertTrue(pose.isVisible)
        assertEquals(0f, pose.offsetX, 0.01f)
        assertEquals(0f, pose.offsetY, 0.01f)
        assertEquals(1f, pose.alpha, 0.01f)
        assertEquals(40f, pose.size, 0.01f)
    }

    @Test
    fun flyAway_upMovesNegativeY_downMovesPositiveY() {
        val up = spawnFlyAwayParticle(
            nowMs = 0L,
            emoji = "😍",
            position = Offset.Zero,
            displayedSize = 30f,
            progress = 1f,
            minPx = 10f,
            maxPx = 30f,
            direction = FloatingEmojiDirection.UP,
        )
        val down = up.copy(direction = FloatingEmojiDirection.DOWN)
        val mid = FLY_AWAY_DURATION_MILLIS / 2
        assertTrue(floatingEmojiFlyAwayPose(up, mid).offsetY < 0f)
        assertTrue(floatingEmojiFlyAwayPose(down, mid).offsetY > 0f)
    }

    @Test
    fun prune_dropsFinishedParticles() {
        val live = spawnFlyAwayParticle(0L, "😍", Offset.Zero, 20f, 0.5f, 10f, 20f, FloatingEmojiDirection.UP)
        val dead = live.copy(id = 1L, startTime = 0L)
        val kept = pruneExpiredParticles(listOf(live, dead), nowMs = FLY_AWAY_DURATION_MILLIS + 1)
        assertTrue(kept.isEmpty())
        assertEquals(1, pruneExpiredParticles(listOf(live), nowMs = 10L).size)
    }
}
