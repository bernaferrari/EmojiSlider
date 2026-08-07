package com.bernaferrari.emojislider

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

/**
 * Remembers a [TooltipState] for controlling average-tooltip visibility.
 */
@Composable
fun rememberTooltipState(): TooltipState = remember { TooltipState() }

/**
 * Simple show/hide holder used by [EmojiSlider] for the average tooltip.
 *
 * [showGeneration] increments on every [show] so auto-dismiss effects can restart per session.
 */
class TooltipState {
    private var _isVisible by mutableStateOf(false)
    private var _showGeneration by mutableIntStateOf(0)

    val isVisible: Boolean get() = _isVisible

    /** Monotonic counter bumped on each [show]; use as a LaunchedEffect key for auto-dismiss. */
    val showGeneration: Int get() = _showGeneration

    fun show() {
        _isVisible = true
        _showGeneration += 1
    }

    fun hide() {
        _isVisible = false
    }

    fun toggle() {
        if (_isVisible) hide() else show()
    }
}
