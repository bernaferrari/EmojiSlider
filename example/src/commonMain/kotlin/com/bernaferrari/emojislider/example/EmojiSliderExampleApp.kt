package com.bernaferrari.emojislider.example

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bernaferrari.emojislider.EmojiSliderParticleSystem
import com.bernaferrari.emojislider.FloatingEmojiDirection
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.random.Random

private const val REPOSITORY_URL = "https://github.com/bernaferrari/EmojiSlider"

private val exampleColors = lightColorScheme(
    primary = Color(0xFF7C3AED),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEDE9FE),
    onPrimaryContainer = Color(0xFF2E1065),
    secondary = Color(0xFF0D9488),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFCCFBF1),
    onSecondaryContainer = Color(0xFF042F2E),
    tertiary = Color(0xFFDB2777),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFCE7F3),
    onTertiaryContainer = Color(0xFF500724),
    background = Color(0xFFF6F4FB),
    onBackground = Color(0xFF1C1822),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1C1822),
    surfaceVariant = Color(0xFFF1ECF7),
    onSurfaceVariant = Color(0xFF6B6675),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF8F4FD),
    surfaceContainer = Color(0xFFF3EEFA),
    surfaceContainerHigh = Color(0xFFECE5F6),
    surfaceContainerHighest = Color(0xFFE6DEF2),
    outline = Color(0xFF79747E),
    outlineVariant = Color(0xFFD6D0DA),
)

data class GradientPreset(
    val name: String,
    val start: Color,
    val end: Color,
)

// Sorted around the colour wheel starting at the brand violet (Coral was dropped — too close to Ember).
internal val presets = listOf(
    GradientPreset("Aurora", Color(0xFF7C3AED), Color(0xFFDB2777)),
    GradientPreset("Berry", Color(0xFFEC4899), Color(0xFF8B5CF6)),
    GradientPreset("Ember", Color(0xFFEF4444), Color(0xFFF97316)),
    GradientPreset("Sunset", Color(0xFFF97316), Color(0xFFEAB308)),
    GradientPreset("Forest", Color(0xFF059669), Color(0xFF84CC16)),
    GradientPreset("Ocean", Color(0xFF0284C7), Color(0xFF06B6D4)),
    GradientPreset("Arctic", Color(0xFF6366F1), Color(0xFF818CF8)),
)

// 14 emojis = a clean 2×7 grid that lines up with the 7 colour swatches below.
internal val emojiChoices = listOf(
    "😍", "😂", "🥰", "😎", "🤩", "🥳", "👍",
    "🔥", "❤️", "💯", "⭐", "🚀", "🌈", "✨",
)

@Composable
fun EmojiSliderExampleApp() {
    MaterialTheme(colorScheme = exampleColors) {
        PlaygroundScreen()
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PlaygroundScreen() {
    val scope = rememberCoroutineScope()
    val uriHandler = LocalUriHandler.current

    var value by remember { mutableFloatStateOf(0.62f) }
    var emoji by remember { mutableStateOf(emojiChoices.first()) }
    var preset by remember { mutableStateOf(presets.first()) }
    var direction by remember { mutableStateOf(FloatingEmojiDirection.UP) }
    var pollMode by remember { mutableStateOf(false) }
    var oneShot by remember { mutableStateOf(false) }
    var average by remember { mutableFloatStateOf(0.5f) }
    var spinTrigger by remember { mutableStateOf(0) }
    var sweepJob by remember { mutableStateOf<Job?>(null) }
    // One-shot lock tracking for the hero slider.
    var heroVoted by remember { mutableStateOf(false) }
    var heroResetKey by remember { mutableStateOf(0) }

    fun sweepTo(target: Float) {
        sweepJob?.cancel()
        sweepJob = scope.launch {
            val anim = Animatable(value)
            anim.animateTo(target, tween(durationMillis = 650, easing = FastOutSlowInEasing)) { value = this.value }
        }
    }

    // Animate the active colours so theme swaps glide instead of snapping.
    val start by animateColorAsState(preset.start, tween(450), label = "start")
    val end by animateColorAsState(preset.end, tween(450), label = "end")
    val accentBrush = Brush.horizontalGradient(listOf(start, end))

    Box(modifier = Modifier.fillMaxSize()) {
        ReactiveBackground(start = start, end = end, value = value, modifier = Modifier.fillMaxSize())

        EmojiSliderParticleSystem(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                Column(
                    modifier = Modifier
                        .widthIn(max = 520.dp)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 28.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    // ── Header ──
                    Box(modifier = Modifier.fillMaxWidth().padding(bottom = 2.dp)) {
                        Text(
                            "EmojiSlider",
                            style = MaterialTheme.typography.displaySmall.copy(brush = accentBrush),
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        GitHubIconButton(
                            onClick = { uriHandler.openUri(REPOSITORY_URL) },
                            modifier = Modifier.align(Alignment.CenterEnd),
                        )
                    }

                    // ── Hero playground ──
                    SectionCard {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            HeroNumber(value = value, start = start, end = end)
                        }
                        key(heroResetKey) {
                            PreviewSlider(
                                value = value,
                                onValueChange = { value = it },
                                emoji = emoji,
                                start = start,
                                end = end,
                                showAverage = pollMode,
                                averageValue = average,
                                allowReselection = !oneShot,
                                floatingDirection = direction,
                                onStopTracking = { if (oneShot) heroVoted = true },
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            SurpriseButton(
                                spinTrigger = spinTrigger,
                                brush = accentBrush,
                                glow = start,
                                onClick = {
                                    spinTrigger += 1
                                    emoji = emojiChoices.random()
                                    preset = presets.random()
                                    heroVoted = false
                                    heroResetKey += 1
                                    sweepTo(Random.nextDouble(0.15, 0.95).toFloat())
                                },
                                modifier = Modifier.weight(1f),
                            )
                            AnimatedVisibility(
                                visible = oneShot && heroVoted,
                                enter = expandHorizontally(expandFrom = Alignment.Start) + fadeIn(),
                                exit = shrinkHorizontally(shrinkTowards = Alignment.Start) + fadeOut(),
                            ) {
                                VoteAgainButton(
                                    accent = start,
                                    onClick = { heroVoted = false; heroResetKey += 1 },
                                )
                            }
                        }
                    }

                    // ── Customize: emoji + vibe on a shared 7-column grid (sizes computed to fill width) ──
                    SectionCard {
                        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                            val columns = 7
                            val gap = 10.dp
                            // 1dp under the exact fit so rounding never wraps the 7th item; the tiny
                            // slack is centred so left/right margins stay equal, with uniform gaps.
                            val cell = (maxWidth - gap * (columns - 1)) / columns - 1.dp
                            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                FlowRow(
                                    maxItemsInEachRow = columns,
                                    horizontalArrangement = Arrangement.spacedBy(gap, Alignment.CenterHorizontally),
                                    verticalArrangement = Arrangement.spacedBy(gap),
                                ) {
                                    emojiChoices.forEach { item ->
                                        EmojiChoice(
                                            emoji = item,
                                            selected = emoji == item,
                                            accent = accentBrush,
                                            onClick = { emoji = item },
                                            size = cell,
                                        )
                                    }
                                }
                                HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
                                FlowRow(
                                    maxItemsInEachRow = columns,
                                    horizontalArrangement = Arrangement.spacedBy(gap, Alignment.CenterHorizontally),
                                ) {
                                    presets.forEach { item ->
                                        ColorSwatch(
                                            start = item.start,
                                            end = item.end,
                                            selected = preset == item,
                                            onClick = { preset = item },
                                            size = cell,
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // ── Options ──
                    SectionCard {
                        SectionLabel("⚙️", "Options")
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                "Which way does the emoji fly?",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                            )
                            SlidingSegmented(
                                options = listOf("⬆️" to "Up", "⬇️" to "Down"),
                                selectedIndex = if (direction == FloatingEmojiDirection.UP) 0 else 1,
                                onSelect = { direction = if (it == 0) FloatingEmojiDirection.UP else FloatingEmojiDirection.DOWN },
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                        // One-shot and Poll sit flush (no gap between the two rows).
                        Column {
                            SwitchRow(
                                emoji = "🎯",
                                title = "One-shot vote",
                                subtitle = "Lock the result after the first pick",
                                checked = oneShot,
                                onCheckedChange = {
                                    oneShot = it
                                    if (!it) {
                                        heroVoted = false
                                        pollMode = false
                                    }
                                },
                            )
                            AnimatedVisibility(
                                visible = oneShot,
                                enter = expandVertically() + fadeIn(),
                                exit = shrinkVertically() + fadeOut(),
                            ) {
                                Column {
                                    SwitchRow(
                                        emoji = "📊",
                                        title = "Poll mode",
                                        subtitle = "Reveal the crowd average after you vote",
                                        checked = pollMode,
                                        onCheckedChange = { pollMode = it },
                                    )
                                    AnimatedVisibility(
                                        visible = pollMode,
                                        enter = expandVertically() + fadeIn(),
                                        exit = shrinkVertically() + fadeOut(),
                                    ) {
                                        ValueSliderRow(
                                            title = "Crowd average position",
                                            value = average,
                                            accent = start,
                                            onValueChange = { average = it },
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
