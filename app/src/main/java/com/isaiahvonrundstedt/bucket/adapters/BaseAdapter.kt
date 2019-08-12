package com.isaiahvonrundstedt.bucket.adapters

import android.graphics.PorterDuff
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.objects.core.Notification

abstract class BaseAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    protected abstract class CoreViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        protected val rootView: View = itemView.findViewById(R.id.rootView)
        protected val iconView: AppCompatImageView = itemView.findViewById(R.id.iconView)
        protected val titleView: AppCompatTextView = itemView.findViewById(R.id.titleView)
        protected val subtitleView: AppCompatTextView = itemView.findViewById(R.id.subtitleView)
    }

    protected inner class NotificationViewHolder(itemView: View): CoreViewHolder(itemView) {
        fun onBindData(notification: Notification) {
            titleView.text = notification.title
            subtitleView.text = notification.content

            val icon = ContextCompat.getDrawable(itemView.context, Notification.obtainIconRes(notification.type))
            icon?.setColorFilter(ContextCompat.getColor(itemView.context, Notification.obtainColorRes(notification.type)),
                PorterDuff.Mode.SRC_ATOP)
            iconView.setImageDrawable(icon)
        }
    }
}