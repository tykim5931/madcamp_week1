package com.example.tabexample.data

import com.example.tabexample.R
import com.example.tabexample.model.GalleryImage

class GalleryDatasource {
    fun loadGallery(): List<GalleryImage> {
        return listOf<GalleryImage>(
            GalleryImage(R.drawable.shiba01),
            GalleryImage(R.drawable.shiba02),
            GalleryImage(R.drawable.shiba03),
            GalleryImage(R.drawable.shiba04),
            GalleryImage(R.drawable.shiba05),
            GalleryImage(R.drawable.shiba06),
            GalleryImage(R.drawable.shiba07),
            GalleryImage(R.drawable.shiba08),
            GalleryImage(R.drawable.shiba09),
            GalleryImage(R.drawable.shiba10),
            GalleryImage(R.drawable.shiba11),
            GalleryImage(R.drawable.shiba12),
            GalleryImage(R.drawable.shiba13),
            GalleryImage(R.drawable.shiba14),
            GalleryImage(R.drawable.shiba15),
            GalleryImage(R.drawable.shiba16),
            GalleryImage(R.drawable.shiba17),
            GalleryImage(R.drawable.shiba18),
            GalleryImage(R.drawable.shiba19),
            GalleryImage(R.drawable.shiba20)
        )
    }
}