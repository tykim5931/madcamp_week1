package com.example.tabexample

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.tabexample.model.Phone
import org.w3c.dom.Text


class PhoneAdapter(val list: List<Phone>) : RecyclerView.Adapter<PhoneAdapter.Holder>(), Filterable {

    var filteredPhone = ArrayList<Phone>()
    var itemFilter = ItemFilter()

    init{
        filteredPhone.addAll(list)
    }

    @SuppressLint("MissingPermission")
    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mPhone: Phone? = null
        val btnPhone : AppCompatImageButton = itemView.findViewById(R.id.btnPhone)
        val textName : TextView = itemView.findViewById(R.id.textName)
        val textPhone : TextView = itemView.findViewById(R.id.textPhone)
        val invisibleLayer : ConstraintLayout = itemView.findViewById(R.id.invisible_item)
        val visibleLayer : ConstraintLayout = itemView.findViewById(R.id.visible_item)

        init {
            this.btnPhone.setOnClickListener {
                mPhone?.phone.let { phoneNumber ->
                    val uri = Uri.parse("tel:${phoneNumber.toString()}")
                    val intent = Intent(Intent.ACTION_CALL, uri)
                    itemView.context.startActivity(intent)
                }
            }
            this.visibleLayer.setOnClickListener {
                if (invisibleLayer.visibility == View.GONE)
                    invisibleLayer.visibility = View.VISIBLE
                else
                    invisibleLayer.visibility = View.GONE
            }
        }
        fun setPhone(phone:Phone) {
            this.mPhone = phone
            this.textName.text = phone.name
            this.textPhone.text = phone.phone
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
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.contact_item, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val phone = filteredPhone[position]
        holder.setPhone(phone)
    }

    override fun getItemCount(): Int {
        return filteredPhone.size
    }

    override fun getFilter(): Filter {
        return itemFilter
    }
}