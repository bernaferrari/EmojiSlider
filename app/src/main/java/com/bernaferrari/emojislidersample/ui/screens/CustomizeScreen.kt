package com.bernaferrari.emojislidersample.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.bernaferrari.emojislider.EmojiSlider
import com.bernaferrari.emojislider.FloatingEmojiDirection
import com.bernaferrari.emojislidersample.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomizeScreen() {
    var progress by remember { mutableStateOf(0.4f) }
    var selectedEmoji by remember { mutableStateOf("😍") }
    var selectedColors by remember { mutableStateOf(Constants.getGradients().first()) }
    var floatingDirection by remember { mutableStateOf(FloatingEmojiDirection.UP) }
    var showAverage by remember { mutableStateOf(true) }
    var averageValue by remember { mutableStateOf(0.6f) }
    var allowReselection by remember { mutableStateOf(false) }
    var isValueSelected by remember { mutableStateOf(false) }
    var showResultImage by remember { mutableStateOf(false) }

    // Auto-reset value selection when allowReselection changes
    LaunchedEffect(allowReselection) {
        if (allowReselection) {
            isValueSelected = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Customize Your Slider",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        // Live Preview
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Live Preview",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    // Status indicator
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isValueSelected)
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (isValueSelected) "Selected" else "Interactive",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = if (isValueSelected)
                                MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                EmojiSlider(
                    value = progress,
                    onValueChange = { progress = it },
                    emoji = selectedEmoji,
                    activeTrackGradient = Brush.horizontalGradient(
                        listOf(
                            Color(selectedColors.first),
                            Color(selectedColors.second)
                        )
                    ),
                    floatingDirection = floatingDirection,
//                    shouldDisplayAverage = showAverage,
//                    averageProgressValue = averageValue,
//                    allowReselection = allowReselection,
//                    shouldDisplayResultPicture = showResultImage,
//                    onStopTracking = {
//                        if (!allowReselection) {
//                            isValueSelected = true
//                        }
//                    }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Progress: ${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )

                    if (showAverage) {
                        Text(
                            text = "Average: ${(averageValue * 100).toInt()}%",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Animated Reset Button
                AnimatedVisibility(
                    visible = isValueSelected,
                    enter = slideInVertically(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        ),
                        initialOffsetY = { -it / 2 } // Start from above
                    ) + fadeIn(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    ) + expandVertically(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        ),
                        expandFrom = Alignment.Top
                    ),
                    exit = slideOutVertically(
                        animationSpec = tween(300, easing = FastOutSlowInEasing),
                        targetOffsetY = { -it / 3 } // Slide up when hiding
                    ) + fadeOut(
                        animationSpec = tween(200)
                    ) + shrinkVertically(
                        animationSpec = tween(300, easing = FastOutSlowInEasing),
                        shrinkTowards = Alignment.Top
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(
                            8.dp,
                            Alignment.CenterHorizontally
                        )
                    ) {
                        Button(
                            onClick = {
                                isValueSelected = false
                                progress = 0.4f
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary
                            )
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Reset")
                        }
                    }
                }

                // Animated Result Image
                AnimatedVisibility(
                    visible = showResultImage && isValueSelected,
                    enter = slideInVertically(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        ),
                        initialOffsetY = { it / 4 } // Slide in from below
                    ) + fadeIn(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    ) + expandVertically(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ),
                    exit = slideOutVertically(
                        animationSpec = tween(300, easing = FastOutSlowInEasing),
                        targetOffsetY = { it / 2 }
                    ) + fadeOut(
                        animationSpec = tween(200)
                    ) + shrinkVertically(
                        animationSpec = tween(300, easing = FastOutSlowInEasing)
                    )
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .padding(top = 8.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data("https://picsum.photos/400/200?random=2")
                                .crossfade(true)
                                .build(),
                            contentDescription = "Result image",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }

        // Emoji Selection
        CustomizationSection(title = "Choose Emoji") {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(Constants.getEmojis()) { emoji ->
                    EmojiChip(
                        emoji = emoji,
                        isSelected = emoji == selectedEmoji,
                        onClick = { selectedEmoji = emoji }
                    )
                }
            }
        }

        // Color Selection
        CustomizationSection(title = "Choose Colors") {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(Constants.getGradients()) { colorPair ->
                    ColorChip(
                        colors = colorPair,
                        isSelected = colorPair == selectedColors,
                        onClick = { selectedColors = colorPair }
                    )
                }
            }
        }

        // Settings with improved clickable areas
        CustomizationSection(title = "Behavior Settings") {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                ClickableSettingRow(
                    title = "Floating Direction",
                    description = "Direction of floating emoji animation",
                    onClick = {
                        floatingDirection = if (floatingDirection == FloatingEmojiDirection.UP)
                            FloatingEmojiDirection.DOWN else FloatingEmojiDirection.UP
                    }
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            onClick = { floatingDirection = FloatingEmojiDirection.UP },
                            label = { Text("Up") },
                            selected = floatingDirection == FloatingEmojiDirection.UP,
                            leadingIcon = { Icon(Icons.Default.KeyboardArrowUp, null) }
                        )
                        FilterChip(
                            onClick = { floatingDirection = FloatingEmojiDirection.DOWN },
                            label = { Text("Down") },
                            selected = floatingDirection == FloatingEmojiDirection.DOWN,
                            leadingIcon = { Icon(Icons.Default.KeyboardArrowDown, null) }
                        )
                    }
                }

                ClickableSettingRow(
                    title = "Show Average Indicator",
                    description = "Display average value indicator on slider",
                    onClick = { showAverage = !showAverage }
                ) {
                    Switch(
                        checked = showAverage,
                        onCheckedChange = { showAverage = it }
                    )
                }

                // Animated average value setting
                AnimatedVisibility(
                    visible = showAverage,
                    enter = expandVertically(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ) + fadeIn(),
                    exit = shrinkVertically(
                        animationSpec = tween(300)
                    ) + fadeOut()
                ) {
                    SettingRow(
                        title = "Average Value",
                        description = "Set the average value position"
                    ) {
                        Column(horizontalAlignment = Alignment.End) {
                            Slider(
                                value = averageValue,
                                onValueChange = { averageValue = it },
                                modifier = Modifier.width(140.dp)
                            )
                            Text(
                                text = "${(averageValue * 100).toInt()}%",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                ClickableSettingRow(
                    title = "Allow Reselection",
                    description = "Allow changing value after initial selection",
                    onClick = {
                        allowReselection = !allowReselection
                        if (allowReselection) isValueSelected = false
                    }
                ) {
                    Switch(
                        checked = allowReselection,
                        onCheckedChange = {
                            allowReselection = it
                            if (it) isValueSelected = false
                        }
                    )
                }

                ClickableSettingRow(
                    title = "Show Result Image",
                    description = "Display custom image when value is selected",
                    onClick = { showResultImage = !showResultImage }
                ) {
                    Switch(
                        checked = showResultImage,
                        onCheckedChange = { showResultImage = it }
                    )
                }

                // Additional helpful settings
                if (!allowReselection && isValueSelected) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Drag sliders to see floating emojis! They should appear above all content thanks to the global particle system.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CustomizationSection(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
            content()
        }
    }
}

@Composable
private fun ClickableSettingRow(
    title: String,
    description: String,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        content()
    }
}

@Composable
private fun EmojiChip(
    emoji: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "emoji_scale"
    )

    Box(
        modifier = Modifier
            .size(56.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(
                if (isSelected) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surfaceVariant
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = emoji,
            style = MaterialTheme.typography.headlineSmall
        )

        // Selection indicator
        if (isSelected) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    )
            )
        }
    }
}

@Composable
private fun ColorChip(
    colors: Pair<Int, Int>,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "color_scale"
    )

    Box(
        modifier = Modifier
            .size(56.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(
                Brush.horizontalGradient(
                    listOf(Color(colors.first), Color(colors.second))
                )
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = isSelected,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            Icon(
                Icons.Default.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .size(24.dp)
                    .background(
                        Color.Black.copy(alpha = 0.3f),
                        CircleShape
                    )
                    .padding(4.dp)
            )
        }
    }
}

@Composable
private fun SettingRow(
    title: String,
    description: String,
    content: @Composable () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        content()
    }
}
