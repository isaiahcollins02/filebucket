package com.isaiahvonrundstedt.bucket.adapters.experience

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.constants.Params
import com.isaiahvonrundstedt.bucket.objects.core.File
import com.isaiahvonrundstedt.bucket.objects.experience.Generic
import com.isaiahvonrundstedt.bucket.utils.managers.DataManager
import com.isaiahvonrundstedt.bucket.utils.managers.ItemManager

class SearchAdapter(private var context: Context?): RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    private var itemList: ArrayList<Generic> = ArrayList()

    fun setObservableItems(items: List<Generic>){
        val callback = ItemCallback(itemList, items)
        val result = DiffUtil.calculateDiff(callback)

        itemList.clear()
        itemList.addAll(items)
        result.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val rowView: View = LayoutInflater.from(viewGroup.context).inflate(R.layout.layout_item_generic, viewGroup, false)
        return ViewHolder(rowView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(itemList[position])
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class ItemCallback(private var oldItems: List<Generic>, private var newItems: List<Generic>): DiffUtil.Callback() {
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

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val iconView: AppCompatImageView = itemView.findViewById(R.id.iconView)
        private val titleView: TextView = itemView.findViewById(R.id.titleView)
        private val subtitleView: TextView = itemView.findViewById(R.id.subtitleView)

        fun bind(result: Generic){
            val bundleArgs = result.payload
            val actualPayload: File? = bundleArgs?.getParcelable(Params.payload)

            iconView.setImageDrawable(ItemManager.getFileIcon(context, actualPayload?.fileType))
            titleView.text = actualPayload?.name
            subtitleView.text = DataManager.formatTimestamp(context, actualPayload?.timestamp)
        }
    }

}