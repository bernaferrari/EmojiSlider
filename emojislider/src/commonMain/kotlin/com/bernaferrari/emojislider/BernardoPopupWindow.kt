package com.bernaferrari.emojislider

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/**
 * Enhanced tooltip component matching the original BubbleTextView behavior
 */
@Composable
fun EmojiSliderTooltip(
    modifier: Modifier = Modifier,
    visible: Boolean,
    text: String = "Average value",
    autoDismissDelay: Long = 2500L,
    onDismiss: () -> Unit = {},
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessHigh,
            ),
        ) + scaleIn(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessHigh,
            ),
        ) + slideInVertically(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessHigh,
            ),
            initialOffsetY = { it / 4 },
        ),
        exit = fadeOut(
            animationSpec = tween(200),
        ) + scaleOut(
            animationSpec = tween(200),
        ) + slideOutVertically(
            animationSpec = tween(200),
            targetOffsetY = { -it / 4 },
        ),
        modifier = modifier,
    ) {
        BubbleTooltip(text = text)

        // Auto-dismiss after delay (matching original behavior)
        LaunchedEffect(visible) {
            if (visible && autoDismissDelay > 0) {
                delay(autoDismissDelay)
                onDismiss()
            }
        }
    }
}

@Composable
private fun BubbleTooltip(
    text: String,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        // Main bubble content
        Card(
            modifier = Modifier.shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(12.dp),
            ),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.inverseSurface,
            ),
        ) {
            Text(
                text = text,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                color = MaterialTheme.colorScheme.inverseOnSurface,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
            )
        }

        // Arrow pointing down (matching original bubble style)
        Box(
            modifier = Modifier
                .size(16.dp, 8.dp)
                .offset(y = (-1).dp)
                .clip(
                    androidx.compose.foundation.shape.GenericShape { size, _ ->
                        moveTo(size.width / 2f, size.height)
                        lineTo(0f, 0f)
                        lineTo(size.width, 0f)
                        close()
                    },
                )
                .background(MaterialTheme.colorScheme.inverseSurface),
        )
    }
}

/**
 * Hook for managing tooltip state (enhanced to match original)
 */
@Composable
fun rememberTooltipState(): TooltipState = remember { TooltipState() }

class TooltipState {
    private var _isVisible by mutableStateOf(false)

    val isVisible: Boolean get() = _isVisible

    fun show() {
        _isVisible = true
    }

    fun hide() {
        _isVisible = false
    }

    fun toggle() {
        _isVisible = !_isVisible
    }
}
