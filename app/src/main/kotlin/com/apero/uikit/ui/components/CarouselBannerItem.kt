@file:OptIn(ExperimentalMaterial3Api::class)

package com.apero.uikit.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.CarouselItemScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.apero.uikit.model.GalleryImage
import com.apero.uikit.ui.components.carousel.CarouselItemContainer
import com.apero.uikit.ui.components.carousel.CarouselItemDefaults

/** Single banner item — delegates to [CarouselItemContainer] for gradient + masking. */
@Composable
fun CarouselItemScope.CarouselBannerItem(
    image: GalleryImage,
    modifier: Modifier = Modifier,
    shape: Shape = CarouselItemDefaults.Shape,
) {
    CarouselItemContainer(
        modifier = modifier,
        maskShape = rememberMaskShape(shape),
        imageContent = {
            OptimizedAsyncImage(
                model = image.imageUrl,
                contentDescription = image.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        },
        overlayContent = {
            Text(
                text = image.title,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp),
            )
        },
    )
}
