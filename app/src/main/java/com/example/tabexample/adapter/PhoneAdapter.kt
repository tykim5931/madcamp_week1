package com.example.tabexample

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.RecyclerView
import com.example.tabexample.model.Phone
import org.w3c.dom.Text


class PhoneAdapter(val list: List<Phone>) : RecyclerView.Adapter<PhoneAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.contact_item, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val phone = list[position]
        holder.setPhone(phone)
    }

    @SuppressLint("MissingPermission")
    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mPhone: Phone? = null
        val btnPhone : AppCompatImageButton = itemView.findViewById(R.id.btnPhone)
        val textName : TextView = itemView.findViewById(R.id.textName)
        val textPhone : TextView = itemView.findViewById(R.id.textPhone)

        init {
            this.btnPhone.setOnClickListener {
                mPhone?.phone.let { phoneNumber ->
                    val uri = Uri.parse("tel:${phoneNumber.toString()}")
                    val intent = Intent(Intent.ACTION_CALL, uri)
                    itemView.context.startActivity(intent)
                }
            }
        }
        fun setPhone(phone:Phone) {
            this.mPhone = phone
            this.textName.text = phone.name
            this.textPhone.text = phone.phone
        }
    }

}