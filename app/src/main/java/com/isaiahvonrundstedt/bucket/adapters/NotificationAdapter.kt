package com.isaiahvonrundstedt.bucket.adapters

import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.objects.Notification
import com.isaiahvonrundstedt.bucket.utils.Preferences
import com.isaiahvonrundstedt.bucket.utils.managers.ItemManager

class NotificationAdapter: RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    private var itemList: ArrayList<Notification> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val rowView: View = LayoutInflater.from(parent.context).inflate(R.layout.layout_item_notification, parent, false)
        return ViewHolder(rowView)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun setItems(itemList: List<Notification>){
        this.itemList.clear()
        this.itemList.addAll(itemList)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(itemList[position])
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val iconView: AppCompatImageView = itemView.findViewById(R.id.iconView)
        private val titleView: TextView = itemView.findViewById(R.id.titleView)
        private val subtitleView: TextView = itemView.findViewById(R.id.subtitleView)

        fun bindData(notification: Notification){

            titleView.text = notification.title
            subtitleView.text = notification.content

            val colorID: Int? = when (Preferences(itemView.context).theme){
                Preferences.THEME_LIGHT -> R.color.colorPrimary
                Preferences.THEME_DARK -> R.color.colorGenericWhite
                else -> null
            }

            val drawable: Drawable? = ItemManager.getNotificationIcon(itemView.context, notification.type)
            drawable?.setColorFilter(ContextCompat.getColor(itemView.context, colorID!!), PorterDuff.Mode.SRC_ATOP)
            iconView.setImageDrawable(drawable)
        }
    }

}