package com.bernaferrari.emojislidersample.extensions

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue


internal fun pxToDp(value: Int, resources: Resources): Int {
    return (resources.displayMetrics.density * value).toInt()
}

internal fun dpToPixels(value: Float, context: Context): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        value,
        context.resources.displayMetrics
    )
}

/**
 * Map a value within a given range to another range.
 * @param value the value to map
 * @param fromLow the low end of the range the value is within
 * @param fromHigh the high end of the range the value is within
 * @param toLow the low end of the range to map to
 * @param toHigh the high end of the range to map to
 * @return the mapped value
 */
internal fun Float.mapValueFromRangeToRange(
    fromLow: Float,
    fromHigh: Float,
    toLow: Float,
    toHigh: Float
): Float {
    val fromRangeSize = fromHigh - fromLow
    val toRangeSize = toHigh - toLow
    val valueScale = (this - fromLow) / fromRangeSize
    return toLow + valueScale * toRangeSize
}
