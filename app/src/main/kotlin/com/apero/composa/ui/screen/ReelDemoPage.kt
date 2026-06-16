package com.apero.composa.ui.screen

import android.widget.Toast
import android.widget.VideoView
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apero.composa.model.ReelItem
import com.apero.composa.ui.components.reel.ReelActionButton
import com.apero.composa.ui.components.reel.ReelActionButtonColumn
import com.apero.composa.ui.components.reel.ReelBufferingIndicator
import com.apero.composa.ui.components.reel.ReelGestureDetector
import com.apero.composa.ui.components.reel.ReelLikeAnimation
import com.apero.composa.ui.components.reel.ReelPageContainer
import com.apero.composa.ui.components.reel.ReelPlayPauseIndicator
import com.apero.composa.ui.components.reel.ReelPlaybackState
import com.apero.composa.ui.components.reel.ReelProgressIndicator
import com.apero.composa.ui.components.reel.ReelSpeedIndicator
import com.apero.composa.ui.components.reel.rememberReelPlaybackState

/** Single reel page — wires all overlay components for the demo. */
@Composable
internal fun ReelDemoPage(item: ReelItem, isActive: Boolean) {
    val context = LocalContext.current

    // Overlay states
    var showPlayPause by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(true) }
    var showLike by remember { mutableStateOf(false) }
    var isFastForward by remember { mutableStateOf(false) }

    // Reset play state when page becomes active/inactive
    LaunchedEffect(isActive) { if (isActive) isPlaying = true }

    // Simulated progress for image items (5-second loop)
    val infiniteTransition = rememberInfiniteTransition(label = "reelProgress")
    val simulatedProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "progress",
    )

    // Real playback state for video items
    var videoViewRef by remember { mutableStateOf<VideoView?>(null) }
    val playback by rememberReelPlaybackState(isActive && item.videoUrl != null) {
        val view = videoViewRef
        if (view != null) {
            try {
                ReelPlaybackState(
                    position = view.currentPosition.toLong(),
                    duration = view.duration.toLong(),
                    isPlaying = view.isPlaying,
                    isBuffering = false,
                )
            } catch (_: IllegalStateException) { ReelPlaybackState() }
        } else ReelPlaybackState()
    }

    ReelGestureDetector(
        onTap = {
            isPlaying = !isPlaying
            showPlayPause = true
            // Toggle video playback
            try {
                val view = videoViewRef
                if (view != null) {
                    if (view.isPlaying) view.pause() else view.start()
                }
            } catch (_: IllegalStateException) { }
        },
        onDoubleTap = {
            showLike = true
            Toast.makeText(context, "\u2764 ${item.title}", Toast.LENGTH_SHORT).show()
        },
        onLongPress = { isFastForward = true },
        onLongPressRelease = { isFastForward = false },
    ) {
        ReelPageContainer(
            mediaContent = {
                if (item.videoUrl != null) {
                    ReelVideoPlayer(
                        videoUrl = item.videoUrl,
                        isActive = isActive,
                        onViewReady = { videoViewRef = it },
                    )
                } else {
                    KenBurnsImage(imageUrl = item.thumbnailUrl, title = item.title)
                }
            },
            overlayContent = {
                // Center overlays
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    ReelPlayPauseIndicator(
                        visible = showPlayPause,
                        isPlaying = isPlaying,
                        onHide = { showPlayPause = false },
                    )
                    ReelLikeAnimation(
                        visible = showLike,
                        onAnimationEnd = { showLike = false },
                    )
                    ReelBufferingIndicator(visible = playback.isBuffering)
                }

                // Speed indicator at top
                ReelSpeedIndicator(
                    visible = isFastForward,
                    modifier = Modifier.align(Alignment.TopCenter).padding(top = 80.dp),
                )

                // Action buttons on right
                ReelActionButtonColumn(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 12.dp, bottom = 80.dp)
                        .navigationBarsPadding(),
                ) {
                    ReelActionButton(
                        label = "Like",
                        onClick = { showLike = true },
                        icon = { Text("\u2764", fontSize = 22.sp) },
                    )
                    ReelActionButton(
                        label = "Comment",
                        onClick = { Toast.makeText(context, "Comments", Toast.LENGTH_SHORT).show() },
                        icon = { Text("\uD83D\uDCAC", fontSize = 22.sp) },
                    )
                    ReelActionButton(
                        label = "Share",
                        onClick = { Toast.makeText(context, "Share", Toast.LENGTH_SHORT).show() },
                        icon = { Text("\u2197", fontSize = 22.sp) },
                    )
                }

                // Title at bottom-left
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 16.dp, bottom = 48.dp, end = 72.dp),
                )

                // Progress bar at bottom
                val progress = if (item.videoUrl != null) playback.progress else simulatedProgress
                ReelProgressIndicator(
                    progress = if (isActive) progress else 0f,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 2.dp),
                )
            },
        )
    }
}
