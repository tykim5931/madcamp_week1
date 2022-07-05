package com.example.tabexample.data

import android.content.Context
import com.beust.klaxon.Klaxon
import com.example.tabexample.model.Phone
import com.example.tabexample.model.ToDoItem
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileNotFoundException

class ToDoSource(private val context: Context) {
    private fun loadJSON(): String? {
        val fileName = "todolist.json"
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

    fun loadTodoList(): List<ToDoItem>? {
        val jsonString = loadJSON()!!
        var todolist = Klaxon().parseArray<ToDoItem>(jsonString)
        return todolist
    }

    fun saveTodoList(todoList:List<ToDoItem>){
//
//        val json = JSONArray()
//        for (item in todoList)
//        {
//            val jsonObject = JSONObject()
//            jsonObject.put("id", item.id)
//            jsonObject.put("date", item.date)
//            jsonObject.put("contents", item.contents)
//            jsonObject.put("done", item.done)
//            json.put(jsonObject)
//        }
        val fileName = "todolist.json"
        context.openFileOutput(fileName, Context.MODE_PRIVATE).use {
//            it.write(json.toString().toByteArray())
            it.write(Klaxon().toJsonString(todoList).toByteArray())
            it.close()
        }
    }
}