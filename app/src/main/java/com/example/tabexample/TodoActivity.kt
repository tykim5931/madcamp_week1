package com.example.tabexample

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
import java.text.SimpleDateFormat
import java.util.*

class TodoActivity : AppCompatActivity() {
    private var _binding: ActivityTodoBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)   // action bar 제거
        // setContentView(R.layout.activity_todo)
        _binding = ActivityTodoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val recievedIntent: Intent = getIntent()
        val calDate: String? = recievedIntent.getStringExtra("date")
        Log.i("Tag","recieved Date: $calDate")
        binding.dateText.text = calDate

        binding.finish.setOnClickListener(){
            val contents = binding.editTextContents.text.toString()
            if(contents.isEmpty()){
                val toast = Toast.makeText(this, "Please write valid info", Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.TOP,0,0)
                toast.show()
            }
            else{
                var now: Long = System.currentTimeMillis()
                val nowdate: Date = Date(now)
                val dateFormat: SimpleDateFormat = SimpleDateFormat("yyMMddhhmmss")
                intent.putExtra("id", dateFormat.format(nowdate) + UUID.randomUUID().toString())
                println( dateFormat.format(nowdate))
                intent.putExtra("contents", contents)
                setResult(RESULT_OK, intent)
                finish()
            }
        }
    }
}