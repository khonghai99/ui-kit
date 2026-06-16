package com.apero.uikit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.apero.uikit.ui.screen.BottomNavigationDemoScreen
import com.apero.uikit.ui.screen.GalleryScreen
import com.apero.uikit.ui.theme.UiKitTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            UiKitTheme {
                var currentScreen by rememberSaveable { mutableStateOf("gallery") }

                when (currentScreen) {
                    "gallery" -> GalleryScreen(
                        onNavigateToBottomNav = { currentScreen = "bottomNav" },
                    )
                    "bottomNav" -> BottomNavigationDemoScreen(
                        onBack = { currentScreen = "gallery" },
                    )
                }
            }
        }
    }
}
