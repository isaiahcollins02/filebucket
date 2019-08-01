package com.isaiahvonrundstedt.bucket.adapters.experience

import android.content.Context
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.adapters.BaseAdapter
import com.isaiahvonrundstedt.bucket.objects.core.StorageItem
import com.isaiahvonrundstedt.bucket.utils.Data

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
        override fun onBindData(item: StorageItem?) {
            rootView.setOnClickListener { onDownload(item) }
            rootView.setOnLongClickListener { showDetailDialog(item); true }
            titleView.text = item?.name
            subtitleView.text = item?.author
            sizeView.text = Data.formatSize(itemView.context, item?.size)

            val icon = ResourcesCompat.getDrawable(rootView.context.resources, StorageItem.obtainIconID(item?.type), null)
            icon?.setColorFilter(ContextCompat.getColor(rootView.context, StorageItem.obtainColorID(item?.type)), PorterDuff.Mode.SRC_ATOP)
            iconView.setImageDrawable(icon)
        }

    }

}