<div align="center">

# EmojiSlider

<a href="https://bernaferrari.github.io/EmojiSlider/">
  <img src="assets/header.png" alt="EmojiSlider web example">
</a>

[Try the web example](https://bernaferrari.github.io/EmojiSlider/)

A playful Compose Multiplatform slider with an emoji thumb, floating reactions, and shared Android, desktop, and web support.

</div>

## Features

- Shared Compose API for Android, desktop, and WASM JS.
- Emoji thumb and floating release animation with bundled emoji font.
- Tap, drag, one-shot selection, and reselection modes.
- Custom colors, gradients, sizes, average marker, tooltip, and result image.

## Installation

```kotlin
dependencies {
    implementation("com.bernaferrari.emojislider:emojislider:1.0.0")
}
```

Library version source of truth: `emojislider/build.gradle.kts` → `mavenPublishing.coordinates(version = …)` (currently `1.0.0`). Keep the coordinate above in sync when releasing.

**Migration notes** (CMP rewrite):
- Removed unused public `EmojiSliderTooltip`. Use the built-in average tooltip (`shouldDisplayTooltip` / `tooltipText`) or your own UI.
- Removed unused `EmojiSliderState` / `rememberEmojiSliderState`. The slider is controlled with `value` / `onValueChange`.
- Dropped compatibility aliases `progress` / `onProgressChange` / `floatingEmojiDirection`. Use `value`, `onValueChange`, and `floatingDirection`.

See [docs/RELEASING.md](docs/RELEASING.md).

## Testing / CI

Library tests live in `emojislider/src/commonTest` (pure logic) and `emojislider/src/desktopTest` (Compose UI / semantics):

```bash
./gradlew :emojislider:desktopTest
./gradlew :emojislider:spotlessCheck
```

[CI](.github/workflows/ci.yml) runs on every push and pull request: desktop compile (library + example), desktop tests, required Spotless, and a non-blocking wasmJs compile.

## Releasing

To cut a release: bump `version` in `emojislider/build.gradle.kts` (and this README), commit, tag `vX.Y.Z`, and push the tag. The [Release workflow](.github/workflows/release.yml) builds the library and creates a GitHub Release with generated notes. Maven Central publish is optional and runs only when repository secrets are configured.

Full steps, secret names, and dry-run options: **[docs/RELEASING.md](docs/RELEASING.md)**.

## Basic Usage

```kotlin
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bernaferrari.emojislider.EmojiSlider

@Composable
fun RatingSlider() {
    var value by remember { mutableFloatStateOf(0.5f) }

    EmojiSlider(
        modifier = Modifier,
        emoji = "😍",
        value = value,
        onValueChange = { value = it },
        colorStart = Color(0xFF7C3AED),
        colorEnd = Color(0xFFDB2777),
    )
}
```

## Floating Above Clipped Content

If the slider is inside a card, scroll container, or any clipped parent, wrap the screen with `EmojiSliderParticleSystem`. The slider will draw the floating emoji in the shared overlay instead of being clipped by its own bounds.

```kotlin
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.bernaferrari.emojislider.EmojiSlider
import com.bernaferrari.emojislider.EmojiSliderParticleSystem

@Composable
fun Screen() {
    EmojiSliderParticleSystem(modifier = Modifier.fillMaxSize()) {
        EmojiSlider(
            value = 0.6f,
            onValueChange = { /* update state */ },
        )
    }
}
```

Without `EmojiSliderParticleSystem`, `EmojiSlider` falls back to a local overlay, which is fine when the slider has enough vertical space and is not clipped.

## Reselection And One-Shot Modes

Use `allowReselection = true` when the slider should behave like a regular slider. Use `allowReselection = false` when the first completed gesture should lock the selected value and reveal the average/result state.

```kotlin
EmojiSlider(
    value = value,
    onValueChange = { value = it },
    allowReselection = true,
)
```

## Average Indicator

```kotlin
EmojiSlider(
    value = value,
    onValueChange = { value = it },
    averageProgressValue = 0.72f,
    shouldDisplayAverage = true,
    shouldDisplayTooltip = true,
    tooltipText = "Average value",
)
```

## Main API

| Parameter | Default | Description |
| --- | --- | --- |
| `emoji` | `"😍"` | Emoji drawn as the thumb and floating particle. |
| `value` | `0.25f` | Current slider value from `0f` to `1f`. |
| `onValueChange` | `{}` | Called when user input changes the value. |
| `onStartTracking` | `{}` | Called when a tap or drag starts. |
| `onStopTracking` | `{}` | Called when tracking ends. |
| `colorStart` | `Color(0xFF6200EE)` | Start color for the active track and result state. |
| `colorEnd` | `Color(0xFFE91E63)` | End color for the active track and result state. |
| `colorTrack` | `Color(0xFFE0E0E0)` | Inactive track color. |
| `activeTrackGradient` | start-to-end gradient | Brush for the active track. |
| `isUserSeekable` | `true` | Enables or disables user input. |
| `registerTouchOnTrack` | `true` | Allows tapping/dragging on the whole track, not only the thumb. |
| `allowReselection` | `false` | Keeps the slider interactive after a completed selection. |
| `floatingDirection` | `FloatingEmojiDirection.UP` | Direction used when the released emoji flies away. |
| `minEmojiSize` | `24.dp` | Smallest floating emoji size. |
| `maxEmojiSize` | `48.dp` | Largest floating emoji size. |
| `averageProgressValue` | `0.5f` | Position of the average indicator. |
| `shouldDisplayAverage` | `true` | Shows the average marker after selection. |
| `shouldDisplayResultPicture` | `true` | Shows the selected result state after selection. |
| `shouldDisplayTooltip` | `true` | Shows the tooltip after selection when average is enabled. |
| `tooltipText` | `"Average value"` | Tooltip label. |
| `tooltipAutoDismissTimer` | `2500L` | Tooltip auto-dismiss delay in milliseconds. |
| `thumbSizePercentWhenPressed` | `0.9f` | Thumb scale while pressed. |
| `resultBitmap` | `null` | Optional result image drawn after selection. |
| `trackHeight` | `16.dp` | Track height. |
| `thumbSize` | `56.dp` | Emoji thumb size. |
| `sliderHeight` | `80.dp` | Overall slider layout height. |
| `trackInset` | `thumbSize / 2` | Horizontal track inset; keeps the thumb visible at the edges. |

## Example App

The `:example` module shares one Compose UI across Android, desktop, and web.
