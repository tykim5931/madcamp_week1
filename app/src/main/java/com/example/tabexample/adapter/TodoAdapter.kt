package com.example.tabexample.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.tabexample.R
import com.example.tabexample.data.ToDoSource
import com.example.tabexample.model.CheckBoxData
import com.example.tabexample.model.ToDoItem
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class TodoAdapter(val list: List<ToDoItem>) : RecyclerView.Adapter<TodoAdapter.Holder>(),
    Filterable {

    private var ck = View.GONE  // for setting checkbox visibility
    fun updateCB(n:Int){ck = n}
    var checkBoxList = arrayListOf<CheckBoxData>()

    var filteredList = ArrayList<ToDoItem>()
    val unfilteredList = ArrayList<ToDoItem>()
    var itemFilter = ItemFilter()
    private lateinit var context:Context
    init{
        unfilteredList.addAll(list)
        filteredList.addAll(list)
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // for Checkbox
        var checkBox: CheckBox = itemView.findViewById(R.id.checkBox)

        fun cbSetter(pos: Int, id:String){
            this.checkBox.visibility = ck
            if(pos >= checkBoxList.size)
                checkBoxList.add(pos, CheckBoxData(id,false))
            checkBox.isChecked = checkBoxList[pos].checked
            checkBox.setOnClickListener {
                if(checkBox.isChecked)
                    checkBoxList[pos].checked = true
                else
                    checkBoxList[pos].checked = false
            }
        }

        lateinit var mItem: ToDoItem
        var textView: TextView = itemView.findViewById(R.id.text)
        var progressBtn: ImageButton = itemView.findViewById(R.id.btn_progress)
        var btnLayer: ConstraintLayout = itemView.findViewById(R.id.btnLayer)
        val radioGroup: RadioGroup = itemView.findViewById(R.id.radioGroup)

        init {
            progressBtn.setOnClickListener {
                if(radioGroup.visibility == View.GONE) {
                    radioGroup.visibility = View.VISIBLE
                }else
                    radioGroup.visibility = View.GONE
            }
            radioGroup.setOnCheckedChangeListener{ _, isChecked ->
                var radioButton: RadioButton = itemView.findViewById(isChecked)
                val pos = unfilteredList.map{it.id}.indexOf(mItem.id)
                when(isChecked){
                    R.id.done -> {
                        unfilteredList[pos].done = 1
                        progressBtn.setImageResource(R.drawable.ic_baseline_check_24)
                        textView.setTextColor(ContextCompat.getColor(context, R.color.gray))
                    }
                    R.id.inProgress -> {
                        unfilteredList[pos].done = 0
                        progressBtn.setImageResource(R.drawable.ic_baseline_clear_24)
                        textView.setTextColor(ContextCompat.getColor(context, R.color.black))
                    }
                    R.id.postpone -> {
                        unfilteredList[pos].done = 2
                        progressBtn.setImageResource(R.drawable.ic_baseline_change_history_24)
                        textView.setTextColor(ContextCompat.getColor(context, R.color.black))
                    }
                }
                radioButton.isChecked = false
                radioGroup.visibility = View.GONE
                ToDoSource(context).saveTodoList(unfilteredList)
            }
        }

        fun setItem(todo:ToDoItem, pos: Int) { // binding method
            this.mItem = todo
            this.textView.text = todo.contents
            if(todo.done == 1){
                progressBtn.setImageResource(R.drawable.ic_baseline_check_24)
                textView.setTextColor(ContextCompat.getColor(context, R.color.gray))
            }
            else if(todo.done == 0){
                progressBtn.setImageResource(R.drawable.ic_baseline_clear_24)
                textView.setTextColor(ContextCompat.getColor(context, R.color.black))
            }
            else{
                progressBtn.setImageResource(R.drawable.ic_baseline_change_history_24)
                textView.setTextColor(ContextCompat.getColor(context, R.color.black))
            }
        }
    }

    inner class ItemFilter: Filter() {
        override fun performFiltering(charSequence: CharSequence?): FilterResults {

            val dateFormat = SimpleDateFormat("yyyy/MM/dd")
            val filterString = charSequence.toString()
            val results = FilterResults()

            val resultList: ArrayList<ToDoItem> = ArrayList<ToDoItem>()
            for (item in unfilteredList){
                if(dateFormat.parse(item.date) == dateFormat.parse(filterString))
                    resultList.add(item)
            }
            results.values = resultList
            results.count = resultList.size
            println(resultList.size)
            return results
        }

        override fun publishResults(charSequence: CharSequence?, filterResults: FilterResults?) {
            filteredList.clear()
            filteredList.addAll(filterResults?.values as ArrayList<ToDoItem>)
            notifyDataSetChanged()
            println(filteredList.size)
        }
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = filteredList[position]
        holder.setItem(filteredList[position], position)
        holder.cbSetter(position, item.id)
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    override fun getFilter(): Filter {
        return itemFilter
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        context = parent.context
        val view = LayoutInflater.from(parent.context).inflate(R.layout.todo_item, parent, false)
        return Holder(view)
    }
}