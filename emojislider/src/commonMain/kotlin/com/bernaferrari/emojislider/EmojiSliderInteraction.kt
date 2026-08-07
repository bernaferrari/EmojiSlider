package com.bernaferrari.emojislider

internal fun sliderCanInteract(
    isUserSeekable: Boolean,
    isValueSelected: Boolean,
    allowReselection: Boolean,
): Boolean = isUserSeekable && (!isValueSelected || allowReselection)

internal fun shouldCommitSelection(
    selectValue: Boolean,
    allowReselection: Boolean,
): Boolean = selectValue && !allowReselection

internal fun shouldShowAverageTooltip(
    selectionCommitted: Boolean,
    shouldDisplayAverage: Boolean,
    shouldDisplayTooltip: Boolean,
): Boolean = selectionCommitted && shouldDisplayAverage && shouldDisplayTooltip
