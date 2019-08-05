package com.isaiahvonrundstedt.bucket.adapters

import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
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

    protected abstract class SimpleViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        protected val rootView: View = itemView.findViewById(R.id.rootView)
        protected val iconView: AppCompatImageView = itemView.findViewById(R.id.iconView)
        protected val titleView: AppCompatTextView = itemView.findViewById(R.id.titleView)
    }

    protected inner class NotificationViewHolder(itemView: View): CoreViewHolder(itemView) {
        fun onBindData(notification: Notification) {
            iconView.setImageResource(Notification.obtainIconRes(notification.type))
            titleView.text = notification.title
            subtitleView.text = notification.content
        }
    }
}