package com.isaiahvonrundstedt.bucket.adapters.support

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.adapters.BaseAdapter
import com.isaiahvonrundstedt.bucket.objects.experience.Info

class InfoAdapter(private var itemList: List<Info>): BaseAdapter() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): InfoViewHolder {
        val rowView: View = LayoutInflater.from(viewGroup.context).inflate(R.layout.layout_item_info, viewGroup, false)
        return InfoViewHolder(rowView)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, itemPosition: Int) {
        (viewHolder as InfoViewHolder).bind(itemList[itemPosition])
    }

    override fun getItemCount(): Int = itemList.size

    inner class InfoViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val titleView: AppCompatTextView = itemView.findViewById(R.id.infoTitleView)
        private val contentView: AppCompatTextView = itemView.findViewById(R.id.infoContentView)

        fun bind(info: Info){
            titleView.text = itemView.context.getString(info.id)
            contentView.text = info.content
        }
    }
}