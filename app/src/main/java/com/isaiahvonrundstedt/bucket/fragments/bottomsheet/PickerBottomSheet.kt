package com.isaiahvonrundstedt.bucket.fragments.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseBottomSheet
import com.isaiahvonrundstedt.bucket.interfaces.BottomSheetPicker
import com.isaiahvonrundstedt.bucket.components.custom.PickerItem

class PickerBottomSheet: BaseBottomSheet(), BottomSheetPicker {

    fun setItems(list: ArrayList<PickerItem>){
        items = list
    }

    internal class PickerAdapter(private var items: ArrayList<PickerItem>,
                                 listener: BottomSheetPicker): RecyclerView.Adapter<PickerAdapter.ViewHolder>() {

        private var bottomSheet: BottomSheetPicker = listener

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            viewHolder.iconView.setImageResource(items[position].drawableID)
            viewHolder.titleView.text = viewHolder.itemView.context.getString(items[position].stringID)
            viewHolder.rowView.setOnClickListener { bottomSheet.onItemSelected(position) }
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
    private lateinit var bottomSheet: BottomSheetPicker

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.layout_sheet_picker, container, false)

        adapter = PickerAdapter(items, this)

        recyclerView = rootView.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(rootView.context, items.size)
        recyclerView.adapter = adapter

        return rootView
    }

    override fun onItemSelected(index: Int) {
        bottomSheet.onItemSelected(index)
        this.dismiss()
    }

    fun setOnItemSelectedListener(listener: BottomSheetPicker){
        this.bottomSheet = listener
    }

}