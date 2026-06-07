package com.bernaferrari.emojislider.example.web

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.bernaferrari.emojislider.example.EmojiSliderExampleApp

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport(viewportContainerId = "ComposeTarget") {
        EmojiSliderExampleApp()
    }
}
