package com.isaiahvonrundstedt.bucket.adapters.experience

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.adapters.BaseAdapter
import com.isaiahvonrundstedt.bucket.objects.core.StorageItem
import java.util.*
import kotlin.collections.ArrayList

class SearchAdapter(context: Context, fragmentManager: FragmentManager, requestManager: RequestManager):
    BaseAdapter(context, fragmentManager, requestManager), Filterable {

    private var itemList: ArrayList<StorageItem> = ArrayList()
    private var filterList: ArrayList<StorageItem> = itemList
    private var filter: SharedFilter? = null

    fun setObservableItems(items: List<StorageItem>){
        val callback = ItemDiffCallback(itemList, items)
        val result = DiffUtil.calculateDiff(callback)

        itemList.clear()
        itemList.addAll(items)

        result.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val rowView: View = LayoutInflater.from(viewGroup.context).inflate(R.layout.layout_item_files, viewGroup, false)
        return SharedFileViewHolder(rowView)
    }

    override fun getItemCount(): Int = filterList.size

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, itemPosition: Int) {
        (viewHolder as SharedFileViewHolder).onBindData(filterList[itemPosition])
    }

    override fun getFilter(): Filter {
        if (filter == null){
            filter = SharedFilter()
        }
        return filter as SharedFilter
    }

    private inner class SharedFilter: Filter(){
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val searchTerm: String? = constraint.toString()
            val filteredList: ArrayList<StorageItem> = ArrayList()
            if (searchTerm != null){
                filterList.addAll(itemList)
            } else {
                itemList.forEachIndexed { _, storageItem ->
                    if (storageItem.name?.toLowerCase(Locale.getDefault())?.contains(constraint!!) == true)
                        filteredList.add(storageItem)
                }
            }
            val results = FilterResults()
            results.values = filteredList

            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            val newList: ArrayList<StorageItem> = results?.values as ArrayList<StorageItem>
            val callback = ItemDiffCallback(filterList, newList)
            val result = DiffUtil.calculateDiff(callback)

            filterList.clear()
            filterList.addAll(newList)
            result.dispatchUpdatesTo(this@SearchAdapter)
        }
    }


}