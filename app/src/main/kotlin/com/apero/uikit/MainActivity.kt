package com.apero.uikit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.apero.uikit.ui.screen.BottomNavigationDemoScreen
import com.apero.uikit.ui.screen.GalleryScreen
import com.apero.uikit.ui.screen.GalleryViewModel
import com.apero.uikit.ui.screen.ReelDemoScreen
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
                        onNavigateToReel = { currentScreen = "reel" },
                        onNavigateToBottomNav = { currentScreen = "bottomNav" },
                    )
                    "reel" -> {
                        val vm: GalleryViewModel = hiltViewModel()
                        val reelItems by vm.reelItems.collectAsStateWithLifecycle()
                        ReelDemoScreen(
                            reelItems = reelItems,
                            onBack = { currentScreen = "gallery" },
                        )
                    }
                    "bottomNav" -> BottomNavigationDemoScreen(
                        onBack = { currentScreen = "gallery" },
                    )
                }
            }
        }
    }
}
