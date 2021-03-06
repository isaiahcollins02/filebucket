package com.isaiahvonrundstedt.bucket.features.storage

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.features.shared.abstracts.BaseAdapter
import com.isaiahvonrundstedt.bucket.features.shared.StorageItem

class LocalAdapter(context: Context, fragmentManager: FragmentManager, requestManager: RequestManager,
                   private var directoryListener: DirectoryListener): BaseAdapter(context, fragmentManager, requestManager){

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val rowView: View = LayoutInflater.from(viewGroup.context).inflate(R.layout.layout_item_files, viewGroup, false)
        return LocalViewHolder(rowView, directoryListener)
    }

    override fun getItemCount(): Int = itemList.size

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, itemPosition: Int) {
        (viewHolder as LocalViewHolder).onBindData(itemList[itemPosition])
    }

    private var itemList: ArrayList<StorageItem> = ArrayList()

    fun setObservableItems(items: List<StorageItem>){
        val callback = ItemDiffCallback(itemList, items)
        val result = DiffUtil.calculateDiff(callback)

        itemList.clear()
        itemList.addAll(items)

        result.dispatchUpdatesTo(this)
    }

}