package com.isaiahvonrundstedt.bucket.adapters.core

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.adapters.BaseAdapter
import com.isaiahvonrundstedt.bucket.objects.core.File

class SentAdapter (context: Context?, fragmentManager: FragmentManager, requestManager: RequestManager):
        BaseAdapter(context, fragmentManager, requestManager) {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val rowView: View = LayoutInflater.from(viewGroup.context).inflate(R.layout.layout_item_files, viewGroup, false)
        return SentFileViewHolder(rowView)
    }

    override fun getItemCount(): Int = itemList.size

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, itemPosition: Int) {
        (viewHolder as SentFileViewHolder).onBindData(itemList[itemPosition])
    }

    private val itemList: ArrayList<File> = ArrayList()

    fun setObservableItems(items: List<File>){
        val callback = FileDiffCallback(itemList, items)
        val result = DiffUtil.calculateDiff(callback)
        itemList.clear()
        itemList.addAll(items)
        result.dispatchUpdatesTo(this)
    }

}