package com.bernaferrari.emojislider

import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

class EmojiSliderStyleTest {

    @Test
    fun resolvedTrackInset_defaultsToHalfThumb() {
        val sizes = EmojiSliderSizes(thumbSize = 40.dp)
        assertEquals(20.dp, sizes.resolvedTrackInset)
    }

    @Test
    fun resolvedTrackInset_usesExplicitZero() {
        assertEquals(0.dp, EmojiSliderSizes(trackInset = 0.dp).resolvedTrackInset)
    }
}
