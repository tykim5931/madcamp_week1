package com.example.tabexample.model

data class ToDoItem (var id: String, var date:String?, var done:Int?, val contents:String?)

// done 0: In progress 1: done 2: postpone