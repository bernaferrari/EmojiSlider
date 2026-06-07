package com.bernaferrari.emojislider.example.desktop

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.bernaferrari.emojislider.example.EmojiSliderExampleApp

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "EmojiSlider",
    ) {
        EmojiSliderExampleApp()
    }
}
