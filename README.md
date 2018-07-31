| Sample app | Sample usage |
|:-:|:-:|
| ![First](assets/ig_slider.gif?raw=true) | ![Sec](assets/sample_2.gif?raw=true) |

Emoji Slider
============
[ ![Download](https://api.bintray.com/packages/bernaferrari/EmojiSlider/com.bernaferrari.emojislider/images/download.svg) ](https://bintray.com/bernaferrari/EmojiSlider/com.bernaferrari.emojislider/_latestVersion)

A custom made SeekBar **heavily** inspired by [this great widget from Instagram](https://instagram-press.com/blog/2018/05/10/introducing-the-emoji-slider/).

## 💻 Installation
Add a dependency to your `build.gradle`:
```groovy
dependencies {
    implementation 'com.bernaferrari.emojislider:emojislider:0.3'
}
```
It is fully stable, but there might be some changes to the API, like improved naming, or some small changes on functions.
This is the reason it is only `0.3` - this only means it is the third public version, but you can use it fine already.

## 🤯 Features
- Customize with xml using custom handy attributes.
- Customize in your activity, fragment or dialog.
- Creating new widget programmatically.

## 😍 Reselection Enabled Sample

| Up | Down |
|:-:|:-:|
| ![First](assets/up_reselection.gif?raw=true) | ![Sec](assets/down_reselection.gif?raw=true) |

## ❕ Basic Usage
Place the `EmojiSlider` in your layout.
```groovy
<com.bernaferrari.emojislider.EmojiSlider
    android:id="@+id/slider"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"/>
```

**Important:** if you want to have the emoji floating above the slider when it is pressed/dragged, you need to supply a view, preferably on the foreground, to be drawn and tell the slider who the view is.
Example (xml + kotlin):

```groovy
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content">

    <com.bernaferrari.emojislider.EmojiSlider
        android:id="@+id/slider"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:padding="72dp" />

    <View
        android:id="@+id/slider_particle_system"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />

</FrameLayout>
```

```kotlin
   findViewById<EmojiSlider>(R.layout.slider).sliderParticleSystem = slider_particle_system
```

## ❔ Usage
To track the current position of the slider, set the `positionListener`, as shown below:
```
val slider = findViewById<EmojiSlider>(R.id.slider)
slider.positionListener = { p -> Log.d("MainActivity", "current position is: $p" )}
```

You can also track the beginning and completion of the movement of the slider, using the following properties:
`startTrackingListener` and `stopTrackingListener`. Examples below:
```
slider.startTrackingListener = { /* action on slider touched */ }
slider.stopTrackingListener = { /* action on slider released */ }
```

Here is another example in Kotlin:
```kotlin
// Kotlin
val slider = findViewById<EmojiSlider>(R.id.slider)
slider.sliderParticleSystem = slider_particle_system
slider.position = 0.25f
slider.averagePosition = 0.75f
slider.allowReselection = true
slider.colorStart = Color.RED
slider.colorEnd = Color.YELLOW
slider.setResultDrawable(profilePictureBitmap)

```

Here is a simple example in Java:
```java
// Java
final EmojiSlider slider = findViewById(R.id.slider);
slider.setStartTrackingListener(new Function0<Unit>() {
    @Override
    public Unit invoke() {
        Log.d("D", "setBeginTrackingListener");
        return Unit.INSTANCE;
    }
});

slider.setStopTrackingListener(new Function0<Unit>() {
    @Override
    public Unit invoke() {
        Log.d("D", "setEndTrackingListener");
        return Unit.INSTANCE;
    }
});

// Or Java 8 lambda
slider.setPositionListener(pos -> {
    Log.d("D", "setPositionListener");
    return Unit.INSTANCE;
});
```

**Check the sample app for more.** The sample app even shows how to use Glide to load a Bitmap into a round drawable.

## 🎨 Customization and Attributes

All customizable attributes for EmojiSlider:
<table>
    <th>Attribute Name</th>
    <th>Default Value</th>
    <th>Description</th>
    <tr>
        <td>app:emoji</td>
        <td>😍</td>
        <td>The emoji which will be used on the slider</td>
    </tr>
    <tr>
        <td>app:progress</td>
        <td>0.25f</td>
        <td>Initial position for the progress in range from 0.0 to 1.0.</td>
    </tr>
    <tr>
        <td>app:average_progress</td>
        <td>0.50f</td>
        <td>Initial position for the average value in range from 0.0 to 1.0.</td>
    </tr>
    <tr>
        <td>app:bar_progress_color_start</td>
        <td>@color/slider_gradient_start</td>
        <td>Color of the start (left side) of the progress bar.</td>
    </tr>
    <tr>
        <td>app:bar_progress_color_end</td>
        <td>@color/slider_gradient_end</td>
        <td>Color of the end (right side) of the progress bar</td>
    </tr>
    <tr>
        <td>app:bar_track_color</td>
        <td>@color/slider_track</td>
        <td>Color of the bar's track.</td>
    </tr>
    <tr>
        <td>app:thumb_size_percent_on_pressed</td>
        <td>0.9</td>
        <td>Thumb size automatically shrinks to 90% (0.9) its original size when a touch is detected. This allows to
            choose another value between 0.0 and 1.0.
        </td>
    </tr>
    <tr>
        <td>app:allow_reselection</td>
        <td>false</td>
        <td>Should the slider behave like the original Emoji Slider or like a SeekBar? When true, it behaves like a
            SeekBar, so average/profile/result will not be shown.
        </td>
    </tr>
    <tr>
        <td>app:is_touch_disabled</td>
        <td>false</td>
        <td>Allow to disable touch input.</td>
    </tr>
    <tr>
        <td>app:should_display_tooltip</td>
        <td>true</td>
        <td>Allow to disable the tooltip when a value is selected.</td>
    </tr>
     <tr>
         <td>app:tooltip_text</td>
         <td>@string/average_answer</td>
         <td>The "average answer" text, translated into 40 languages. You can overwrite it using this.</td>
     </tr>
    <tr>
        <td>app:tooltip_dismiss_timer</td>
        <td>2500</td>
        <td>The tooltip auto hide after some period, in milliseconds. Choose -1 to disable this timer.</td>
    </tr>
    <tr>
        <td>app:should_display_average</td>
        <td>true</td>
        <td>Allow to disable the average circle when a value is selected. If this is disabled, tooltip will not be shown
            even if it is enabled.
        </td>
    </tr>
    <tr>
        <td>app:should_display_average</td>
        <td>false</td>
        <td>Allow to disable the round circle that shows up when a value is selected (usually with user's profile
            picture).
        </td>
    </tr>
    <tr>
        <td>app:register_touches_outside_thumb</td>
        <td>true</td>
        <td>The original Emoji Slider only registers touch inside the thumb. The SeekBar register on the bar, too. This
            allows to choose which best suits you.
        </td>
    </tr>
    <tr>
        <td>app:particle_direction</td>
        <td>up</td>
        <td>Should the floating emoji go up or down after finger leaves the bar?</td>
    </tr>
</table>

Of course, some attributes might have better names than others and documentation might not be perfect. If you find anything wrong or weird, [**let me know**](https://github.com/bernaferrari/EmojiSlider/issues).

## 📃 Libraries Used
* Facebook's [Rebound](https://github.com/facebook/rebound)
* [BubbleView](https://github.com/cpiz/BubbleView)

## 🦁 Screenshots

| Floating | Value Selected |
|:-:|:-:|
| ![First](assets/custom_1.png?raw=true) | ![Sec](assets/custom_2.png?raw=true) |

| Sample | Sample |
|:-:|:-:|
| ![Third](assets/main_1.png?raw=true) | ![Fourth](assets/main_2.png?raw=true) |

### Reporting Issues

Issues and Pull Requests are welcome.
You can report [here](https://github.com/bernaferrari/EmojiSlider/issues).

License
-------

Copyright 2018 Bernardo Ferrari.

Licensed to the Apache Software Foundation (ASF) under one or more contributor
license agreements.  See the NOTICE file distributed with this work for
additional information regarding copyright ownership.  The ASF licenses this
file to you under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License.  You may obtain a copy of
the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
License for the specific language governing permissions and limitations under
the License.
