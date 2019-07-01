package com.isaiahvonrundstedt.bucket.fragments.bottomsheet

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseBottomSheet
import com.isaiahvonrundstedt.bucket.interfaces.PickerItemSelected
import com.isaiahvonrundstedt.bucket.objects.PickerItem

class PickerBottomSheet: BaseBottomSheet(), PickerItemSelected {

    fun setItems(list: ArrayList<PickerItem>){
        items = list
    }

    internal class PickerAdapter(private var items: ArrayList<PickerItem>,
                                 listener: PickerItemSelected): RecyclerView.Adapter<PickerAdapter.ViewHolder>() {

        private var itemSelected: PickerItemSelected = listener

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            viewHolder.iconView.setImageResource(items[position].drawableID)
            viewHolder.titleView.text = viewHolder.itemView.context.getString(items[position].stringID)
            viewHolder.rowView.setOnClickListener { itemSelected.onItemSelected(position) }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val rowView: View = LayoutInflater.from(parent.context).inflate(R.layout.layout_item_picker, parent, false)
            return ViewHolder(rowView)
        }

        override fun getItemCount(): Int {
            return items.size
        }

        class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
            val rowView: View = itemView.findViewById(R.id.rootView)
            val iconView: AppCompatImageView = itemView.findViewById(R.id.iconView)
            val titleView: TextView = itemView.findViewById(R.id.titleView)
        }

    }

    private var items: ArrayList<PickerItem> = ArrayList()

    private lateinit var rootView: View
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PickerAdapter
    private lateinit var itemSelected: PickerItemSelected

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.layout_sheet_picker, container, false)

        adapter = PickerAdapter(items, this)

        recyclerView = rootView.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(rootView.context, items.size)
        recyclerView.adapter = adapter

        return rootView
    }

    override fun onItemSelected(index: Int) {
        itemSelected.onItemSelected(index)
        this.dismiss()
    }

    fun setOnItemSelectedListener(listener: PickerItemSelected){
        this.itemSelected = listener
    }

}