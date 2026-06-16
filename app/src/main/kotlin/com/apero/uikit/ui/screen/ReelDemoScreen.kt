package com.apero.uikit.ui.screen

import android.net.Uri
import android.widget.VideoView
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.apero.uikit.model.ReelItem
import com.apero.uikit.ui.components.OptimizedAsyncImage
import com.apero.uikit.ui.components.reel.VerticalReelPager

/**
 * Demo screen showcasing reel components with video + Ken Burns animated images.
 *
 * Video items play via built-in VideoView (no ExoPlayer/Media3).
 * Image items animate with Ken Burns (slow zoom + pan) for motion feel.
 * All overlay components (indicators, like animation, action buttons) are wired in.
 */
@Composable
fun ReelDemoScreen(
    reelItems: List<ReelItem>,
    onBack: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        VerticalReelPager(
            items = reelItems,
            key = { reelItems[it].id },
        ) { item, isActive ->
            ReelDemoPage(item = item, isActive = isActive)
        }

        // Back button overlay
        Surface(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(12.dp)
                .size(40.dp),
            shape = CircleShape,
            color = Color.Black.copy(alpha = 0.5f),
        ) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("\u2190", color = Color.White, style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

/** Simple video player using built-in [VideoView] — no Media3/ExoPlayer dependency. */
@Composable
internal fun ReelVideoPlayer(
    videoUrl: String,
    isActive: Boolean,
    onViewReady: (VideoView) -> Unit = {},
) {
    DisposableEffect(Unit) {
        onDispose { /* VideoView lifecycle managed by AndroidView */ }
    }

    AndroidView(
        factory = { ctx ->
            VideoView(ctx).apply {
                setVideoURI(Uri.parse(videoUrl))
                setOnPreparedListener { mp ->
                    mp.isLooping = true
                    mp.setVolume(0f, 0f)
                    if (isActive) mp.start()
                }
                setOnErrorListener { _, _, _ -> true }
                onViewReady(this)
            }
        },
        modifier = Modifier.fillMaxSize(),
        update = { view ->
            try {
                if (isActive && !view.isPlaying) view.start()
                else if (!isActive && view.isPlaying) view.pause()
            } catch (_: IllegalStateException) { }
        },
    )
}

/** Ken Burns effect — slow zoom + pan creating motion from static images. */
@Composable
internal fun KenBurnsImage(imageUrl: String, title: String) {
    val transition = rememberInfiniteTransition(label = "kenBurns")
    val scale by transition.animateFloat(
        initialValue = 1.0f, targetValue = 1.15f,
        animationSpec = infiniteRepeatable(tween(8000, easing = LinearEasing), RepeatMode.Reverse),
        label = "scale",
    )
    val panX by transition.animateFloat(
        initialValue = -15f, targetValue = 15f,
        animationSpec = infiniteRepeatable(tween(10000, easing = LinearEasing), RepeatMode.Reverse),
        label = "panX",
    )
    OptimizedAsyncImage(
        model = imageUrl,
        contentDescription = title,
        modifier = Modifier.fillMaxSize().graphicsLayer { scaleX = scale; scaleY = scale; translationX = panX },
    )
}
