package com.isaiahvonrundstedt.bucket.adapters.support

import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.objects.core.Notification
import com.isaiahvonrundstedt.bucket.utils.managers.ItemManager

class NotificationAdapter: RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    private var itemList: ArrayList<Notification> = ArrayList()

    fun setObservableItems(items: List<Notification>){
        val callback = ItemCallback(itemList, items)
        val result = DiffUtil.calculateDiff(callback)

        itemList.clear()
        itemList.addAll(items)
        result.dispatchUpdatesTo(this)
    }

    inner class ItemCallback(private var oldItems: List<Notification>, private var newItems: List<Notification>): DiffUtil.Callback(){
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItems[oldItemPosition].id == newItems[newItemPosition].id
        }

        override fun getOldListSize(): Int {
            return oldItems.size
        }

        override fun getNewListSize(): Int {
            return newItems.size
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItems[oldItemPosition] == newItems[newItemPosition]
        }

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val rowView: View = LayoutInflater.from(viewGroup.context).inflate(R.layout.layout_item_notification, viewGroup, false)
        return ViewHolder(rowView)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(itemList[position])
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val iconView: AppCompatImageView = itemView.findViewById(R.id.iconView)
        private val titleView: AppCompatTextView = itemView.findViewById(R.id.titleView)
        private val subtitleView: AppCompatTextView = itemView.findViewById(R.id.subtitleView)

        fun bindData(notification: Notification){

            titleView.text = notification.title
            subtitleView.text = notification.content

            val drawable: Drawable? = ItemManager.getNotificationIcon(itemView.context, notification.type)
            drawable?.setColorFilter(ContextCompat.getColor(itemView.context, R.color.colorIcons), PorterDuff.Mode.SRC_ATOP)
            iconView.setImageDrawable(drawable)
        }
    }

}