package com.example.tabexample

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        binding.recyclerTodo.adapter = TodoAdapter(todoList)
        binding.recyclerTodo.layoutManager = LinearLayoutManager(context)

        // Contact plus button clicked
        binding.todoButton.setOnClickListener {
            ToDoSource(requireContext()).saveTodoList(todoList) // 현재 todolist 기록
            val intent = Intent(context, TodoActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        var todoList = ToDoSource(requireContext()).loadTodoList() as MutableList<ToDoItem>
        binding.recyclerTodo.adapter = TodoAdapter(todoList)
        binding.recyclerTodo.layoutManager = LinearLayoutManager(context)
    }

//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
}