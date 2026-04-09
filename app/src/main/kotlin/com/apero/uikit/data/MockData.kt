package com.apero.uikit.data

import com.apero.uikit.model.GalleryImage

/** Picsum-based mock data — no API key required. */
object MockData {

    val trendingImages: List<GalleryImage> = listOf(
        GalleryImage("t1", "Mountain Lake", "https://picsum.photos/id/10/800/450", "https://picsum.photos/id/10/400/225"),
        GalleryImage("t2", "Autumn Forest", "https://picsum.photos/id/15/800/450", "https://picsum.photos/id/15/400/225"),
        GalleryImage("t3", "City Lights", "https://picsum.photos/id/20/800/450", "https://picsum.photos/id/20/400/225"),
        GalleryImage("t4", "Ocean Waves", "https://picsum.photos/id/25/800/450", "https://picsum.photos/id/25/400/225"),
        GalleryImage("t5", "Snow Peaks", "https://picsum.photos/id/29/800/450", "https://picsum.photos/id/29/400/225"),
    )

    fun getGridPage(page: Int, pageSize: Int): List<GalleryImage> {
        val startId = page * pageSize + 1
        return (startId until startId + pageSize).map { id ->
            GalleryImage(
                id = "g$id",
                title = "Photo #$id",
                imageUrl = "https://picsum.photos/id/$id/600/400",
                thumbnailUrl = "https://picsum.photos/id/$id/300/200",
            )
        }
    }
}
