package com.example.tabexample.model

import android.net.Uri
import com.beust.klaxon.Json


data class GalleryImage(
    @Json(name = "imageUriString")
    val imageUriString: String
)