package com.bernaferrari.emojislidersample.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.bernaferrari.emojislider.EmojiSlider
import com.bernaferrari.emojislider.FloatingEmojiDirection
import com.bernaferrari.emojislidersample.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowcaseScreen() {
    var sliderValues by remember { mutableStateOf(List(10) { 0.3f }) }

    // Note: Floating emojis are handled by the global particle system in MainActivity
    // This ensures emojis can float freely without being clipped by any container

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Text(
                text = "EmojiSlider Showcase",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Basic Usage
        item {
            ShowcaseCard(
                title = "Basic Usage",
                description = "Simple emoji slider with default settings"
            ) {
                EmojiSlider(
                    value = sliderValues[0],
                    onValueChange = {
                        sliderValues = sliderValues.toMutableList().apply { this[0] = it }
                    },
                    emoji = "😍"
                )
                Text(
                    text = "Progress: ${(sliderValues[0] * 100).toInt()}%",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        // Custom Colors
        item {
            ShowcaseCard(
                title = "Custom Gradient Colors",
                description = "Beautiful gradient combinations"
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Constants.getGradients().take(3).forEachIndexed { index, gradient ->
                        EmojiSlider(
                            value = sliderValues[1 + index],
                            onValueChange = { newValue ->
                                sliderValues = sliderValues.toMutableList()
                                    .apply { this[1 + index] = newValue }
                            },
                            emoji = listOf("🎉", "❤️", "🔥")[index],
                            activeTrackGradient = Brush.linearGradient(
                                colors = listOf(
                                    Color(gradient.first), Color(gradient.second)
                                ),
                            )
                        )
                    }
                }
            }
        }

        // Different Emojis
        item {
            ShowcaseCard(
                title = "Emoji Variety",
                description = "Various emoji expressions and types"
            ) {
                val emojis = listOf("🚀", "⭐", "🌈", "💎")
                emojis.forEachIndexed { index, emoji ->
                    EmojiSlider(
                        value = sliderValues[4 + index],
                        onValueChange = { newValue ->
                            sliderValues =
                                sliderValues.toMutableList().apply { this[4 + index] = newValue }
                        },
                        emoji = emoji,
                        activeTrackGradient = Brush.linearGradient(
                            colors = when (index) {
                                0 -> listOf(Color(0xFF2196F3), Color(0xFF00BCD4))
                                1 -> listOf(Color(0xFFFFC107), Color(0xFFFF5722))
                                2 -> listOf(Color(0xFF9C27B0), Color(0xFFE91E63))
                                else -> listOf(Color(0xFF4CAF50), Color(0xFF8BC34A))
                            }
                        ),
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
        }

        // Floating Directions
        item {
            ShowcaseCard(
                title = "Floating Animation Directions",
                description = "Emojis can float up or down when released"
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        "Floating Up:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    EmojiSlider(
                        value = sliderValues[8],
                        onValueChange = {
                            sliderValues = sliderValues.toMutableList().apply { this[8] = it }
                        },
                        emoji = "⬆️",
                        floatingDirection = FloatingEmojiDirection.UP,
                        activeTrackGradient = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF4CAF50),
                                Color(0xFF8BC34A)
                            )
                        ),
                    )

                    Text(
                        "Floating Down:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    EmojiSlider(
                        value = sliderValues[9],
                        onValueChange = {
                            sliderValues = sliderValues.toMutableList().apply { this[9] = it }
                        },
                        emoji = "⬇️",
                        floatingDirection = FloatingEmojiDirection.DOWN,
                        activeTrackGradient = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFFF9800),
                                Color(0xFFFFC107)
                            )
                        ),
                    )
                }
            }
        }

        // With Image Result
        item {
            ShowcaseCard(
                title = "Image Result Display",
                description = "Show custom image when value is selected"
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    EmojiSlider(
                        value = 0.7f,
                        emoji = "📸",
                        activeTrackGradient = Brush.linearGradient(
                            colors = listOf(Color(0xFF4CAF50), Color(0xFF8BC34A))
                        ),
//                        allowReselection = false,
//                        shouldDisplayResultPicture = true
                    )

                    // Sample image using Coil 3
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data("https://picsum.photos/400/200?random=1")
                                .crossfade(true)
                                .build(),
                            contentDescription = "Sample result image",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }

        // Add debug info
        item {
            ShowcaseCard(
                title = "Instagram-Perfect Floating Emojis",
                description = "Now actually works like Instagram! Drag to see the magic ✨"
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "• Drag sliders to see floating emojis above the thumb",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        "• Emoji grows larger as you drag further (dramatic scaling!)",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        "• Release to see the emoji fly across the ENTIRE screen",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "• 2.5 second flight time - you'll actually see it!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun ShowcaseCard(
    title: String,
    description: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            content()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ShowcaseScreenPreview() {
    MaterialTheme {
        ShowcaseScreen()
    }
}
