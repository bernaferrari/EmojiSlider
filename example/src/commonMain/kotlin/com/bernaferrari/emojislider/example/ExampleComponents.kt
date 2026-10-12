@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.bernaferrari.emojislider.example

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ripple
import androidx.compose.material3.toPath
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.graphics.shapes.Morph
import com.bernaferrari.emojislider.EmojiSlider
import com.bernaferrari.emojislider.EmojiSliderBehavior
import com.bernaferrari.emojislider.EmojiSliderColors
import com.bernaferrari.emojislider.FloatingEmojiDirection
import com.bernaferrari.emojislider.generated.resources.Res as EmojiSliderRes
import com.bernaferrari.emojislider.generated.resources.noto_emoji_regular
import emojislider.example.generated.resources.Res as ExampleRes
import emojislider.example.generated.resources.github_mark
import kotlin.math.PI
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource

@Composable
internal fun emojiFontFamily(): FontFamily = FontFamily(Font(EmojiSliderRes.font.noto_emoji_regular))

/** Renders an emoji with the bundled colour font (the web target has no system emoji font). */
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

// ── Shapes ──

/** A [Shape] that draws [morph] at [progress], scaled to the layout bounds. */
private class MorphShape(private val morph: Morph, private val progress: Float) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val path = morph.toPath(progress)
        path.transform(Matrix().apply { scale(size.width, size.height) })
        return Outline.Generic(path)
    }
}

private val circleToCookie by lazy { Morph(MaterialShapes.Circle, MaterialShapes.Cookie9Sided) }
private val circleToSunny by lazy { Morph(MaterialShapes.Circle, MaterialShapes.Sunny) }

// ── Chrome ──

@Composable
internal fun Wordmark(emoji: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(MaterialShapes.Cookie12Sided.toShape())
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            EmojiText(emoji, 18.dp)
        }
        Text(
            "EmojiSlider",
            style = MaterialTheme.typography.titleLargeEmphasized,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
internal fun GitHubButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    FilledTonalButton(
        onClick = onClick,
        shapes = ButtonDefaults.shapes(),
        modifier = modifier.pointerHoverIcon(PointerIcon.Hand),
    ) {
        Icon(
            painter = painterResource(ExampleRes.drawable.github_mark),
            contentDescription = null,
            modifier = Modifier.size(ButtonDefaults.IconSize),
        )
        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
        Text("GitHub")
    }
}

/** Sun/moon tonal icon button; the glyphs swap with a quarter turn. */
@Composable
internal fun ThemeToggle(isDark: Boolean, onToggle: () -> Unit, modifier: Modifier = Modifier) {
    val spin = MaterialTheme.motionScheme.defaultSpatialSpec<Float>()
    FilledTonalIconButton(
        onClick = onToggle,
        shapes = IconButtonDefaults.shapes(),
        modifier = modifier
            .pointerHoverIcon(PointerIcon.Hand)
            .semantics { contentDescription = if (isDark) "Switch to light theme" else "Switch to dark theme" },
    ) {
        AnimatedContent(
            targetState = isDark,
            transitionSpec = {
                (fadeIn() + scaleIn(spin, initialScale = 0.5f))
                    .togetherWith(fadeOut() + scaleOut(targetScale = 0.5f))
            },
            label = "theme_icon",
        ) { dark ->
            val rotation by animateFloatAsState(if (dark) 0f else 90f, spin, label = "theme_spin")
            val iconModifier = Modifier.size(20.dp).graphicsLayer { rotationZ = rotation }
            // Show what you get now: the moon in dark mode, the sun in light mode.
            if (dark) MoonIcon(iconModifier) else SunIcon(iconModifier)
        }
    }
}

/** Two slow, low-alpha glows in the gradient colours over the themed surface. */
@Composable
internal fun AmbientBackground(
    start: Color,
    end: Color,
    value: Float,
    modifier: Modifier = Modifier,
) {
    val drift by rememberInfiniteTransition(label = "bg").animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(tween(durationMillis = 24000, easing = LinearEasing)),
        label = "drift",
    )
    val base = MaterialTheme.colorScheme.surface

    Canvas(modifier = modifier.fillMaxSize()) {
        drawRect(base)
        val w = size.width
        val h = size.height
        val alpha = 0.10f + value * 0.08f
        val r = maxOf(w, h) * 0.55f
        drawGlow(start.copy(alpha = alpha), Offset(w * (0.12f + 0.05f * sin(drift)), h * (0.02f + 0.04f * sin(drift * 1.3f))), r)
        drawGlow(end.copy(alpha = alpha * 0.8f), Offset(w * (0.92f + 0.04f * sin(drift + 2f)), h * (0.10f + 0.05f * sin(drift * 0.7f))), r)
    }
}

private fun DrawScope.drawGlow(color: Color, center: Offset, radius: Float) {
    drawRect(brush = Brush.radialGradient(listOf(color, color.copy(alpha = 0f)), center = center, radius = radius))
}

// ── Containers ──

@Composable
internal fun Panel(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            content = content,
        )
    }
}

@Composable
internal fun ControlSection(
    title: String,
    trailing: String? = null,
    content: @Composable () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
            )
            if (trailing != null) {
                Text(trailing, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            }
        }
        content()
    }
}

/** The hero: big value readout, the slider, and the primary action. */
@Composable
internal fun StagePanel(
    value: Float,
    locked: Boolean,
    onShuffle: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier,
    slider: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.extraLargeIncreased,
        color = MaterialTheme.colorScheme.surfaceContainer,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // Fixed height so the row doesn't jump when "Vote again" (with its 48dp touch target) appears.
            Row(modifier = Modifier.fillMaxWidth().height(48.dp), verticalAlignment = Alignment.CenterVertically) {
                StatusChip(locked = locked)
                Spacer(Modifier.weight(1f))
                AnimatedVisibility(
                    visible = locked,
                    enter = fadeIn() + scaleIn(MaterialTheme.motionScheme.fastSpatialSpec()),
                    exit = fadeOut() + scaleOut(),
                ) {
                    FilledTonalButton(
                        onClick = onReset,
                        shapes = ButtonDefaults.shapes(),
                        modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                    ) { Text("Vote again") }
                }
            }
            Spacer(Modifier.weight(1f))
            ValueReadout(value = value, modifier = Modifier.align(Alignment.CenterHorizontally))
            Spacer(Modifier.weight(1f))
            slider()
            Spacer(Modifier.size(8.dp))
            Button(
                onClick = onShuffle,
                shapes = ButtonDefaults.shapes(),
                contentPadding = ButtonDefaults.contentPaddingFor(ButtonDefaults.MediumContainerHeight),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = ButtonDefaults.MediumContainerHeight)
                    .pointerHoverIcon(PointerIcon.Hand),
            ) {
                Text("Shuffle", style = ButtonDefaults.textStyleFor(ButtonDefaults.MediumContainerHeight))
            }
        }
    }
}

@Composable
private fun StatusChip(locked: Boolean) {
    val container by animateColorAsState(
        if (locked) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.surfaceContainerHighest,
        label = "chip",
    )
    val content = if (locked) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
    // Same height as the "Vote again" button beside it.
    Surface(
        shape = RoundedCornerShape(50),
        color = container,
        contentColor = content,
        modifier = Modifier.height(ButtonDefaults.MinHeight),
    ) {
        Box(modifier = Modifier.padding(horizontal = 16.dp), contentAlignment = Alignment.Center) {
            Text(
                if (locked) "Vote locked" else "Drag, then release",
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}

@Composable
private fun ValueReadout(value: Float, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.Top) {
        Text(
            "${(value * 100).roundToInt()}",
            style = MaterialTheme.typography.displayLargeEmphasized.copy(fontFeatureSettings = "tnum"),
            fontSize = 112.sp,
            lineHeight = 112.sp,
            letterSpacing = (-4).sp,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            "%",
            style = MaterialTheme.typography.headlineMediumEmphasized,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 18.dp, start = 4.dp),
        )
    }
}

// ── Pickers ──

/** Circle that morphs into a cookie when selected, using the expressive spatial spring. */
@Composable
internal fun EmojiChoice(
    emoji: String,
    selected: Boolean,
    onClick: () -> Unit,
    size: Dp = 48.dp,
    description: String = emoji,
) {
    val progress by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec(),
        label = "emoji_morph",
    )
    val container by animateColorAsState(
        if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHighest,
        MaterialTheme.motionScheme.defaultEffectsSpec(),
        label = "emoji_container",
    )
    val shape = MorphShape(circleToCookie, progress)
    Box(
        modifier = Modifier
            .size(size)
            .clip(shape)
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton,
                interactionSource = null,
                indication = ripple(),
            )
            .pointerHoverIcon(PointerIcon.Hand)
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier
                .matchParentSize()
                .graphicsLayer { rotationZ = progress * 20f }
                .background(container, shape),
        )
        EmojiText(emoji, size * 0.58f)
    }
}

/** Gradient circle that morphs into a sunny shape when selected. */
@Composable
internal fun ColorSwatch(
    start: Color,
    end: Color,
    name: String,
    selected: Boolean,
    onClick: () -> Unit,
    size: Dp = 44.dp,
) {
    val progress by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec(),
        label = "swatch_morph",
    )
    val shape = MorphShape(circleToSunny, progress)
    Box(
        modifier = Modifier
            .size(size)
            .graphicsLayer { rotationZ = progress * 30f }
            .clip(shape)
            .background(Brush.linearGradient(listOf(start, end)))
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton,
                interactionSource = null,
                indication = ripple(color = Color.White),
            )
            .pointerHoverIcon(PointerIcon.Hand)
            .semantics { contentDescription = name },
        contentAlignment = Alignment.Center,
    ) {
        if (progress > 0.01f) {
            CheckMark(
                color = Color.White,
                modifier = Modifier
                    .size(size * 0.4f)
                    .graphicsLayer {
                        rotationZ = -progress * 30f
                        scaleX = progress
                        scaleY = progress
                    },
            )
        }
    }
}

/** M3 Expressive connected button group: shapes morph on press and when checked. */
@Composable
internal fun ConnectedToggleGroup(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    icons: List<@Composable () -> Unit> = emptyList(),
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
    ) {
        options.forEachIndexed { index, label ->
            ToggleButton(
                checked = index == selectedIndex,
                onCheckedChange = { onSelect(index) },
                shapes = when (index) {
                    0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                    options.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                    else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                },
                modifier = Modifier
                    .weight(1f)
                    .pointerHoverIcon(PointerIcon.Hand)
                    .semantics { role = Role.RadioButton },
            ) {
                icons.getOrNull(index)?.let {
                    it()
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                }
                Text(label, maxLines = 1)
            }
        }
    }
}

internal enum class ListPosition { Single, First, Middle, Last }

/** Grouped list item: large outer corners, small inner corners, 2dp gaps — the expressive settings look. */
@Composable
private fun listItemShape(position: ListPosition): Shape {
    val outer = 24.dp
    val inner = 4.dp
    val top by animateDpAsState(
        if (position == ListPosition.Single || position == ListPosition.First) outer else inner,
        MaterialTheme.motionScheme.fastSpatialSpec(),
        label = "top_corner",
    )
    val bottom by animateDpAsState(
        if (position == ListPosition.Single || position == ListPosition.Last) outer else inner,
        MaterialTheme.motionScheme.fastSpatialSpec(),
        label = "bottom_corner",
    )
    return RoundedCornerShape(topStart = top, topEnd = top, bottomStart = bottom, bottomEnd = bottom)
}

@Composable
internal fun SwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    position: ListPosition = ListPosition.Single,
) {
    Surface(
        shape = listItemShape(position),
        color = MaterialTheme.colorScheme.surfaceContainerHighest,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .toggleable(value = checked, onValueChange = onCheckedChange, role = Role.Switch)
                .pointerHoverIcon(PointerIcon.Hand)
                .heightIn(min = 64.dp)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
                Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Switch(
                checked = checked,
                onCheckedChange = null,
                thumbContent = {
                    if (checked) {
                        CheckMark(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(SwitchDefaults.IconSize),
                        )
                    }
                },
            )
        }
    }
}

@Composable
internal fun ValueSliderRow(
    title: String,
    value: Float,
    onValueChange: (Float) -> Unit,
) {
    Surface(
        shape = listItemShape(ListPosition.Last),
        color = MaterialTheme.colorScheme.surfaceContainerHighest,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(title, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                Text(
                    "${(value * 100).roundToInt()}%",
                    style = MaterialTheme.typography.labelLarge.copy(fontFeatureSettings = "tnum"),
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            Slider(value = value, onValueChange = onValueChange)
        }
    }
}

// ── Code ──

@Composable
internal fun CodePanel(code: String, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    val emojiFont = emojiFontFamily()
    val highlighted = remember(code, colors, emojiFont) {
        highlightKotlin(code, key = colors.primary, string = colors.tertiary, plain = colors.onSurface, emojiFont = emojiFont)
    }
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Code", style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
                Text("Kotlin", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Surface(
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Box {
                    SelectionContainer {
                        Text(
                            highlighted,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp,
                            lineHeight = 20.sp,
                            softWrap = false,
                            // End padding keeps the first lines clear of the copy button.
                            modifier = Modifier
                                .horizontalScroll(rememberScrollState())
                                .padding(start = 16.dp, top = 16.dp, bottom = 16.dp, end = 64.dp),
                        )
                    }
                    CopyButton(text = code, modifier = Modifier.align(Alignment.TopEnd).padding(8.dp))
                }
            }
        }
    }
}

/** Tonal icon button that morphs on press and swaps to a check for a moment after copying. */
@Composable
private fun CopyButton(text: String, modifier: Modifier = Modifier) {
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()
    var copied by remember { mutableStateOf(false) }
    val container by animateColorAsState(
        if (copied) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer,
        MaterialTheme.motionScheme.defaultEffectsSpec(),
        label = "copy_container",
    )
    val content by animateColorAsState(
        if (copied) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer,
        MaterialTheme.motionScheme.defaultEffectsSpec(),
        label = "copy_content",
    )
    val iconSpec = MaterialTheme.motionScheme.fastSpatialSpec<Float>()
    FilledTonalIconButton(
        onClick = {
            scope.launch {
                clipboard.setClipEntry(plainTextClipEntry(text))
                copied = true
                delay(1600)
                copied = false
            }
        },
        shapes = IconButtonDefaults.shapes(),
        colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = container, contentColor = content),
        modifier = modifier
            .pointerHoverIcon(PointerIcon.Hand)
            .semantics { contentDescription = if (copied) "Copied" else "Copy code" },
    ) {
        AnimatedContent(
            targetState = copied,
            transitionSpec = {
                (fadeIn() + scaleIn(iconSpec, initialScale = 0.6f))
                    .togetherWith(fadeOut() + scaleOut(targetScale = 0.6f))
            },
            label = "copy_icon",
        ) { done ->
            if (done) {
                CheckMark(color = LocalContentColor.current, modifier = Modifier.size(20.dp))
            } else {
                CopyIcon(modifier = Modifier.size(20.dp))
            }
        }
    }
}

/** Tiny highlighter for the generated snippet: argument names, string literals, emoji in the colour font. */
private fun highlightKotlin(code: String, key: Color, string: Color, plain: Color, emojiFont: FontFamily): AnnotatedString =
    buildAnnotatedString {
        code.lines().forEachIndexed { i, line ->
            if (i > 0) append('\n')
            val eq = line.indexOf(" = ")
            val keyEnd = if (eq > 0 && line.substring(0, eq).isBlank().not() && line.substring(0, eq).trim().all { it.isLetterOrDigit() }) eq else 0
            if (keyEnd > 0) {
                val indent = line.takeWhile { it == ' ' }
                append(indent)
                withStyle(SpanStyle(color = key)) { append(line.substring(indent.length, keyEnd)) }
            }
            var rest = line.substring(keyEnd)
            while (rest.isNotEmpty()) {
                val q = rest.indexOf('"')
                val close = if (q >= 0) rest.indexOf('"', q + 1) else -1
                if (q < 0 || close < 0) {
                    withStyle(SpanStyle(color = plain)) { append(rest) }
                    break
                }
                withStyle(SpanStyle(color = plain)) { append(rest.substring(0, q)) }
                withStyle(SpanStyle(color = string)) {
                    append('"')
                    withStyle(SpanStyle(fontFamily = emojiFont)) { append(rest.substring(q + 1, close)) }
                    append('"')
                }
                rest = rest.substring(close + 1)
            }
        }
    }

// ── Glyphs ──

@Composable
private fun CheckMark(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val s = size.minDimension
        val path = Path().apply {
            moveTo(0.22f * s, 0.52f * s)
            lineTo(0.42f * s, 0.72f * s)
            lineTo(0.80f * s, 0.30f * s)
        }
        drawPath(path, color, style = Stroke(width = s * 0.14f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
private fun CopyIcon(modifier: Modifier = Modifier) {
    val color = LocalContentColor.current
    Canvas(modifier = modifier) {
        val s = size.minDimension
        val stroke = Stroke(width = s * 0.09f)
        val corner = CornerRadius(s * 0.12f)
        // Back sheet, then the front sheet on top.
        drawRoundRect(color, topLeft = Offset(s * 0.30f, s * 0.12f), size = Size(s * 0.52f, s * 0.58f), cornerRadius = corner, style = stroke)
        drawRoundRect(color, topLeft = Offset(s * 0.16f, s * 0.30f), size = Size(s * 0.52f, s * 0.58f), cornerRadius = corner, style = Fill)
    }
}

@Composable
private fun SunIcon(modifier: Modifier = Modifier) {
    val color = LocalContentColor.current
    Canvas(modifier = modifier) {
        val s = size.minDimension
        val c = Offset(s / 2f, s / 2f)
        drawCircle(color, radius = s * 0.2f, center = c)
        repeat(8) { i ->
            val angle = i * PI.toFloat() / 4f
            val dir = Offset(kotlin.math.cos(angle), sin(angle))
            drawLine(color, c + dir * (s * 0.32f), c + dir * (s * 0.45f), strokeWidth = s * 0.09f, cap = StrokeCap.Round)
        }
    }
}

@Composable
private fun MoonIcon(modifier: Modifier = Modifier) {
    val color = LocalContentColor.current
    Canvas(modifier = modifier) {
        val s = size.minDimension
        val moon = Path().apply {
            op(
                Path().apply { addOval(Rect(Offset(s / 2f, s / 2f), s * 0.4f)) },
                Path().apply { addOval(Rect(Offset(s * 0.72f, s * 0.32f), s * 0.32f)) },
                PathOperation.Difference,
            )
        }
        drawPath(moon, color)
    }
}

@Composable
internal fun ArrowIcon(up: Boolean, modifier: Modifier = Modifier) {
    val color = LocalContentColor.current
    Canvas(modifier = modifier.size(ButtonDefaults.IconSize)) {
        val s = size.minDimension
        val (tip, tail) = if (up) 0.18f to 0.84f else 0.82f to 0.16f
        val head = if (up) 0.22f else -0.22f
        val stroke = Stroke(width = s * 0.12f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        drawLine(color, Offset(0.5f * s, tail * s), Offset(0.5f * s, tip * s), strokeWidth = stroke.width, cap = StrokeCap.Round)
        val path = Path().apply {
            moveTo(0.24f * s, (tip + head) * s)
            lineTo(0.5f * s, tip * s)
            lineTo(0.76f * s, (tip + head) * s)
        }
        drawPath(path, color, style = stroke)
    }
}

// ── Slider ──

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
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        emoji = emoji,
        onStopTracking = onStopTracking,
        colors = EmojiSliderColors(
            start = start,
            end = end,
            track = MaterialTheme.colorScheme.surfaceContainerHighest,
            activeTrack = Brush.horizontalGradient(listOf(start, end)),
        ),
        behavior = EmojiSliderBehavior(
            allowReselection = allowReselection,
            floatingDirection = floatingDirection,
            displayAverage = showAverage,
            displayTooltip = showAverage,
        ),
        averageProgress = averageValue,
    )
}

internal fun Color.hex(): String = (toArgb().toLong() and 0xFFFFFFFFL).toString(16).uppercase().padStart(8, '0')
