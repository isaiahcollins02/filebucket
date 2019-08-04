package com.isaiahvonrundstedt.bucket.adapters.support

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.adapters.BaseAdapter
import com.isaiahvonrundstedt.bucket.objects.core.Notification

class NotificationAdapter: BaseAdapter() {

    private var itemList: ArrayList<Notification> = ArrayList()

    fun setObservableItems(items: List<Notification>){
        val callback = NotificationDiffCallback(itemList, items)
        val result = DiffUtil.calculateDiff(callback)

        itemList.clear()
        itemList.addAll(items)
        result.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val rowView: View = LayoutInflater.from(viewGroup.context).inflate(R.layout.layout_item_notification, viewGroup, false)
        return NotificationViewHolder(rowView)
    }

    override fun getItemCount(): Int = itemList.size

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, itemPosition: Int) {
        (viewHolder as NotificationViewHolder).onBindData(itemList[itemPosition])
    }

    private class NotificationDiffCallback(private var oldItems: List<Notification>,
                                           private var newItems: List<Notification>): DiffUtil.Callback(){

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItems[oldItemPosition].id == newItems[newItemPosition].id
        }

        override fun getOldListSize(): Int = oldItems.size

        override fun getNewListSize(): Int = newItems.size

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItems[oldItemPosition] == newItems[newItemPosition]
        }

    }

}