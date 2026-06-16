package com.apero.composa.ui.components.reel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import kotlinx.coroutines.delay

/**
 * Snapshot of playback state at a point in time.
 *
 * Generic — works with any player type. Consumer constructs this from their player's API.
 */
@Immutable
data class ReelPlaybackState(
    val position: Long = 0L,
    val duration: Long = 0L,
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
) {
    /** Progress as a 0..1 float. Safe against zero-duration. */
    val progress: Float
        get() = if (duration > 0) (position.toFloat() / duration.toFloat()).coerceIn(0f, 1f) else 0f
}

/**
 * Polls playback state at [intervalMs] while [isActive].
 *
 * Consumer provides [readState] lambda to read from their specific player (ExoPlayer, MediaPlayer,
 * VideoView, etc.) — no media dependency in this composable.
 *
 * Usage:
 * ```
 * val playback by rememberReelPlaybackState(isActive) {
 *     ReelPlaybackState(
 *         position = player.currentPosition,
 *         duration = player.duration,
 *         isPlaying = player.isPlaying,
 *         isBuffering = player.playbackState == STATE_BUFFERING,
 *     )
 * }
 * ```
 *
 * @param isActive Only polls while true. Resets to default when false.
 * @param intervalMs Polling interval in milliseconds (default 250ms, matching Flow's pattern)
 * @param readState Lambda that reads current state from the player
 */
@Composable
fun rememberReelPlaybackState(
    isActive: Boolean,
    intervalMs: Long = 250L,
    readState: () -> ReelPlaybackState,
): State<ReelPlaybackState> {
    val state = remember { mutableStateOf(ReelPlaybackState()) }
    val currentReadState by rememberUpdatedState(readState)

    LaunchedEffect(isActive) {
        if (isActive) {
            while (true) {
                state.value = currentReadState()
                delay(intervalMs)
            }
        } else {
            state.value = ReelPlaybackState()
        }
    }

    return state
}
