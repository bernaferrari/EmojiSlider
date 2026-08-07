package com.bernaferrari.emojislider

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue

internal data class EmojiSliderScales(
    val thumb: Float,
    val result: Float,
    val average: Float,
)

@Composable
internal fun animateEmojiSliderScales(
    isValueSelected: Boolean,
    isDragging: Boolean,
    allowReselection: Boolean,
    shouldDisplayResultPicture: Boolean,
    shouldDisplayAverage: Boolean,
    pressedThumbScale: Float,
): EmojiSliderScales {
    val thumb by animateFloatAsState(
        targetValue = when {
            isValueSelected && !allowReselection -> 0f
            isDragging -> pressedThumbScale.limitToRange()
            else -> 1f
        },
        animationSpec = spring(dampingRatio = 0.72f, stiffness = Spring.StiffnessMediumLow),
        label = "emoji_thumb_scale",
    )
    val result by animateFloatAsState(
        targetValue = if (isValueSelected && shouldDisplayResultPicture && !allowReselection) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.62f, stiffness = Spring.StiffnessLow),
        label = "result_scale",
    )
    val average by animateFloatAsState(
        targetValue = if (isValueSelected && shouldDisplayAverage && !allowReselection) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.62f, stiffness = Spring.StiffnessLow),
        label = "average_scale",
    )
    return EmojiSliderScales(thumb = thumb, result = result, average = average)
}
