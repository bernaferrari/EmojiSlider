<div align="center">

# EmojiSlider

<a href="https://bernaferrari.github.io/EmojiSlider/">
  <img src="assets/header.png" alt="EmojiSlider web example">
</a>

[Try the web example](https://bernaferrari.github.io/EmojiSlider/)

A Compose Multiplatform slider with an emoji thumb and floating reactions. Android, desktop, and web.

</div>

## Install

```kotlin
implementation("com.bernaferrari.emojislider:emojislider:1.0.0")
```

## Usage

```kotlin
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import com.bernaferrari.emojislider.EmojiSlider
import com.bernaferrari.emojislider.EmojiSliderColors

@Composable
fun RatingSlider() {
    var value by remember { mutableFloatStateOf(0.5f) }

    EmojiSlider(
        value = value,
        onValueChange = { value = it },
        emoji = "😍",
        colors = EmojiSliderColors(
            start = Color(0xFF7C3AED),
            end = Color(0xFFDB2777),
        ),
    )
}
```

By default the first completed gesture locks the value and can reveal the average. For a normal slider:

```kotlin
EmojiSlider(
    value = value,
    onValueChange = { value = it },
    behavior = EmojiSliderBehavior(allowReselection = true),
)
```

Show an average marker and tooltip after selection:

```kotlin
EmojiSlider(
    value = value,
    onValueChange = { value = it },
    averageProgress = 0.72f,
    behavior = EmojiSliderBehavior(
        displayAverage = true,
        displayTooltip = true,
        tooltipText = "Average value",
    ),
)
```

## Floating emoji inside clipped parents

Cards and scroll containers clip the fly-away emoji. Wrap the screen so particles draw in a shared overlay:

```kotlin
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.bernaferrari.emojislider.EmojiSlider
import com.bernaferrari.emojislider.EmojiSliderParticleSystem

@Composable
fun Screen() {
    EmojiSliderParticleSystem(modifier = Modifier.fillMaxSize()) {
        EmojiSlider(
            value = value,
            onValueChange = { value = it },
        )
    }
}
```

Without the wrap, the slider uses a local overlay. That is fine when it has enough space and is not clipped.

## API

| Parameter | Description |
| --- | --- |
| `value` / `onValueChange` | Controlled progress in `0f..1f`. |
| `emoji` | Thumb and particle glyph. Default `😍`. |
| `colors` | `EmojiSliderColors` — start, end, track, active brush. |
| `behavior` | `EmojiSliderBehavior` — seekable, reselection, direction, average / result / tooltip. |
| `sizes` | `EmojiSliderSizes` — track, thumb, height, inset, particle min / max. |
| `averageProgress` | Average marker position. |
| `resultBitmap` | Optional image after a one-shot selection. |

`sizes.trackInset` defaults to half the thumb so the emoji stays on-canvas. Pass `0.dp` for an edge-to-edge track.
