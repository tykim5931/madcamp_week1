package com.example.tabexample

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.tabexample.model.CheckBoxData
import com.example.tabexample.model.Phone


class PhoneAdapter(val list: List<Phone>) : RecyclerView.Adapter<PhoneAdapter.Holder>(), Filterable {

    // ### 클릭 인터페이스
    interface MyItemClickListener{
        fun onItemClick(position: Int)
        fun onLongClick(position: Int)
    }

    // ####
    var filteredPhone = ArrayList<Phone>()
    var itemFilter = ItemFilter()
    private var ck = 0  // for setting checkbox visibility

    init{
        filteredPhone.addAll(list)
    }
    fun updateCB(n:Int){
        ck = n
    }
    var checkBoxList = arrayListOf<CheckBoxData>()

    @SuppressLint("MissingPermission")
    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mPhone: Phone? = null
        val btnPhone : AppCompatImageButton = itemView.findViewById(R.id.btnPhone)
        val textName : TextView = itemView.findViewById(R.id.textName)
        val textPhone : TextView = itemView.findViewById(R.id.textPhone)
        val invisibleItem : ConstraintLayout = itemView.findViewById(R.id.invisible_item)
        val visibleItem : ConstraintLayout = itemView.findViewById(R.id.visible_item)
        var checkBox: CheckBox = itemView.findViewById(R.id.checkBox)

        init {
            this.btnPhone.setOnClickListener {
                mPhone?.phone.let { phoneNumber ->
                    val uri = Uri.parse("tel:${phoneNumber.toString()}")
                    val intent = Intent(Intent.ACTION_CALL, uri)
                    itemView.context.startActivity(intent)
                }
            }
            this.visibleItem.setOnClickListener {
//                mItemClickListener.onItemClick(adapterPosition)
                if(invisibleItem.visibility == View.GONE)
                    invisibleItem.visibility = View.VISIBLE
                else
                    invisibleItem.visibility = View.GONE
            }
//            itemView.setOnLongClickListener{
//                mItemClickListener.onLongClick(adapterPosition)
//                checkBox.visibility = View.VISIBLE
//                return@setOnLongClickListener true
//            }
        }

        fun setPhone(phone:Phone, pos: Int) { // binding method
            this.mPhone = phone
            this.textName.text = phone.name
            this.textPhone.text = phone.phone
            if(ck == 1)
                this.checkBox.visibility = View.VISIBLE
            else
                this.checkBox.visibility = View.GONE
            if(pos >= checkBoxList.size)
                checkBoxList.add(pos, CheckBoxData(phone.id, false))

            checkBox.isChecked = checkBoxList[pos].checked
            checkBox.setOnClickListener {
                if(checkBox.isChecked)
                    checkBoxList[pos].checked = true
                else
                    checkBoxList[pos].checked = false
            }
        }
    }

    inner class ItemFilter: Filter() {
        override fun performFiltering(charSequence: CharSequence?): FilterResults {
            val filterString = charSequence.toString()
            val results = FilterResults()
            println("charSequence: $charSequence")

            val filteredList: ArrayList<Phone> = ArrayList<Phone>()
            if (filterString.trim{ it <= ' ' }.isEmpty()){  // 검색 없으면 전체리스트
                results.values = list
                results.count = list.size
            }
            else if (filterString.trim{ it <= ' ' }.length <= 2){  // 공백제외 3글자 이하 -> 이름으로 검색.
                for (phoneItem in list) {
                    if (phoneItem.name?.contains(filterString,true) == true) filteredList.add(phoneItem)
                }
                results.values = filteredList
                results.count = filteredList.size
                //그 외의 경우(공백제외 2글자 초과) -> 이름/전화번호로 검색
            } else {
                for (phoneItem in list) {
                    if (phoneItem.name?.contains(filterString,true) == true
                        || phoneItem.phone?.contains(filterString,true) == true
                    ) filteredList.add(phoneItem)
                }
                results.values = filteredList
                results.count = filteredList.size
            }
            return results
        }

        override fun publishResults(charSequence: CharSequence?, filterResults: FilterResults?) {
            filteredPhone.clear()
            println(filterResults?.values)
            filteredPhone.addAll(filterResults?.values as ArrayList<Phone>)
            notifyDataSetChanged()
        }
    }

    // ### Necessary Implementation for Using Adapter
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.contact_item, parent, false)
        return Holder(view)
    }
    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.setPhone(filteredPhone[position], position)
    }
    override fun getItemCount(): Int {
        return filteredPhone.size
    }
    override fun getFilter(): Filter {
        return itemFilter
    }

}