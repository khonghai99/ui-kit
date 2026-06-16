package com.apero.uikit.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.apero.uikit.model.GalleryImage
import com.apero.uikit.model.ReelItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GalleryRepository @Inject constructor() {

    fun getTrendingImages(): Flow<List<GalleryImage>> {
        return flowOf(MockData.trendingImages)
    }

    fun getReelItems(): Flow<List<ReelItem>> = flowOf(MockData.reelItems)

    fun getImagesPager(): Flow<PagingData<GalleryImage>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                prefetchDistance = 5,
                initialLoadSize = 20, // Must match pageSize — MockData IDs derive from page*loadSize
            ),
        ) {
            GalleryPagingSource()
        }.flow
    }
}
