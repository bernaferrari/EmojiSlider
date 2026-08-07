package com.bernaferrari.emojislider

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class EmojiSliderInteractionTest {

    @Test
    fun sliderCanInteract_requiresSeekable() {
        assertFalse(sliderCanInteract(isUserSeekable = false, isValueSelected = false, allowReselection = true))
        assertTrue(sliderCanInteract(isUserSeekable = true, isValueSelected = false, allowReselection = false))
    }

    @Test
    fun sliderCanInteract_blocksAfterOneShotUnlessReselectionAllowed() {
        assertFalse(sliderCanInteract(isUserSeekable = true, isValueSelected = true, allowReselection = false))
        assertTrue(sliderCanInteract(isUserSeekable = true, isValueSelected = true, allowReselection = true))
    }

    @Test
    fun shouldCommitSelection_onlyOnSuccessfulOneShotGesture() {
        assertTrue(shouldCommitSelection(selectValue = true, allowReselection = false))
        assertFalse(shouldCommitSelection(selectValue = false, allowReselection = false))
        assertFalse(shouldCommitSelection(selectValue = true, allowReselection = true))
        assertFalse(shouldCommitSelection(selectValue = false, allowReselection = true))
    }

    @Test
    fun shouldShowAverageTooltip_requiresCommittedSelectionAndFlags() {
        assertTrue(
            shouldShowAverageTooltip(
                selectionCommitted = true,
                shouldDisplayAverage = true,
                shouldDisplayTooltip = true,
            ),
        )
        assertFalse(
            shouldShowAverageTooltip(
                selectionCommitted = false,
                shouldDisplayAverage = true,
                shouldDisplayTooltip = true,
            ),
        )
        assertFalse(
            shouldShowAverageTooltip(
                selectionCommitted = true,
                shouldDisplayAverage = false,
                shouldDisplayTooltip = true,
            ),
        )
        assertFalse(
            shouldShowAverageTooltip(
                selectionCommitted = true,
                shouldDisplayAverage = true,
                shouldDisplayTooltip = false,
            ),
        )
    }
}
