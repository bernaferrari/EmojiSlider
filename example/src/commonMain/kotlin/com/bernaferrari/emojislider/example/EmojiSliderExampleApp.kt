package com.bernaferrari.emojislider.example

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bernaferrari.emojislider.EmojiSliderParticleSystem
import com.bernaferrari.emojislider.FloatingEmojiDirection
import com.materialkolor.PaletteStyle
import com.materialkolor.material3.DynamicMaterialExpressiveTheme
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.random.Random

private const val REPOSITORY_URL = "https://github.com/bernaferrari/EmojiSlider"

data class GradientPreset(
    val name: String,
    val start: Color,
    val end: Color,
)

internal val presets = listOf(
    GradientPreset("Iris", Color(0xFF6366F1), Color(0xFFA855F7)),
    GradientPreset("Ember", Color(0xFFF97316), Color(0xFFE11D48)),
    GradientPreset("Dusk", Color(0xFFEC4899), Color(0xFFF59E0B)),
    GradientPreset("Moss", Color(0xFF16A34A), Color(0xFF84CC16)),
    GradientPreset("Lagoon", Color(0xFF0EA5E9), Color(0xFF14B8A6)),
    GradientPreset("Cobalt", Color(0xFF2563EB), Color(0xFF38BDF8)),
)

internal val emojiChoices = listOf("😍", "🔥", "😂", "🤯", "👍", "😢", "💯", "✨")

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun EmojiSliderExampleApp() {
    var preset by remember { mutableStateOf(presets.first()) }
    // The whole UI is a tonal scheme seeded by the slider gradient, so picking a colour re-themes everything.
    DynamicMaterialExpressiveTheme(
        seedColor = preset.start,
        motionScheme = MotionScheme.expressive(),
        style = PaletteStyle.TonalSpot,
        animate = true,
    ) {
        PlaygroundScreen(preset = preset, onPresetChange = { preset = it })
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun PlaygroundScreen(
    preset: GradientPreset,
    onPresetChange: (GradientPreset) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val uriHandler = LocalUriHandler.current

    var value by remember { mutableFloatStateOf(0.62f) }
    var emoji by remember { mutableStateOf(emojiChoices.first()) }
    var direction by remember { mutableStateOf(FloatingEmojiDirection.UP) }
    var oneShot by remember { mutableStateOf(false) }
    var pollMode by remember { mutableStateOf(false) }
    var average by remember { mutableFloatStateOf(0.5f) }
    var sweepJob by remember { mutableStateOf<Job?>(null) }
    // One-shot lock tracking; bumping the key remounts the slider so it accepts a new vote.
    var voted by remember { mutableStateOf(false) }
    var resetKey by remember { mutableIntStateOf(0) }

    fun sweepTo(target: Float) {
        sweepJob?.cancel()
        sweepJob = scope.launch {
            Animatable(value).animateTo(target, tween(durationMillis = 600, easing = FastOutSlowInEasing)) {
                value = this.value
            }
        }
    }

    fun resetVote() {
        voted = false
        resetKey += 1
    }

    val start by animateColorAsState(preset.start, tween(400), label = "start")
    val end by animateColorAsState(preset.end, tween(400), label = "end")

    val stage: @Composable (Modifier) -> Unit = { modifier ->
        StagePanel(
            value = value,
            locked = oneShot && voted,
            onShuffle = {
                emoji = emojiChoices.filter { it != emoji }.random()
                onPresetChange(presets.filter { it != preset }.random())
                resetVote()
                sweepTo(Random.nextDouble(0.12, 0.92).toFloat())
            },
            onReset = ::resetVote,
            modifier = modifier,
        ) {
            key(resetKey) {
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
                    onStopTracking = { if (oneShot) voted = true },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }

    val controls: @Composable (Modifier) -> Unit = { modifier ->
        Panel(modifier = modifier) {
            ControlSection(title = "Emoji") {
                EmojiGrid(selected = emoji, onSelect = { emoji = it })
            }
            ControlSection(title = "Gradient", trailing = preset.name) {
                SwatchRow(selected = preset, onSelect = onPresetChange)
            }
            ControlSection(title = "Reaction") {
                ConnectedToggleGroup(
                    options = listOf("Float up", "Float down"),
                    icons = listOf({ ArrowIcon(up = true) }, { ArrowIcon(up = false) }),
                    selectedIndex = if (direction == FloatingEmojiDirection.UP) 0 else 1,
                    onSelect = { direction = if (it == 0) FloatingEmojiDirection.UP else FloatingEmojiDirection.DOWN },
                )
            }
            ControlSection(title = "Behavior") {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    SwitchRow(
                        title = "Lock after first vote",
                        subtitle = "The value is final once released",
                        checked = oneShot,
                        onCheckedChange = {
                            oneShot = it
                            if (!it) {
                                voted = false
                                pollMode = false
                            }
                        },
                        position = if (oneShot) ListPosition.First else ListPosition.Single,
                    )
                    AnimatedVisibility(
                        visible = oneShot,
                        enter = expandVertically(MaterialTheme.motionScheme.defaultSpatialSpec()) + fadeIn(),
                        exit = shrinkVertically(MaterialTheme.motionScheme.fastSpatialSpec()) + fadeOut(),
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            SwitchRow(
                                title = "Reveal crowd average",
                                subtitle = "Shows a marker after voting",
                                checked = pollMode,
                                onCheckedChange = { pollMode = it },
                                position = if (pollMode) ListPosition.Middle else ListPosition.Last,
                            )
                            AnimatedVisibility(
                                visible = pollMode,
                                enter = expandVertically(MaterialTheme.motionScheme.defaultSpatialSpec()) + fadeIn(),
                                exit = shrinkVertically(MaterialTheme.motionScheme.fastSpatialSpec()) + fadeOut(),
                            ) {
                                ValueSliderRow(
                                    title = "Average",
                                    value = average,
                                    onValueChange = { average = it },
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    val code: @Composable (Modifier) -> Unit = { modifier ->
        CodePanel(
            code = usageSnippet(
                emoji = emoji,
                preset = preset,
                direction = direction,
                oneShot = oneShot,
                pollMode = pollMode,
                average = average,
            ),
            modifier = modifier,
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AmbientBackground(start = start, end = end, value = value, modifier = Modifier.fillMaxSize())

        EmojiSliderParticleSystem(modifier = Modifier.fillMaxSize()) {
            BoxWithConstraints(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                val wide = maxWidth >= 900.dp
                Column(
                    modifier = Modifier
                        .widthIn(max = if (wide) 1120.dp else 560.dp)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = if (wide) 40.dp else 16.dp, vertical = if (wide) 28.dp else 16.dp),
                ) {
                    TopBar(emoji = emoji, onGitHub = { uriHandler.openUri(REPOSITORY_URL) })
                    Spacer(Modifier.height(if (wide) 56.dp else 28.dp))
                    Intro(wide = wide)
                    Spacer(Modifier.height(if (wide) 40.dp else 24.dp))

                    if (wide) {
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                            ) {
                                stage(Modifier.fillMaxWidth().heightIn(min = 420.dp))
                                code(Modifier.fillMaxWidth())
                            }
                            controls(Modifier.width(380.dp))
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            stage(Modifier.fillMaxWidth())
                            controls(Modifier.fillMaxWidth())
                            code(Modifier.fillMaxWidth())
                        }
                    }

                    Spacer(Modifier.height(32.dp))
                    Footer()
                }
            }
        }
    }
}

@Composable
private fun TopBar(emoji: String, onGitHub: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Wordmark(emoji = emoji)
        Spacer(Modifier.weight(1f))
        GitHubButton(onClick = onGitHub)
    }
}

@Composable
private fun Intro(wide: Boolean) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            "A slider with a pulse.",
            style = if (wide) MaterialTheme.typography.displayMediumEmphasized else MaterialTheme.typography.headlineLargeEmphasized,
            letterSpacing = if (wide) (-1).sp else (-0.5).sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            "An emoji thumb that grows as you drag, reactions that float away on release, " +
                "and optional one-shot polls. Compose Multiplatform for Android, desktop, and web.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.widthIn(max = 560.dp),
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun EmojiGrid(selected: String, onSelect: (String) -> Unit) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val columns = emojiChoices.size
        val gap = 4.dp
        // 1dp under the exact fit so rounding never wraps the last tile.
        val cell = ((maxWidth - gap * (columns - 1)) / columns - 1.dp).coerceAtMost(48.dp)
        FlowRow(
            maxItemsInEachRow = columns,
            horizontalArrangement = Arrangement.spacedBy(gap),
            verticalArrangement = Arrangement.spacedBy(gap),
        ) {
            emojiChoices.forEach { item ->
                EmojiChoice(
                    emoji = item,
                    selected = selected == item,
                    onClick = { onSelect(item) },
                    size = cell,
                )
            }
        }
    }
}

@Composable
private fun SwatchRow(selected: GradientPreset, onSelect: (GradientPreset) -> Unit) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val gap = 8.dp
        val cell = ((maxWidth - gap * (presets.size - 1)) / presets.size - 1.dp).coerceAtMost(48.dp)
        Row(horizontalArrangement = Arrangement.spacedBy(gap)) {
            presets.forEach { item ->
                ColorSwatch(
                    start = item.start,
                    end = item.end,
                    name = item.name,
                    selected = selected == item,
                    onClick = { onSelect(item) },
                    size = cell,
                )
            }
        }
    }
}

private fun usageSnippet(
    emoji: String,
    preset: GradientPreset,
    direction: FloatingEmojiDirection,
    oneShot: Boolean,
    pollMode: Boolean,
    average: Float,
): String = buildString {
    appendLine("EmojiSlider(")
    appendLine("    value = value,")
    appendLine("    onValueChange = { value = it },")
    appendLine("    emoji = \"$emoji\",")
    appendLine("    colors = EmojiSliderColors(")
    appendLine("        start = Color(0x${preset.start.hex()}),")
    appendLine("        end = Color(0x${preset.end.hex()}),")
    appendLine("    ),")
    // Only emit arguments that differ from EmojiSliderBehavior's defaults.
    val behavior = buildList {
        if (!oneShot) add("allowReselection = true")
        if (direction == FloatingEmojiDirection.DOWN) add("floatingDirection = FloatingEmojiDirection.DOWN")
        if (!pollMode) {
            add("displayAverage = false")
            add("displayTooltip = false")
        }
    }
    if (behavior.isNotEmpty()) {
        appendLine("    behavior = EmojiSliderBehavior(")
        behavior.forEach { appendLine("        $it,") }
        appendLine("    ),")
    }
    if (pollMode) appendLine("    averageProgress = ${(average * 100).toInt() / 100f}f,")
    append(")")
}
