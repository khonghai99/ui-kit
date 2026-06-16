package com.apero.uikit.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import androidx.compose.material3.Button
import com.apero.uikit.ui.components.CarouselBannerItem
import com.apero.uikit.ui.components.ImageGridItem
import com.apero.uikit.ui.components.carousel.HeroCenterCarousel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(
    viewModel: GalleryViewModel = hiltViewModel(),
    onNavigateToReel: () -> Unit = {},
    onNavigateToBottomNav: () -> Unit = {},
) {
    val trending by viewModel.trendingImages.collectAsStateWithLifecycle()
    val gridItems = viewModel.gridImages.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("UI Kit — Carousel Demo") })
        },
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 150.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // Hero carousel section
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column {
                    Text(
                        "Hero Carousel",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 8.dp),
                    )
                    HeroCenterCarousel(items = trending, infiniteScroll = true) { image ->
                        CarouselBannerItem(image = image)
                    }

                    Spacer(Modifier.height(16.dp))

                    Button(onClick = onNavigateToReel) {
                        Text("Open Reel Demo")
                    }

                    Spacer(Modifier.height(12.dp))

                    Button(onClick = onNavigateToBottomNav) {
                        Text("Open Bottom Nav Demo")
                    }

                    Spacer(Modifier.height(24.dp))

                    Text(
                        "Gallery",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 8.dp),
                    )
                }
            }

            // Grid items with stable keys
            items(
                count = gridItems.itemCount,
                key = gridItems.itemKey { it.id },
                contentType = gridItems.itemContentType { "gallery" },
            ) { index ->
                ImageGridItem(image = gridItems[index])
            }
        }
    }
}
