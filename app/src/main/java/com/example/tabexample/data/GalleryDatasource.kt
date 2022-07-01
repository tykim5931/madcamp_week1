package com.example.tabexample.data

import android.content.Context
import android.os.Environment
import android.util.Log
import com.beust.klaxon.Klaxon
import com.example.tabexample.R
import com.example.tabexample.model.GalleryImage
import org.json.JSONArray
import org.json.JSONObject
import java.io.*

class GalleryDatasource(private val context: Context) {

    private fun loadJSONString(): String? {
        val fileName = "images.json"
        val jsonString: String
        try{
            jsonString = context.openFileInput(fileName).bufferedReader().use {
                it.readText()
            }
        } catch (ex: Exception) {
            when(ex) {
                is FileNotFoundException -> {
                    val newFile = File(context.filesDir, fileName)
                    newFile.createNewFile()
                    val json = JSONArray()
                    context.openFileOutput(fileName, Context.MODE_PRIVATE).use {
                        it.write(json.toString().toByteArray())
                        it.close()
                    }
                    return json.toString()
                }
                else -> {
                    ex.printStackTrace()
                    return null
                }
            }
        }
        return jsonString
    }

    fun loadGallery(): List<GalleryImage> {
        val jsonString = loadJSONString()!!
        val gallery = Klaxon()
            .parseArray<GalleryImage>(jsonString)
        Log.d("GalleryDataSource", "gallery.size : ${gallery?.size}")
        return gallery!!
    }
}