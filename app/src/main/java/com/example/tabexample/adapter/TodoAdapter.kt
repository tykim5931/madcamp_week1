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
import com.example.tabexample.model.ToDoItem


class TodoAdapter(val list: List<ToDoItem>) : RecyclerView.Adapter<TodoAdapter.Holder>(),
    Filterable {

    var filteredList = ArrayList<ToDoItem>()
    var itemFilter = ItemFilter()
    private lateinit var context:Context
    init{ filteredList.addAll(list) }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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
                when(isChecked){
                    R.id.done -> {
                        mItem.done = true
                        progressBtn.setImageResource(R.drawable.ic_baseline_check_24)
                        textView.setTextColor(ContextCompat.getColor(context, R.color.gray))
                    }
                    R.id.inProgress -> {
                        mItem.done = false
                        progressBtn.setImageResource(R.drawable.ic_baseline_radio_button_unchecked_24)
                        textView.setTextColor(ContextCompat.getColor(context, R.color.black))
                    }
                    R.id.postpone -> {
                        mItem.date = mItem.date + 1
                        println(mItem.date)
                    }
                }
                radioGroup.visibility = View.GONE
            }
        }

        fun setItem(todo:ToDoItem, pos: Int) { // binding method
            this.mItem = todo
            this.textView.text = todo.contents
            if(todo.done == true){
                progressBtn.setImageResource(R.drawable.ic_baseline_check_24)
                textView.setTextColor(ContextCompat.getColor(context, R.color.gray))
            }
            else{
                progressBtn.setImageResource(R.drawable.ic_baseline_radio_button_unchecked_24)
                textView.setTextColor(ContextCompat.getColor(context, R.color.black))
            }
        }
    }

    inner class ItemFilter: Filter() {
        override fun performFiltering(charSequence: CharSequence?): FilterResults {

            val filterString = charSequence.toString()  // here we get date of calender
            val results = FilterResults()
            println("charSequence: $charSequence")

            val filteredList: ArrayList<ToDoItem> = ArrayList<ToDoItem>()
            for (item in list){
                if(item.date == filterString) filteredList.add(item)
            }

            results.values = filteredList
            results.count = filteredList.size
            return results
        }

        override fun publishResults(charSequence: CharSequence?, filterResults: FilterResults?) {
            filteredList.clear()
            filteredList.addAll(filterResults?.values as ArrayList<ToDoItem>)
            notifyDataSetChanged()
        }
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.setItem(filteredList[position], position)
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