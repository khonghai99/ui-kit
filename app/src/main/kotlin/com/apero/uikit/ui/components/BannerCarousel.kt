@file:OptIn(ExperimentalMaterial3Api::class)

package com.apero.uikit.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.apero.uikit.model.GalleryImage
import com.apero.uikit.ui.components.carousel.HeroCenterCarousel

/** Convenience wrapper: hero carousel for [GalleryImage] lists. */
@Composable
fun BannerCarousel(
    items: List<GalleryImage>,
    modifier: Modifier = Modifier,
) {
    HeroCenterCarousel(
        items = items,
        modifier = modifier,
    ) { image ->
        CarouselBannerItem(image = image)
    }
}
