package com.bernaferrari.emojislider.example.web

import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.bernaferrari.emojislider.example.EmojiSliderExampleApp
import com.bernaferrari.emojislider.generated.resources.Res
import com.bernaferrari.emojislider.generated.resources.noto_emoji_regular
import org.jetbrains.compose.resources.preloadFont

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport(viewportContainerId = "ComposeTarget") {
        // Load the emoji font once before composing the UI, so no Font() load is cancelled mid-download
        // and every emoji renders on first frame instead of flashing tofu.
        val emojiFont by preloadFont(Res.font.noto_emoji_regular)
        if (emojiFont != null) {
            EmojiSliderExampleApp()
        }
    }
}
