package com.bernaferrari.emojislider

import androidx.compose.ui.geometry.Offset
import kotlin.test.Test
import kotlin.test.assertEquals

class EmojiSliderMathTest {

    @Test
    fun limitToRange_clampsBelowZero() {
        assertEquals(0f, (-0.01f).limitToRange())
        assertEquals(0f, (-100f).limitToRange())
        assertEquals(0f, Float.NEGATIVE_INFINITY.limitToRange())
    }

    @Test
    fun limitToRange_preservesInRangeValues() {
        assertEquals(0f, 0f.limitToRange())
        assertEquals(0.5f, 0.5f.limitToRange())
        assertEquals(1f, 1f.limitToRange())
    }

    @Test
    fun limitToRange_clampsAboveOne() {
        assertEquals(1f, 1.01f.limitToRange())
        assertEquals(1f, 100f.limitToRange())
        assertEquals(1f, Float.POSITIVE_INFINITY.limitToRange())
    }

    @Test
    fun limitToRange_mapsNaNToZero() {
        assertEquals(0f, Float.NaN.limitToRange())
    }

    @Test
    fun distanceSquaredTo_computesSquaredEuclideanDistance() {
        val a = Offset(0f, 0f)
        val b = Offset(3f, 4f)
        assertEquals(25f, a.distanceSquaredTo(b))
        assertEquals(0f, a.distanceSquaredTo(a))
        assertEquals(25f, b.distanceSquaredTo(a))
    }
}
