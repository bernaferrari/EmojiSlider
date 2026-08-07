package com.bernaferrari.emojislider

import androidx.compose.ui.geometry.Offset

/**
 * Clamps to `0f..1f`. Maps non-finite values (`NaN`, ±∞) to a safe range endpoint:
 * `NaN` → `0f`, `+∞` → `1f`, `-∞` → `0f`.
 */
internal fun Float.limitToRange(): Float {
    if (isNaN()) return 0f
    return coerceIn(0f, 1f)
}

internal fun Offset.distanceSquaredTo(other: Offset): Float {
    val dx = x - other.x
    val dy = y - other.y
    return dx * dx + dy * dy
}
