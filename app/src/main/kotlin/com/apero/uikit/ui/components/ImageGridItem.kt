package com.apero.uikit.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.apero.uikit.model.GalleryImage

/** Single grid cell — uses thumbnailUrl for smaller decode. */
@Composable
fun ImageGridItem(
    image: GalleryImage?,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.aspectRatio(3f / 2f),
        shape = MaterialTheme.shapes.medium,
    ) {
        Box {
            if (image != null) {
                OptimizedAsyncImage(
                    model = image.thumbnailUrl,
                    contentDescription = image.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
                Text(
                    text = image.title,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.4f))
                        .padding(8.dp),
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            } else {
                // Shimmer placeholder
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .shimmerPlaceholder(visible = true),
                )
            }
        }
    }
}
