package com.bernaferrari.emojislider2

import android.os.Build.VERSION
import android.text.Layout
import android.text.StaticLayout

object TextDrawableHelper {

    fun getMaxLineWidth(layout: Layout?): Int {
        if (layout == null) {
            return 0
        }

        var maxWidth = 0
        for (i in 0..layout.lineCount) {
            maxWidth =
                    Math.max(maxWidth, Math.round(layout.getLineRight(i) - layout.getLineLeft(i)))
        }

        return maxWidth
    }

    fun getHeight(layout: Layout?): Int {
        if (layout == null) {
            return 0
        }

        var i = 0
        if (VERSION.SDK_INT <= 19 && layout is StaticLayout) {
            var lineDescent =
                (layout.getLineDescent(layout.lineCount - 1) - layout.getLineAscent(layout.lineCount - 1)).toFloat()
            lineDescent -= (lineDescent - layout.spacingAdd) / layout.spacingMultiplier
            i =
                    if (lineDescent >= 0.0f) (lineDescent.toDouble() + 0.5).toInt() else -((-lineDescent).toDouble() + 0.5).toInt()
        }
        return layout.height - i
    }

    fun getMinLineLeft(layout: Layout?): Int {
        if (layout == null || layout.lineCount == 0) {
            return 0
        }

        var minLineLeft = Integer.MAX_VALUE
        for (i in 0..layout.lineCount) {
            minLineLeft = Math.min(minLineLeft, layout.getLineLeft(i).toInt())
        }
        return minLineLeft
    }
}