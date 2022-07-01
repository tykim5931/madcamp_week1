package com.example.tabexample.data

import android.content.Context
import com.beust.klaxon.Klaxon
import com.example.tabexample.model.Phone
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileNotFoundException

class PhoneBookSource(private val context: Context) {
    private fun loadJSON(): String? {
        val fileName = "contacts.json"
        val jsonString: String
        try {
            jsonString = context.openFileInput(fileName).bufferedReader().use {
                it.readText()
            }
        } catch (ex: Exception) {
            when (ex) {
                is FileNotFoundException -> {
                    val newFile = File(context.filesDir, fileName)
                    newFile.createNewFile()
                    val json = JSONArray()
                    context.openFileOutput(fileName, Context.MODE_PRIVATE).use {
                        it.write(json.toString().toByteArray())
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

    fun loadPhoneBook(): List<Phone>? {
        val jsonString = loadJSON()!!
        var phonelist = Klaxon().parseArray<Phone>(jsonString)
        return phonelist
    }

    fun savePhoneBook(phoneList:MutableList<Phone>){
        val json = JSONArray()
        for (item in phoneList)
        {
            val jsonObject = JSONObject()
            jsonObject.put("id", item.id)
            jsonObject.put("name", item.name)
            jsonObject.put("phone", item.phone)
            json.put(jsonObject)
        }
        val fileName = "contacts.json"
        context.openFileOutput(fileName, Context.MODE_PRIVATE).use {
            it.write(json.toString().toByteArray())
            it.close()
        }
    }
}