package com.bernaferrari.emojislider

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/** Show/hide holder for the average tooltip. [showGeneration] restarts auto-dismiss. */
internal class TooltipState {
    private var _isVisible by mutableStateOf(false)
    private var _showGeneration by mutableIntStateOf(0)

    val isVisible: Boolean get() = _isVisible
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
