package com.bernaferrari.emojislider

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TooltipStateTest {

    @Test
    fun initialState_isHidden() {
        val state = TooltipState()
        assertFalse(state.isVisible)
    }

    @Test
    fun show_makesVisible() {
        val state = TooltipState()
        state.show()
        assertTrue(state.isVisible)
    }

    @Test
    fun hide_clearsVisibility() {
        val state = TooltipState()
        state.show()
        state.hide()
        assertFalse(state.isVisible)
    }

    @Test
    fun toggle_flipsVisibility() {
        val state = TooltipState()
        state.toggle()
        assertTrue(state.isVisible)
        state.toggle()
        assertFalse(state.isVisible)
    }

    @Test
    fun show_incrementsShowGenerationEachCall() {
        val state = TooltipState()
        assertEquals(0, state.showGeneration)
        state.show()
        assertEquals(1, state.showGeneration)
        state.show()
        assertEquals(2, state.showGeneration)
        state.hide()
        assertEquals(2, state.showGeneration)
    }
}
