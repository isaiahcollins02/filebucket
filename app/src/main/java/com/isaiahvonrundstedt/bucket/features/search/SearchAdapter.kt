package com.isaiahvonrundstedt.bucket.features.search

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

class SearchAdapter(context: Context, fragmentManager: FragmentManager, requestManager: RequestManager):
    BaseAdapter(context, fragmentManager, requestManager){

    private var itemList: ArrayList<StorageItem> = ArrayList()

    fun setObservableItems(items: List<StorageItem>){
        val callback = ItemDiffCallback(itemList, items)
        val result = DiffUtil.calculateDiff(callback)

        itemList.clear()
        itemList.addAll(items)

        result.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val rowView: View = LayoutInflater.from(viewGroup.context).inflate(R.layout.layout_item_files, viewGroup, false)
        return SearchViewHolder(rowView)
    }

    override fun getItemCount(): Int = itemList.size

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, itemPosition: Int) {
        (viewHolder as SearchViewHolder).onBindData(itemList[itemPosition])
    }

    private inner class SearchViewHolder(itemView: View): FileViewHolder(itemView){
        override fun onBindData(item: StorageItem) {
            rootView.setOnClickListener { onDownload(item) }
            rootView.setOnLongClickListener { showDetailDialog(item); true }
            titleView.text = item.name
            item.fetchAuthorName { subtitleView.text = it ?: getString(R.string.unknown_file_author) }
            sizeView.text = item.formatSize(itemView.context)

            val icon = obtainTintedDrawable(StorageItem.obtainIconID(item.type), StorageItem.obtainColorID(item.type))
            iconView.setImageDrawable(icon)
        }

    }

}