package com.example.tabexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.tabexample.data.PhoneBookSource
import com.example.tabexample.data.ToDoSource
import com.example.tabexample.databinding.ActivityTodoBinding
import com.example.tabexample.databinding.FragmentGalleryBinding
import com.example.tabexample.model.ToDoItem
import org.w3c.dom.Text

class TodoActivity : AppCompatActivity() {
    private var _binding: ActivityTodoBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)   // action bar 제거
        // setContentView(R.layout.activity_todo)
        _binding = ActivityTodoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.finish.setOnClickListener(){
            val date = binding.editTextDate.text.toString()
            val contents = binding.editTextContents.text.toString()
            if(!checkValidDate(date) || contents.isEmpty()){
                val toast = Toast.makeText(this, "Please write valid info", Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.TOP,0,0)
                toast.show()
            }
            else{
                var todoList = ToDoSource(this).loadTodoList() as MutableList<ToDoItem>
                todoList.add(ToDoItem(date,false, contents))
                ToDoSource(this).saveTodoList(todoList)
                finish()
            }
        }
    }
    fun checkValidDate(date: String):Boolean{
        if(date.isEmpty() || date.length > 6) return false //여러 조건 추가해야함
        else return true
    }
}