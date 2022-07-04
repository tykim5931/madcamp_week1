package com.example.tabexample

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tabexample.adapter.TodoAdapter
import com.example.tabexample.data.PhoneBookSource
import com.example.tabexample.data.ToDoSource
import com.example.tabexample.databinding.FragmentContactBinding
import com.example.tabexample.databinding.FragmentTodoBinding
import com.example.tabexample.model.CheckBoxData
import com.example.tabexample.model.Phone
import com.example.tabexample.model.ToDoItem
import com.shrikanthravi.collapsiblecalendarview.data.Day
import com.shrikanthravi.collapsiblecalendarview.widget.CollapsibleCalendar
import java.text.SimpleDateFormat

class Fragment03 : Fragment() {

    private var _binding: FragmentTodoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentTodoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // get to-do from json file
        var todoList = ToDoSource(requireContext()).loadTodoList() as MutableList<ToDoItem>

        // initialize calDate
        var calendarView = binding.calendarView
        val dateFormat: SimpleDateFormat = SimpleDateFormat("yyyy/MM/dd")
        var calDate:String = dateFormat.format(calendarView.getDate())

        // initialize recycler view
        val mAdapter = TodoAdapter(todoList)
        mAdapter.getFilter().filter(calDate)
        binding.recyclerTodo.adapter = mAdapter
        binding.recyclerTodo.layoutManager = LinearLayoutManager(context)

        // for getting data from subactivity
        val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                todoList = ToDoSource(requireContext()).loadTodoList() as MutableList<ToDoItem>
                val id = it.data?.getStringExtra("id") ?: ""
                val contents = it.data?.getStringExtra("contents") ?: ""
                val todoItem = ToDoItem(id, calDate, false, contents)
                if(id !in todoList.map{it.id}){    // 만약 중복되지 않는 todo이면
                    todoList.add(todoItem) // 결과목록에 추가
                }
                ToDoSource(requireContext()).saveTodoList(todoList)
            }
        }
        // Contact plus button clicked
        binding.todoButton.setOnClickListener {
            val intent = Intent(context, TodoActivity::class.java)
            intent.putExtra("date", calDate)
            getContent.launch(intent)
        }

        calendarView.setOnDateChangeListener{ calenderView, i, i2, i3 ->
            calDate = "$i/${i2+1}/$i3"
            var todoList = ToDoSource(requireContext()).loadTodoList() as MutableList<ToDoItem>
            binding.recyclerTodo.adapter = TodoAdapter(todoList)
            binding.recyclerTodo.layoutManager = LinearLayoutManager(context)
        }

    }

    override fun onResume() {
        super.onResume()
        var todoList = ToDoSource(requireContext()).loadTodoList() as MutableList<ToDoItem>
        binding.recyclerTodo.adapter = TodoAdapter(todoList)
        binding.recyclerTodo.layoutManager = LinearLayoutManager(context)
    }
}

