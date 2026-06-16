package com.apero.uikit.ui.components.reel

/**
 * Generic interface for an N-player pool used with [VerticalReelPager].
 *
 * Consumers implement this for their specific player type (Media3 ExoPlayer, MediaPlayer, etc.).
 * The pool manages a fixed number of player instances and maps content indices to player slots
 * via modulo arithmetic.
 *
 * This is a pure contract — no media dependencies. The interface documents the lifecycle/activation
 * pattern; consumers configure players themselves via [getPlayer].
 *
 * Usage example:
 * ```
 * class ExoPlayerPool(count: Int) : ReelPlayerPool<ExoPlayer> {
 *     private val players = List(count) { ExoPlayer.Builder(context).build() }
 *     override fun getPlayer(index: Int) = players[index % players.size]
 *     override fun activate(index: Int) { ... }
 *     override fun releaseDistant(currentIndex: Int) { ... }
 *     override fun releaseAll() { players.forEach { it.release() } }
 * }
 * ```
 *
 * @param P The player type (e.g., ExoPlayer, MediaPlayer)
 */
interface ReelPlayerPool<P> {

    /** Get the player instance mapped to [index] (typically index % poolSize). */
    fun getPlayer(index: Int): P?

    /** Activate the player at [index] (play) and pause all others. */
    fun activate(index: Int)

    /** Release players far from [currentIndex] to free resources. */
    fun releaseDistant(currentIndex: Int)

    /** Release all players. Call when leaving the reel screen. */
    fun releaseAll()
}
