package com.bernaferrari.emojislider.example

import androidx.compose.ui.platform.ClipEntry

internal actual fun plainTextClipEntry(text: String): ClipEntry = ClipEntry.withPlainText(text)
