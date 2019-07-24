package com.isaiahvonrundstedt.bucket.adapters

import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.isaiahvonrundstedt.bucket.R

abstract class GenericAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    internal abstract class GenericViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        internal val rootView: View = itemView.findViewById(R.id.rootView)
        internal val iconView: View = itemView.findViewById(R.id.iconView)
        internal val titleView: AppCompatTextView = itemView.findViewById(R.id.titleView)
        internal val subtitleView: AppCompatTextView = itemView.findViewById(R.id.subtitleView)
    }
}