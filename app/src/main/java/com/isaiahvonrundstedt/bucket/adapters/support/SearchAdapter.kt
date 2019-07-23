package com.isaiahvonrundstedt.bucket.adapters.support

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.objects.core.File
import com.isaiahvonrundstedt.bucket.utils.managers.DataManager

class SearchAdapter(private var itemList: ArrayList<File>): RecyclerView.Adapter<SearchAdapter.ViewHolder>(){

    private val filterList: ArrayList<File> = ArrayList()

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val rowView: View = LayoutInflater.from(viewGroup.context).inflate(R.layout.layout_item_files, viewGroup, false)
        return ViewHolder(rowView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return filterList.size
    }

    inner class ItemCallback(private val oldList: List<File>, private val newList: List<File>): DiffUtil.Callback() {
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].fileID == newList[newItemPosition].fileID
        }

        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val rootView: View = itemView.findViewById(R.id.rootView)
        private val iconView: AppCompatImageView = itemView.findViewById(R.id.iconView)
        private val titleView: AppCompatTextView = itemView.findViewById(R.id.titleView)
        private val subtitleView: AppCompatTextView = itemView.findViewById(R.id.subtitleView)
        private val sizeView: AppCompatTextView = itemView.findViewById(R.id.sizeView)

        fun bind(file: File){
            titleView.text = file.name
            subtitleView.text = DataManager.formatTimestamp(itemView.context, file.timestamp)
            sizeView.text = DataManager.formatSize(itemView.context, file.fileSize)
        }
    }

    inner class ItemFilter: Filter(){
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val searchTerm = constraint.toString().toLowerCase()
            val filterResults = FilterResults()

            val sourceList: List<File> = itemList
            val sourceListSize: Int = sourceList.size

            val filterList: ArrayList<File> = ArrayList(sourceListSize)
            var filterableString: String
            sourceList.forEachIndexed { _, file ->
                filterableString = file.name!!
                if (filterableString.toLowerCase().contains(searchTerm))
                    filterList.add(file)
            }

            filterResults.count = filterList.size
            filterResults.values = filterList

            return filterResults
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            filterList.addAll(results?.values as ArrayList<File>)
            notifyDataSetChanged()
        }

    }
}