package com.bernaferrari.emojislidersample

/**
 * Modern constants for the EmojiSlider sample app.
 *
 * Gradient colors inspired by:
 * - Material Design 3 color palettes
 * - Modern UI/UX trends
 * - uiGradients.com
 * - Spark design system
 */
object Constants {

    fun getGradients(): List<Pair<Int, Int>> = listOf(
        // Material 3 inspired gradients
        Pair(0xFF6200EE.toInt(), 0xFFE91E63.toInt()), // Purple -> Pink
        Pair(0xFF00BCD4.toInt(), 0xFF2196F3.toInt()), // Cyan -> Blue
        Pair(0xFF4CAF50.toInt(), 0xFF8BC34A.toInt()), // Green gradient
        Pair(0xFFFF5722.toInt(), 0xFFFF9800.toInt()), // Orange gradient

        // Vibrant modern gradients
        Pair(0xFFE91E63.toInt(), 0xFF9C27B0.toInt()), // Pink -> Purple
        Pair(0xFF2196F3.toInt(), 0xFF03A9F4.toInt()), // Blue gradient
        Pair(0xFFFFC107.toInt(), 0xFFFF5722.toInt()), // Yellow -> Orange
        Pair(0xFF9C27B0.toInt(), 0xFF673AB7.toInt()), // Purple gradient

        // Sunset/Nature inspired
        Pair(0xFFFF6B6B.toInt(), 0xFFFFE66D.toInt()), // Coral -> Yellow
        Pair(0xFF4ECDC4.toInt(), 0xFF44A08D.toInt()), // Turquoise -> Teal
        Pair(0xFFFC466B.toInt(), 0xFF3F5EFB.toInt()), // Pink -> Blue
        Pair(0xFFFFCE00.toInt(), 0xFFFF6B6B.toInt()), // Gold -> Coral

        // Cool tones
        Pair(0xFF667eea.toInt(), 0xFF764ba2.toInt()), // Blue -> Purple
        Pair(0xFF89f7fe.toInt(), 0xFF66a6ff.toInt()), // Light Blue gradient
        Pair(0xFFa8edea.toInt(), 0xFFfed6e3.toInt()), // Mint -> Pink
        Pair(0xFF667db6.toInt(), 0xFF0082c8.toInt())  // Navy -> Blue
    )

    fun getEmojis(): List<String> = listOf(
        // Emotions & Faces
        "😍", "😂", "😀", "😎", "🥰", "😭", "😢", "😱",
        "😡", "😴", "🤔", "😇", "😷", "🤯", "🥺", "😋",

        // Hand gestures
        "👍", "👏", "🙌", "🙏", "👋", "✌️", "🤞", "👌",

        // Objects & Symbols
        "🔥", "❤️", "💯", "⭐", "🎉", "🚀", "💎", "🌈",
        "⚡", "✨", "🎯", "🏆", "🎊", "🌟", "💫", "🔆",

        // Animals
        "🐶", "🐱", "🦄", "🦋", "🐯", "🦁", "🐸", "🐵",

        // Food & Fun
        "🍕", "🍰", "🎂", "🍦", "🍭", "🎈", "🎮", "🎵"
    )
}
