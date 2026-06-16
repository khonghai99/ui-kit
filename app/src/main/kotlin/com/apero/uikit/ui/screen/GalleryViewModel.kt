package com.apero.uikit.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.apero.uikit.data.GalleryRepository
import com.apero.uikit.model.GalleryImage
import com.apero.uikit.model.ReelItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    repository: GalleryRepository,
) : ViewModel() {

    val trendingImages: StateFlow<List<GalleryImage>> = repository.getTrendingImages()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val reelItems: StateFlow<List<ReelItem>> = repository.getReelItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val gridImages = repository.getImagesPager().cachedIn(viewModelScope)
}
