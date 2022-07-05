package com.example.tabexample

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.CalendarView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tabexample.Fragment01.Companion.SHRUNKEN_MENU
import com.example.tabexample.adapter.GalleryAdapter
import com.example.tabexample.adapter.TodoAdapter
import com.example.tabexample.data.GalleryDatasource
import com.example.tabexample.data.PhoneBookSource
import com.example.tabexample.data.ToDoSource
import com.example.tabexample.databinding.FragmentContactBinding
import com.example.tabexample.databinding.FragmentTodoBinding
import com.example.tabexample.decorator.DotDecorator
import com.example.tabexample.decorator.TodayDecorator
import com.example.tabexample.model.CheckBoxData
import com.example.tabexample.model.Phone
import com.example.tabexample.model.ToDoItem
import com.prolificinteractive.materialcalendarview.CalendarMode
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class Fragment03 : Fragment() {

    companion object {
        const val SHRUNKEN_MENU = 1
        const val EXPANDED_MENU = 2
        const val DELETE_MENU = 3

    }

    private var _binding: FragmentTodoBinding? = null
    private val binding get() = _binding!!
    private val dateFormat: SimpleDateFormat = SimpleDateFormat("yyyy/MM/dd")

    lateinit var mAdapter:TodoAdapter
    lateinit var todoList: MutableList<ToDoItem>

    //Calendar-related variables
    lateinit var calDate: String
    lateinit var selectedDate: CalendarDay
    lateinit var calendarView: MaterialCalendarView
    lateinit var salmonDotDecorator: DotDecorator
    lateinit var grayDotDecorator: DotDecorator
    // Animation variables & switch
    private val rotateOpen: Animation by lazy{ AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_open_anim)}
    private val rotateClose: Animation by lazy{ AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_close_anim)}
    private val fromBottom: Animation by lazy{ AnimationUtils.loadAnimation(requireContext(), R.anim.from_bottom_anim)}
    private val toBottom: Animation by lazy{ AnimationUtils.loadAnimation(requireContext(), R.anim.to_bottom_anim)}

    private var menuStatus: Int = Fragment03.SHRUNKEN_MENU


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentTodoBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // get to-do from json file
        todoList = ToDoSource(requireContext()).loadTodoList() as ArrayList<ToDoItem>
        mAdapter = TodoAdapter(todoList)
        // initialize calDate
        calendarView = binding.calendarView
        selectedDate = CalendarDay.today()
        calendarView.selectedDate = CalendarDay.today()
        val dateFormat = SimpleDateFormat("yyyy/MM/dd")
//        calDate = dateFormat.format(selectedDate.date)
        calDate = selectedDate.date.toString()
        salmonDotDecorator = generateSalmonDotDecorator()
        grayDotDecorator = generateGrayDotDecorator()
        calendarView.addDecorator(salmonDotDecorator)
        calendarView.addDecorator(grayDotDecorator)
        calendarView.addDecorator(TodayDecorator()) // Set today color
//        calendarView.setDateTextAppearance(R.style.CustomDateTextAppearance)
//        calendarView.setWeekDayTextAppearance(R.style.CustomWeekDayAppearance)
//        calendarView.setHeaderTextAppearance(R.style.CustomHeaderTextAppearance)
        // initialize recycler view
        mAdapter.getFilter().filter(calDate)
        binding.recyclerTodo.adapter = mAdapter
        binding.recyclerTodo.layoutManager = LinearLayoutManager(context)

        //Initialize visibility
        menuStatus = Fragment01.SHRUNKEN_MENU
        setVisibility(menuStatus)
        setAnimation(menuStatus)
        setClickable(menuStatus)

        // for getting data from subactivity
        val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val id = it.data?.getStringExtra("id") ?: ""
                val contents = it.data?.getStringExtra("contents") ?: ""
                val todoItem = ToDoItem(id, calDate, 0, contents)
                if(id !in todoList.map{it.id}){    // 만약 중복되지 않는 todo이면
                    todoList.add(todoItem) // 결과목록에 추가
                }
                calendarView.removeDecorator(salmonDotDecorator)
                calendarView.removeDecorator(grayDotDecorator)
                salmonDotDecorator = generateSalmonDotDecorator()
                grayDotDecorator = generateGrayDotDecorator()
                calendarView.addDecorator(salmonDotDecorator)
                calendarView.addDecorator(grayDotDecorator)
                ToDoSource(requireContext()).saveTodoList(todoList)
                mAdapter = TodoAdapter(todoList)
                mAdapter.getFilter().filter(calDate)
                binding.recyclerTodo.adapter = mAdapter
                binding.recyclerTodo.layoutManager = LinearLayoutManager(context)
            }
        }
        calendarView.setOnDateChangedListener { widget, date, selected -> //                calendarView.removeDecorator(todayDecorator)
            selectedDate = calendarView.selectedDate!!
            calendarView.removeDecorator(salmonDotDecorator)
            calendarView.removeDecorator(grayDotDecorator)
            salmonDotDecorator = generateSalmonDotDecorator()
            grayDotDecorator = generateGrayDotDecorator()
            calendarView.addDecorator(salmonDotDecorator)
            calendarView.addDecorator(grayDotDecorator)
            calDate = date.date.toString()
            mAdapter.getFilter().filter(calDate)
            binding.recyclerTodo.adapter = mAdapter
        }

        //On-click listeners
        binding.moreButton.setOnClickListener{
            menuStatus = Fragment01.EXPANDED_MENU
            setVisibility(menuStatus)
            setAnimation(menuStatus)
            setClickable(menuStatus)
        }
        binding.closeButton.setOnClickListener{
            menuStatus = Fragment01.SHRUNKEN_MENU
            setVisibility(menuStatus)
            setAnimation(menuStatus)
            setClickable(menuStatus)
        }

        binding.addButton.setOnClickListener {
            val intent = Intent(context, TodoActivity::class.java)
            intent.putExtra("date", calDate)
            getContent.launch(intent)
        }

        binding.selectButton.setOnClickListener{
            mAdapter = TodoAdapter(todoList) // update mAdapter
            mAdapter.updateCB(View.VISIBLE)    // 체크박스 모두노출
            mAdapter.getFilter().filter(calDate)
            binding.recyclerTodo.adapter = mAdapter

            menuStatus = Fragment01.DELETE_MENU
            setVisibility(menuStatus)
            setAnimation(menuStatus)
            setClickable(menuStatus)
        }

        binding.cancelButton.setOnClickListener{
            mAdapter.checkBoxList.map{it.checked = false}//체크박스 모두해제
            mAdapter.updateCB(View.GONE)    // 체크박스 안보이게
            mAdapter.getFilter().filter(calDate)
            binding.recyclerTodo.adapter = mAdapter
            menuStatus = Fragment01.EXPANDED_MENU
            setVisibility(menuStatus)
            setAnimation(menuStatus)
            setClickable(menuStatus)
        }

        binding.deleteButton.setOnClickListener{
            var checklist : List<CheckBoxData> = mAdapter.checkBoxList.filter{it.checked} // filter checked img
            todoList = ToDoSource(requireContext()).loadTodoList() as MutableList<ToDoItem>
            if(!checklist.isEmpty() && !todoList.isEmpty()){
                todoList = todoList.filter{
                    !checklist.map{item -> item.id}.contains(it.id)
                }.toMutableList()
                ToDoSource(requireContext()).saveTodoList(todoList)
                calendarView.removeDecorator(salmonDotDecorator)
                calendarView.removeDecorator(grayDotDecorator)
                salmonDotDecorator = generateSalmonDotDecorator()
                grayDotDecorator = generateGrayDotDecorator()
                calendarView.addDecorator(salmonDotDecorator)
                calendarView.addDecorator(grayDotDecorator)
                mAdapter = TodoAdapter(todoList) // update mAdapter
                mAdapter.getFilter().filter(calDate)
                menuStatus = Fragment01.SHRUNKEN_MENU
                setVisibility(menuStatus)
                listOf(binding.moreButton)
                    .forEach{it.startAnimation(rotateClose)}
                setClickable(menuStatus)
                binding.recyclerTodo.adapter = mAdapter
            }
        }

        binding.expandButton.setOnClickListener{
            if(calendarView.calendarMode == CalendarMode.MONTHS) {
                calendarView.state().edit().setCalendarDisplayMode(CalendarMode.WEEKS).commit()
                binding.expandButton.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
            }
            else{
                calendarView.state().edit().setCalendarDisplayMode(CalendarMode.MONTHS).commit()
                binding.expandButton.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24)
            }
        }
    }

    // Animation Functions
    private fun setVisibility(menuStatus: Int) {
        val allSet = setOf(binding.moreButton, binding.selectButton, binding.addButton, binding.closeButton, binding.deleteButton, binding.cancelButton)
        when(menuStatus) {
            Fragment01.SHRUNKEN_MENU -> {
                val visibleSet = setOf(binding.moreButton)
                visibleSet.forEach{it.visibility = View.VISIBLE}
                allSet.minus(visibleSet).forEach{it.visibility = View.INVISIBLE}
            }
            Fragment01.EXPANDED_MENU -> {
                val visibleSet = setOf(binding.selectButton, binding.addButton, binding.closeButton)
                visibleSet.forEach{it.visibility = View.VISIBLE}
                allSet.minus(visibleSet).forEach{it.visibility = View.INVISIBLE}
            }
            //DELETE_MENU
            else -> {
                val visibleSet = setOf(binding.deleteButton, binding.cancelButton)
                visibleSet.forEach{it.visibility = View.VISIBLE}
                allSet.minus(visibleSet).forEach{it.visibility = View.INVISIBLE}
            }
        }
    }
    private fun setAnimation(menuStatus: Int) {
        when(menuStatus) {
            Fragment01.SHRUNKEN_MENU -> {
                listOf(binding.moreButton)
                    .forEach{it.startAnimation(rotateClose)}
                listOf(binding.selectButton, binding.addButton)
                    .forEach{it.startAnimation(toBottom)}
            }
            Fragment01.EXPANDED_MENU -> {
                listOf(binding.moreButton)
                    .forEach{it.startAnimation(rotateOpen)}
                listOf(binding.selectButton, binding.addButton)
                    .forEach{it.startAnimation(fromBottom)}
            }
            //DELETE_MENU
            else -> {
                listOf(binding.selectButton, binding.addButton)
                    .forEach{it.startAnimation(toBottom)}
            }
        }
    }

    private fun setClickable(menuStatus: Int) {
        val allSet = setOf(binding.moreButton, binding.selectButton, binding.addButton, binding.closeButton, binding.deleteButton, binding.cancelButton)
        when(menuStatus) {
            Fragment01.SHRUNKEN_MENU -> {
                val clickableSet = setOf(binding.moreButton)
                clickableSet.forEach{it.isClickable = true}
                allSet.minus(clickableSet).forEach{it.isClickable = false}
            }
            Fragment01.EXPANDED_MENU -> {
                val clickableSet = setOf(binding.selectButton, binding.addButton, binding.closeButton)
                clickableSet.forEach{it.isClickable = true}
                allSet.minus(clickableSet).forEach{it.isClickable = false}
            }
            //DELETE_MENU
            else -> {
                val clickableSet = setOf(binding.deleteButton, binding.cancelButton)
                clickableSet.forEach{it.isClickable = true}
                allSet.minus(clickableSet).forEach{it.isClickable = false}
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun generateSalmonDotDecorator(): DotDecorator {
        val salmonDotDates =
            todoList
                .filter{it.done==0}
                .map{it.date}
                .map{LocalDate.parse(it, DateTimeFormatter.ISO_DATE)}
                .map{CalendarDay.from(it.year, it.monthValue, it.dayOfMonth)}
                .toSet()

        return DotDecorator(Color.parseColor("#DD5151"), salmonDotDates)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun generateGrayDotDecorator(): DotDecorator {
        val salmonDotDates =
            todoList
                .filter{it.done==0}
                .map{it.date}
                .map{LocalDate.parse(it, DateTimeFormatter.ISO_DATE)}
                .map{CalendarDay.from(it.year, it.monthValue, it.dayOfMonth)}
                .toSet()

        val grayDotDates =
            todoList
                .filter{it.done==2}
                .map{it.date}
                .map{LocalDate.parse(it, DateTimeFormatter.ISO_DATE)}
                .map{CalendarDay.from(it.year, it.monthValue, it.dayOfMonth)}
                .toSet()
        return DotDecorator(Color.parseColor("#808080"), grayDotDates.minus(salmonDotDates))
    }
}

