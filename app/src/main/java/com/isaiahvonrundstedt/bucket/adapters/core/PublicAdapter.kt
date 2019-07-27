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
import com.isaiahvonrundstedt.bucket.objects.core.StorageItem
import com.isaiahvonrundstedt.bucket.utils.Preferences

class PublicAdapter (private var context: Context?, fragmentManager: FragmentManager, requestManager: RequestManager):
    BaseAdapter(context, fragmentManager, requestManager) {

    private var itemList: ArrayList<StorageItem> = ArrayList()

    fun setObservableItems(items: List<StorageItem>){
        val callback = ItemDiffCallback(itemList, items)
        val result = DiffUtil.calculateDiff(callback)
        itemList.clear()
        itemList.addAll(items)
        result.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutID: Int = if (viewTypeImage == viewType) R.layout.layout_item_photo else R.layout.layout_item_files
        val rowView: View = LayoutInflater.from(viewGroup.context).inflate(layoutID, viewGroup, false)
        return if (viewTypeImage == viewType) ImageViewHolder(rowView) else SharedFileViewHolder(rowView)
    }

    override fun getItemCount(): Int = itemList.size

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, itemPosition: Int) {
        when (viewHolder.itemViewType){
            viewTypeFilePublic -> (viewHolder as SharedFileViewHolder).onBindData(itemList[itemPosition])
            viewTypeImage -> (viewHolder as ImageViewHolder).onBindData(itemList[itemPosition])
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (itemList[position].type){
            StorageItem.typeImage -> {
                if (Preferences(context).previewPreference != false)
                    viewTypeImage
                else viewTypeFilePublic
            } else -> viewTypeFilePublic
        }
    }
}