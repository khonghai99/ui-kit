package com.apero.composa.data

import com.apero.composa.model.GalleryImage
import com.apero.composa.model.ReelItem

/** Picsum-based mock data — no API key required. */
object MockData {

    val trendingImages: List<GalleryImage> = listOf(
        GalleryImage("t1", "Mountain Lake", "https://picsum.photos/id/10/800/450", "https://picsum.photos/id/10/400/225"),
        GalleryImage("t2", "Autumn Forest", "https://picsum.photos/id/15/800/450", "https://picsum.photos/id/15/400/225"),
        GalleryImage("t3", "City Lights", "https://picsum.photos/id/20/800/450", "https://picsum.photos/id/20/400/225"),
        GalleryImage("t4", "Ocean Waves", "https://picsum.photos/id/25/800/450", "https://picsum.photos/id/25/400/225"),
        GalleryImage("t5", "Snow Peaks", "https://picsum.photos/id/29/800/450", "https://picsum.photos/id/29/400/225"),
    )

    /** Google public sample videos — short 15s clips, reliable CDN. */
    private const val VIDEO_BUCKET =
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample"

    val reelItems: List<ReelItem> = listOf(
        // Video items (Google sample videos — 15-30s clips)
        ReelItem("v1", "Blazing Fire", "https://picsum.photos/id/1015/540/960", "$VIDEO_BUCKET/ForBiggerBlazes.mp4"),
        ReelItem("v2", "Great Escape", "https://picsum.photos/id/1018/540/960", "$VIDEO_BUCKET/ForBiggerEscapes.mp4"),
        ReelItem("v3", "Fun Times", "https://picsum.photos/id/1035/540/960", "$VIDEO_BUCKET/ForBiggerFun.mp4"),
        ReelItem("v4", "Joyride", "https://picsum.photos/id/1039/540/960", "$VIDEO_BUCKET/ForBiggerJoyrides.mp4"),
        ReelItem("v5", "Meltdown", "https://picsum.photos/id/1036/540/960", "$VIDEO_BUCKET/ForBiggerMeltdowns.mp4"),
        ReelItem("v6", "Subaru Stars", "https://picsum.photos/id/1040/540/960", "$VIDEO_BUCKET/SubaruOutbackOnStreetAndDirt.mp4"),
        ReelItem("v7", "Volkswagen GTI", "https://picsum.photos/id/1044/540/960", "$VIDEO_BUCKET/VolkswagenGTIReview.mp4"),
        ReelItem("v8", "We Are Going", "https://picsum.photos/id/1047/540/960", "$VIDEO_BUCKET/WeAreGoingOnBullrun.mp4"),
        // Image-only items (Ken Burns animated)
        ReelItem("i1", "Waterfall", "https://picsum.photos/id/1015/1080/1920"),
        ReelItem("i2", "Forest Path", "https://picsum.photos/id/1018/1080/1920"),
        ReelItem("i3", "Mountain View", "https://picsum.photos/id/1035/1080/1920"),
        ReelItem("i4", "Autumn Road", "https://picsum.photos/id/1039/1080/1920"),
        ReelItem("i5", "Misty Lake", "https://picsum.photos/id/1036/1080/1920"),
        ReelItem("i6", "Desert Sunset", "https://picsum.photos/id/1040/1080/1920"),
        ReelItem("i7", "Ocean Cliff", "https://picsum.photos/id/1044/1080/1920"),
        ReelItem("i8", "Snow Forest", "https://picsum.photos/id/1047/1080/1920"),
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
