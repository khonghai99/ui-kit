package com.apero.smoothgallery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.apero.smoothgallery.ui.screen.GalleryScreen
import com.apero.smoothgallery.ui.theme.SmoothGalleryTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            SmoothGalleryTheme {
                GalleryScreen()
            }
        }
    }
}
