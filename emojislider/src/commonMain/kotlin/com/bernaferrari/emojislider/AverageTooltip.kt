package com.bernaferrari.emojislider

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
internal fun AverageTooltip(
    text: String,
    anchorX: Float,
    autoDismissDelay: Long,
    onDismiss: () -> Unit,
    /** Bumps when the tooltip is (re)shown so auto-dismiss restarts for each session. */
    showGeneration: Int = 0,
) {
    val density = LocalDensity.current
    val tooltipColor = MaterialTheme.colorScheme.inverseSurface
    var containerSize by remember { mutableStateOf(IntSize.Zero) }
    var tooltipSize by remember { mutableStateOf(IntSize.Zero) }

    LaunchedEffect(showGeneration, autoDismissDelay) {
        delay(autoDismissDelay)
        onDismiss()
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(46.dp)
            .onSizeChanged { containerSize = it },
    ) {
        val fallbackTooltipWidth = with(density) { 132.dp.toPx() }
        val currentTooltipWidth = tooltipSize.width.takeIf { it > 0 }?.toFloat() ?: fallbackTooltipWidth
        val maxTooltipX = (containerSize.width - currentTooltipWidth).coerceAtLeast(0f)
        val tooltipX = (anchorX - currentTooltipWidth / 2f).coerceIn(0f, maxTooltipX)

        Surface(
            modifier = Modifier
                .testTag(EMOJI_SLIDER_TOOLTIP_TEST_TAG)
                .offset { IntOffset(tooltipX.roundToInt(), 0) }
                .size(width = 132.dp, height = 34.dp)
                .onSizeChanged { tooltipSize = it },
            shape = MaterialTheme.shapes.medium,
            color = tooltipColor,
            tonalElevation = 6.dp,
            shadowElevation = 6.dp,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = text.ifBlank { "Average value" },
                    color = MaterialTheme.colorScheme.inverseOnSurface,
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1,
                )
            }
        }

        Canvas(
            modifier = Modifier
                .offset {
                    val arrowHalfWidth = with(density) { 7.dp.toPx() }
                    val arrowX = (anchorX - arrowHalfWidth).coerceIn(
                        0f,
                        (containerSize.width - arrowHalfWidth * 2f).coerceAtLeast(0f),
                    )
                    IntOffset(arrowX.roundToInt(), with(density) { 30.dp.toPx() }.roundToInt())
                }
                .size(width = 14.dp, height = 8.dp),
        ) {
            val arrow = Path().apply {
                moveTo(size.width / 2f, size.height)
                lineTo(0f, 0f)
                lineTo(size.width, 0f)
                close()
            }
            drawPath(arrow, tooltipColor)
        }
    }
}
