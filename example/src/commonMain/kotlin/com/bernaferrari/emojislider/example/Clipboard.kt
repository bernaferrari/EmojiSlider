package com.bernaferrari.emojislider.example

import androidx.compose.ui.platform.ClipEntry

/** [ClipEntry] has no common constructor, so each platform builds its own plain-text entry. */
internal expect fun plainTextClipEntry(text: String): ClipEntry
