package com.isaiahvonrundstedt.bucket.adapters.experience

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.objects.experience.Information

class InfoAdapter(private var items: List<Information>): RecyclerView.Adapter<InfoAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val rowView: View = LayoutInflater.from(viewGroup.context).inflate(R.layout.layout_item_info, viewGroup, false)
        return ViewHolder(rowView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val titleView: AppCompatTextView = itemView.findViewById(R.id.infoTitleView)
        private val contentView: AppCompatTextView = itemView.findViewById(R.id.infoContentView)

        fun bind(information: Information){
            titleView.text = itemView.context.getString(information.id)
            contentView.text = information.content
        }
    }
}