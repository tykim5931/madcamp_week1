package com.example.tabexample.data

import android.content.Context
import android.graphics.BitmapFactory
import com.example.tabexample.model.GalleryImage
import java.io.*


class GalleryDatasource(private val context: Context) {

    fun loadGallery(): List<GalleryImage> {
        val dirName = "images"
        try {
            val dir = File(context.filesDir, dirName)
            if(!dir.exists()) {
                dir.mkdir()
            }
            val fileList = dir.listFiles()
            fileList?.let {
                val fileInfoList = fileList.map{Pair (it.name,
                    BitmapFactory.decodeStream(FileInputStream(it)))}
                return fileInfoList.map{GalleryImage(id=it.first, bitmap = it.second)}
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        return listOf<GalleryImage>()
    }
}