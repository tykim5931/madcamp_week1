package com.example.tabexample.adapter

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tabexample.R
import com.example.tabexample.model.CheckBoxData
import com.example.tabexample.model.CheckBoxImg
import com.example.tabexample.model.GalleryImage

class GalleryAdapter(
    private val context: Context,
    private val dataset: List<GalleryImage>
): RecyclerView.Adapter<GalleryAdapter.ItemViewHolder>() {

    // for checkBox and Delete
    private var ck = View.GONE  // for setting checkbox visibility
    fun updateCB(n:Int){ck = n}
    var checkBoxList = arrayListOf<CheckBoxData>()

    inner class ItemViewHolder(private val view: View): RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.gallery_image)
        var checkBox: CheckBox = view.findViewById(R.id.checkBox)

        fun cbSetter(pos: Int, item:String){
            this.checkBox.visibility = ck
            if(pos >= checkBoxList.size)
                checkBoxList.add(pos, CheckBoxData(item,false))
            checkBox.isChecked = checkBoxList[pos].checked
            checkBox.setOnClickListener {
                if(checkBox.isChecked)
                    checkBoxList[pos].checked = true
                else
                    checkBoxList[pos].checked = false
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.gallery_item, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]
        holder.imageView.setImageBitmap(item.bitmap)
        holder.cbSetter(position, item.id)
    }

    override fun getItemCount() = dataset.size
}