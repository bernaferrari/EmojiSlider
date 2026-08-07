package com.bernaferrari.emojislider

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

internal fun DrawScope.drawFloatingEmoji(
    emoji: String,
    position: Offset,
    size: Float,
    alpha: Float = 1f,
    rotation: Float = 0f,
    textMeasurer: TextMeasurer,
    emojiFontFamily: FontFamily,
) {
    val layout = textMeasurer.measure(
        text = AnnotatedString(emoji),
        style = TextStyle(
            fontSize = (size * 0.8f / density).sp,
            fontFamily = emojiFontFamily,
            fontWeight = FontWeight.Normal,
        ),
    )
    val topLeft = Offset(
        x = position.x - layout.size.width / 2f,
        y = position.y - layout.size.height / 2f,
    )
    val bounds = Rect(
        left = topLeft.x,
        top = topLeft.y,
        right = topLeft.x + layout.size.width,
        bottom = topLeft.y + layout.size.height,
    )
    val paint = Paint().apply { this.alpha = alpha.coerceIn(0f, 1f) }

    drawContext.canvas.saveLayer(bounds, paint)
    if (rotation != 0f) {
        rotate(rotation, pivot = position) {
            drawText(textLayoutResult = layout, topLeft = topLeft, alpha = 1f)
        }
    } else {
        drawText(textLayoutResult = layout, topLeft = topLeft, alpha = 1f)
    }
    drawContext.canvas.restore()
}
