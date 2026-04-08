package com.apero.smoothgallery.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.apero.smoothgallery.model.GalleryImage
import kotlinx.coroutines.delay

class GalleryPagingSource : PagingSource<Int, GalleryImage>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GalleryImage> {
        val page = params.key ?: 0
        // Cap at 50 pages — picsum IDs above ~1084 return 404
        if (page >= MAX_PAGES) {
            return LoadResult.Page(data = emptyList(), prevKey = page - 1, nextKey = null)
        }
        // Simulate network latency for realistic shimmer demo
        delay(if (page == 0) 300L else 500L)
        val data = MockData.getGridPage(page, params.loadSize)
        return LoadResult.Page(
            data = data,
            prevKey = if (page > 0) page - 1 else null,
            nextKey = if (page < MAX_PAGES - 1) page + 1 else null,
        )
    }

    companion object {
        private const val MAX_PAGES = 50
    }

    override fun getRefreshKey(state: PagingState<Int, GalleryImage>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
    }
}
