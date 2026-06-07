package com.bernaferrari.emojislider.example

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bernaferrari.emojislider.EmojiSlider
import com.bernaferrari.emojislider.FloatingEmojiDirection
import com.bernaferrari.emojislider.generated.resources.Res as EmojiSliderRes
import com.bernaferrari.emojislider.generated.resources.noto_emoji_regular
import emojislider.example.generated.resources.Res as ExampleRes
import emojislider.example.generated.resources.github_mark
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
internal fun emojiFontFamily(): FontFamily = FontFamily(Font(EmojiSliderRes.font.noto_emoji_regular))

/** Renders any emoji/glyph with the bundled colour font (system arrows would otherwise tofu). */
@Composable
internal fun EmojiText(emoji: String, size: Dp, modifier: Modifier = Modifier) {
    Text(
        emoji,
        fontFamily = emojiFontFamily(),
        fontSize = size.value.sp,
        lineHeight = (size.value + 2).sp,
        modifier = modifier,
    )
}

@Composable
internal fun GitHubIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .pointerHoverIcon(PointerIcon.Hand)
            .semantics {
                contentDescription = "Open GitHub repository"
            },
    ) {
        Icon(
            painter = painterResource(ExampleRes.drawable.github_mark),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp),
        )
    }
}

/** Soft drifting aurora that subtly intensifies as the value rises — the room lights up as you drag. */
@Composable
internal fun ReactiveBackground(
    start: Color,
    end: Color,
    value: Float,
    modifier: Modifier = Modifier,
) {
    val infinite = rememberInfiniteTransition(label = "bg")
    val drift by infinite.animateFloat(
        initialValue = 0f,
        targetValue = (2f * kotlin.math.PI).toFloat(),
        animationSpec = infiniteRepeatable(tween(durationMillis = 20000, easing = LinearEasing)),
        label = "drift",
    )
    val warm by animateColorAsState(start, tween(600), label = "warm")
    val cool by animateColorAsState(end, tween(600), label = "cool")
    val base = MaterialTheme.colorScheme.background

    Canvas(modifier = modifier.fillMaxSize()) {
        drawRect(base)
        val w = size.width
        val h = size.height
        val energy = 0.12f + value * 0.16f
        val r = maxOf(w, h) * 0.6f
        drawGlow(warm.copy(alpha = energy + 0.04f), Offset(w * (0.10f + 0.06f * sin(drift)), h * (0.06f + 0.04f * sin(drift * 1.2f))), r)
        drawGlow(cool.copy(alpha = energy), Offset(w * (0.94f + 0.05f * sin(drift + 2f)), h * (0.16f + 0.05f * sin(drift * 0.8f))), r)
        drawGlow(blend(warm, cool, 0.5f).copy(alpha = energy - 0.03f), Offset(w * (0.5f + 0.10f * sin(drift * 0.6f)), h * 1.02f), r)
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawGlow(color: Color, center: Offset, radius: Float) {
    drawRect(brush = Brush.radialGradient(listOf(color, color.copy(alpha = 0f)), center = center, radius = radius))
}

/** Opaque card — no translucency means the soft shadow stays behind it (no inner-edge artifact). */
@Composable
internal fun SectionCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(30.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 3.dp,
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            content = content,
        )
    }
}

@Composable
internal fun SectionLabel(emoji: String, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        EmojiText(emoji, 18.dp)
        Text(
            text,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

/** Big, centred, gradient percentage. */
@Composable
internal fun HeroNumber(value: Float, start: Color, end: Color) {
    val gradient = Brush.horizontalGradient(listOf(start, end))
    Row(verticalAlignment = Alignment.Top) {
        Text(
            "${(value * 100).roundToInt()}",
            style = MaterialTheme.typography.displayLarge.copy(brush = gradient, fontFeatureSettings = "tnum"),
            fontWeight = FontWeight.Black,
            fontSize = 96.sp,
            lineHeight = 96.sp,
        )
        Text(
            "%",
            style = MaterialTheme.typography.headlineMedium.copy(brush = gradient),
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(top = 16.dp, start = 2.dp),
        )
    }
}

@Composable
internal fun EmojiChoice(
    emoji: String,
    selected: Boolean,
    accent: Brush,
    onClick: () -> Unit,
    size: Dp = 52.dp,
) {
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.12f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "emoji_scale",
    )
    Box(
        modifier = Modifier
            .size(size)
            .scale(scale)
            .clip(CircleShape)
            .then(if (selected) Modifier.background(accent) else Modifier.background(MaterialTheme.colorScheme.surfaceContainerHigh))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        EmojiText(emoji, size * 0.5f)
    }
}

/** Theme swatch: a rounded square that morphs into a checked circle when selected. Size never changes. */
@Composable
internal fun ColorSwatch(
    start: Color,
    end: Color,
    selected: Boolean,
    onClick: () -> Unit,
    size: Dp = 58.dp,
) {
    val corner by animateDpAsState(
        targetValue = if (selected) size / 2 else 18.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "swatch_corner",
    )
    val check by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "swatch_check",
    )
    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(corner))
            .background(Brush.linearGradient(listOf(start, end)))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (check > 0f) {
            Box(
                modifier = Modifier
                    .size(size * 0.52f)
                    .scale(check)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.28f)),
            )
            CheckMark(
                color = Color.White,
                modifier = Modifier.size(size * 0.42f).scale(check),
            )
        }
    }
}

@Composable
private fun CheckMark(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val s = size.minDimension
        val path = Path().apply {
            moveTo(0.24f * s, 0.52f * s)
            lineTo(0.43f * s, 0.70f * s)
            lineTo(0.78f * s, 0.32f * s)
        }
        drawPath(path, color, style = Stroke(width = s * 0.15f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

/** Expressive pill — gradient fill, a coloured glow, springy press, spinning die. */
@Composable
internal fun SurpriseButton(
    spinTrigger: Int,
    brush: Brush,
    glow: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "surprise_scale",
    )
    val spin by animateFloatAsState(
        targetValue = spinTrigger * 360f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessLow),
        label = "dice_spin",
    )
    Surface(
        onClick = onClick,
        interactionSource = interaction,
        modifier = modifier
            .scale(scale)
            .shadow(16.dp, RoundedCornerShape(50), spotColor = glow, ambientColor = glow),
        shape = RoundedCornerShape(50),
        color = Color.Transparent,
    ) {
        Box(
            modifier = Modifier.background(brush).padding(vertical = 18.dp),
            contentAlignment = Alignment.Center,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                EmojiText("🎲", 20.dp, modifier = Modifier.rotate(spin))
                Text("Surprise me", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

/** Settings-style row — the WHOLE row toggles. Dims and locks when [enabled] is false. */
@Composable
internal fun SwitchRow(
    emoji: String,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true,
) {
    val rowAlpha by animateFloatAsState(if (enabled) 1f else 0.45f, tween(200), label = "row_alpha")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 52.dp)
            .clip(RoundedCornerShape(16.dp))
            .then(if (enabled) Modifier.clickable { onCheckedChange(!checked) } else Modifier)
            .padding(horizontal = 6.dp)
            .alpha(rowAlpha),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        EmojiText(emoji, 18.dp)
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            colors = SwitchDefaults.colors(checkedTrackColor = MaterialTheme.colorScheme.primary),
        )
    }
}

/** Segmented control with a single pill that springs/slides between options — no colour flash. */
@Composable
internal fun SlidingSegmented(
    options: List<Pair<String, String>>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val count = options.size.coerceAtLeast(1)
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
    ) {
        BoxWithConstraints(modifier = Modifier.padding(4.dp).height(44.dp)) {
            val itemWidth = maxWidth / count
            val pillOffset by animateDpAsState(
                targetValue = itemWidth * selectedIndex,
                animationSpec = spring(dampingRatio = 0.78f, stiffness = Spring.StiffnessMediumLow),
                label = "pill_offset",
            )
            // The single sliding pill.
            Box(
                modifier = Modifier
                    .offset(x = pillOffset)
                    .width(itemWidth)
                    .fillMaxHeight()
                    .padding(2.dp)
                    .shadow(2.dp, RoundedCornerShape(14.dp))
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surface),
            )
            Row(modifier = Modifier.fillMaxSize()) {
                options.forEachIndexed { index, (emoji, label) ->
                    val selected = index == selectedIndex
                    val itemInteraction = remember { MutableInteractionSource() }
                    val hovered by itemInteraction.collectIsHoveredAsState()
                    val fg by animateColorAsState(
                        targetValue = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        animationSpec = tween(180),
                        label = "seg_fg",
                    )
                    // Subtle darker tint when hovering an unselected segment (the selected one is the pill).
                    val hoverBg by animateColorAsState(
                        targetValue = if (hovered && !selected) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f) else Color.Transparent,
                        animationSpec = tween(140),
                        label = "seg_hover",
                    )
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(14.dp))
                            .background(hoverBg)
                            .clickable(
                                interactionSource = itemInteraction,
                                indication = null,
                            ) { onSelect(index) },
                        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        EmojiText(emoji, 15.dp)
                        Text(
                            label,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                            color = fg,
                            maxLines = 1,
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun ValueSliderRow(
    title: String,
    value: Float,
    accent: Color,
    onValueChange: (Float) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(title, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                "${(value * 100).roundToInt()}%",
                style = MaterialTheme.typography.titleSmall.copy(fontFeatureSettings = "tnum"),
                fontWeight = FontWeight.Bold,
                color = accent,
            )
        }
        Slider(value = value, onValueChange = onValueChange, colors = SliderDefaults.colors(thumbColor = accent, activeTrackColor = accent))
    }
}

/** Secondary pill that re-casts a locked-in one-shot vote. Sized to sit beside [SurpriseButton]. */
@Composable
internal fun VoteAgainButton(accent: Color, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.surfaceContainerHighest,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            EmojiText("🔄", 16.dp)
            Text("Vote again", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = accent)
        }
    }
}

@Composable
internal fun PreviewSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    emoji: String,
    start: Color,
    end: Color,
    modifier: Modifier = Modifier,
    showAverage: Boolean = false,
    averageValue: Float = 0.5f,
    allowReselection: Boolean = true,
    floatingDirection: FloatingEmojiDirection = FloatingEmojiDirection.UP,
    onStopTracking: () -> Unit = {},
) {
    EmojiSlider(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        onStopTracking = onStopTracking,
        emoji = emoji,
        colorStart = start,
        colorEnd = end,
        colorTrack = blend(start, end, 0.86f).copy(alpha = 0.16f),
        activeTrackGradient = Brush.horizontalGradient(listOf(start, end)),
        averageProgressValue = averageValue,
        shouldDisplayAverage = showAverage,
        shouldDisplayTooltip = showAverage,
        allowReselection = allowReselection,
        floatingDirection = floatingDirection,
        trackInset = 0.dp,
    )
}

internal fun blend(start: Color, end: Color, fraction: Float): Color {
    val t = fraction.coerceIn(0f, 1f)
    return Color(
        red = start.red + (end.red - start.red) * t,
        green = start.green + (end.green - start.green) * t,
        blue = start.blue + (end.blue - start.blue) * t,
        alpha = start.alpha + (end.alpha - start.alpha) * t,
    )
}
