package com.bernaferrari.emojislidersample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bernaferrari.emojislidersample.ui.screens.AboutScreen
import com.bernaferrari.emojislidersample.ui.screens.CustomizeScreen
import com.bernaferrari.emojislidersample.ui.screens.ShowcaseScreen
import com.bernaferrari.emojislidersample.ui.theme.EmojiSliderTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            EmojiSliderTheme {
                // Simple approach - no complex particle system needed
                EmojiSliderApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmojiSliderApp() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val currentRoute = navController.currentDestination?.route

                NavigationBarItem(
                    icon = { Icon(Icons.Default.Apps, contentDescription = null) },
                    label = { Text("Showcase") },
                    selected = currentRoute == "showcase",
                    onClick = {
                        if (currentRoute != "showcase") {
                            navController.navigate("showcase") {
                                popUpTo("showcase") { inclusive = true }
                            }
                        }
                    }
                )

                NavigationBarItem(
                    icon = { Icon(Icons.Default.Tune, contentDescription = null) },
                    label = { Text("Customize") },
                    selected = currentRoute == "customize",
                    onClick = {
                        if (currentRoute != "customize") {
                            navController.navigate("customize") {
                                popUpTo("showcase")
                            }
                        }
                    }
                )

                NavigationBarItem(
                    icon = { Icon(Icons.Default.Info, contentDescription = null) },
                    label = { Text("About") },
                    selected = currentRoute == "about",
                    onClick = {
                        if (currentRoute != "about") {
                            navController.navigate("about") {
                                popUpTo("showcase")
                            }
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "showcase",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("showcase") { ShowcaseScreen() }
            composable("customize") { CustomizeScreen() }
            composable("about") { AboutScreen() }
        }
    }
}

