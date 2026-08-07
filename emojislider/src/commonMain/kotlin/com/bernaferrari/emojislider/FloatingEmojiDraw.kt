package com.bernaferrari.emojislider

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

internal fun DrawScope.drawCenteredEmoji(
    emoji: String,
    center: Offset,
    size: Float,
    textMeasurer: TextMeasurer,
    emojiFontFamily: FontFamily,
    alpha: Float = 1f,
    rotation: Float = 0f,
    glyphScale: Float = 0.8f,
) {
    val layout = textMeasurer.measure(
        text = AnnotatedString(emoji),
        style = TextStyle(
            fontSize = (size * glyphScale / density).sp,
            fontFamily = emojiFontFamily,
            fontWeight = FontWeight.Normal,
        ),
    )
    val topLeft = Offset(
        x = center.x - layout.size.width / 2f,
        y = center.y - layout.size.height / 2f,
    )
    val drawAlpha = alpha.coerceIn(0f, 1f)
    if (rotation == 0f) {
        drawText(textLayoutResult = layout, topLeft = topLeft, alpha = drawAlpha)
        return
    }
    rotate(rotation, pivot = center) {
        drawText(textLayoutResult = layout, topLeft = topLeft, alpha = drawAlpha)
    }
}
