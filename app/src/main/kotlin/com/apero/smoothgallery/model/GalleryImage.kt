package com.apero.smoothgallery.model

import androidx.compose.runtime.Immutable

@Immutable
data class GalleryImage(
    val id: String,
    val title: String,
    val imageUrl: String,
    val thumbnailUrl: String,
)
