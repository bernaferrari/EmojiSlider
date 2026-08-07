package com.bernaferrari.emojislider

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

internal fun DrawScope.drawEmojiSlider(
    progress: Float,
    geometry: SliderGeometry,
    emoji: String,
    activeTrackGradient: Brush,
    colorStart: Color,
    colorEnd: Color,
    colorTrack: Color,
    averageProgress: Float,
    shouldDisplayAverage: Boolean,
    shouldDisplayResult: Boolean,
    averageScale: Float,
    resultScale: Float,
    thumbScale: Float,
    resultBitmap: ImageBitmap?,
    textMeasurer: TextMeasurer,
    emojiFontFamily: FontFamily,
    isDragging: Boolean,
    allowReselection: Boolean,
) {
    val trackTopLeft = Offset(
        x = geometry.trackStart,
        y = geometry.centerY - geometry.trackHeight / 2f,
    )

    drawRoundRect(
        color = colorTrack,
        topLeft = trackTopLeft,
        size = Size(geometry.trackWidth, geometry.trackHeight),
        cornerRadius = CornerRadius(geometry.trackHeight / 2f),
    )

    val progressWidth = progress.limitToRange() * geometry.trackWidth
    if (progressWidth > 0f) {
        drawRoundRect(
            brush = activeTrackGradient,
            topLeft = trackTopLeft,
            size = Size(progressWidth, geometry.trackHeight),
            cornerRadius = CornerRadius(geometry.trackHeight / 2f),
        )
    }

    val thumbCenter = geometry.thumbCenter(progress)
    if (shouldDisplayResult && resultScale > 0f && !allowReselection) {
        drawResult(
            center = thumbCenter,
            radius = geometry.thumbSize / 2f * resultScale,
            progress = progress,
            colorStart = colorStart,
            colorEnd = colorEnd,
            resultBitmap = resultBitmap,
        )
    }

    if (shouldDisplayAverage && averageScale > 0f && !allowReselection) {
        val averageCenter = geometry.thumbCenter(averageProgress)
        drawAverageIndicator(
            center = averageCenter,
            outerColor = lerp(colorStart, colorEnd, averageProgress),
            scale = averageScale,
        )
    }

    if (thumbScale > 0f) {
        drawCenteredEmoji(
            emoji = emoji,
            center = thumbCenter,
            size = geometry.thumbSize * thumbScale,
            textMeasurer = textMeasurer,
            emojiFontFamily = emojiFontFamily,
            alpha = if (isDragging) 0.82f else 1f,
            glyphScale = 0.86f,
        )
    }
}

internal fun DrawScope.drawResult(
    center: Offset,
    radius: Float,
    progress: Float,
    colorStart: Color,
    colorEnd: Color,
    resultBitmap: ImageBitmap?,
) {
    val resultColor = lerp(colorStart, colorEnd, progress.limitToRange())

    drawCircle(
        color = resultColor.copy(alpha = 0.20f),
        radius = radius * 1.34f,
        center = center,
    )

    if (resultBitmap == null) {
        drawCircle(
            color = resultColor,
            radius = radius,
            center = center,
        )
        return
    }

    val clip = Path().apply {
        addOval(
            androidx.compose.ui.geometry.Rect(
                left = center.x - radius,
                top = center.y - radius,
                right = center.x + radius,
                bottom = center.y + radius,
            ),
        )
    }

    clipPath(clip) {
        drawImage(
            image = resultBitmap,
            dstOffset = IntOffset((center.x - radius).roundToInt(), (center.y - radius).roundToInt()),
            dstSize = IntSize((radius * 2f).roundToInt(), (radius * 2f).roundToInt()),
        )
    }
}

internal fun DrawScope.drawAverageIndicator(
    center: Offset,
    outerColor: Color,
    scale: Float,
) {
    val radius = 10.dp.toPx() * scale
    val ringThickness = 2.dp.toPx() * scale
    if (radius <= 0f) return

    drawCircle(
        color = outerColor,
        radius = radius,
        center = center,
    )
    drawCircle(
        color = Color.White,
        radius = (radius - ringThickness).coerceAtLeast(0f),
        center = center,
    )
}
