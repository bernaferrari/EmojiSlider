# EmojiSlider

A playful Compose Multiplatform slider with an emoji thumb, floating reactions, and shared Android, desktop, and web support.

[Try the web example](https://bernaferrari.github.io/EmojiSlider/)

## Features

- Shared Compose API for Android, desktop, and WASM JS.
- Emoji thumb and floating release animation with bundled emoji font.
- Tap, drag, one-shot selection, and reselection modes.
- Custom colors, gradients, sizes, average marker, tooltip, and result image.

## Installation

After the `1.0.0` release is published to Maven Central:

```kotlin
dependencies {
    implementation("com.bernaferrari.emojislider:emojislider:1.0.0")
}
```

For local development inside this repository, depend on the module:

```kotlin
dependencies {
    implementation(project(":emojislider"))
}
```

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
| `value` / `progress` | `0.25f` | Current slider value from `0f` to `1f`. `progress` is kept as a compatibility alias. |
| `onValueChange` / `onProgressChange` | `{}` | Called when user input changes the value. |
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

The example lives in `:example` and shares one Compose UI across targets.

Run the web version:

```sh
./gradlew :example:wasmJsBrowserDevelopmentRun
```

Build the production web bundle:

```sh
./gradlew :example:wasmJsBrowserDistribution
```

Run the desktop version:

```sh
./gradlew :example:run
```

Run the desktop version with Compose Hot Reload:

```sh
./gradlew :example:hotRunDesktop
```

Build/check the Android host:

```sh
./gradlew :app:assembleDebug
```
