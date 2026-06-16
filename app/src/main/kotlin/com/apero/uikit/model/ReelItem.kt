package com.apero.uikit.model

import androidx.compose.runtime.Immutable

/** Model for a reel page — supports both video and image-only items. */
@Immutable
data class ReelItem(
    val id: String,
    val title: String,
    val thumbnailUrl: String,
    val videoUrl: String? = null,
)
