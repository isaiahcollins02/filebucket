package com.isaiahvonrundstedt.bucket.adapters.filterable

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.activities.wrapper.FrameActivity
import com.isaiahvonrundstedt.bucket.constants.Params
import com.isaiahvonrundstedt.bucket.fragments.bottomsheet.DetailsBottomSheet
import com.isaiahvonrundstedt.bucket.objects.core.File
import com.isaiahvonrundstedt.bucket.service.UsageService
import com.isaiahvonrundstedt.bucket.utils.managers.DataManager
import com.isaiahvonrundstedt.bucket.utils.managers.ItemManager
import java.text.DecimalFormat

class VaultAdapter (private var itemList: ArrayList<File>,
                    private var fragmentManager: FragmentManager
    ): RecyclerView.Adapter<VaultAdapter.CoreViewHolder>(), Filterable {

    private var windowContext: Context? = null
    private var filterList: ArrayList<File> = itemList
    private var filter: SentFiler? = null

    companion object {
        const val viewTypeCore: Int = 0
        const val viewTypeEmpty: Int = 1
    }

    fun removeAllData(){
        filterList.clear()
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: CoreViewHolder, position: Int) {
        holder.bindData(filterList[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoreViewHolder {
        windowContext = parent.context
        val rootView: View = LayoutInflater.from(parent.context).inflate(R.layout.layout_item_files, parent, false)
        return CoreViewHolder(rootView)
    }

    override fun getItemCount(): Int {
        return filterList.size
    }

    inner class CoreViewHolder (itemView: View): RecyclerView.ViewHolder(itemView){
        private val rootView: View = itemView.findViewById(R.id.rootView)
        private val iconView: AppCompatImageView = itemView.findViewById(R.id.iconView)
        private val titleView: TextView = itemView.findViewById(R.id.titleView)
        private val subtitleView: TextView = itemView.findViewById(R.id.subtitleView)
        private val sizeView: TextView = itemView.findViewById(R.id.sizeView)

        internal fun bindData(file: File){
            rootView.setOnClickListener {
                val args = Bundle()
                args.putParcelable("fileArgs", file)

                it.context.startService(Intent(it.context, UsageService::class.java)
                    .setAction(UsageService.sendFileUsage)
                    .putExtra(UsageService.extraObjectID, file.fileID))

                itemView.context.startActivity(Intent(itemView.context, FrameActivity::class.java)
                    .putExtra(Params.viewType, FrameActivity.viewTypeDetails)
                    .putExtra(Params.viewArgs, args))
            }
            rootView.setOnLongClickListener {
                invokeBottomSheet(file)
                true
            }

            iconView.setImageDrawable(ItemManager.getFileIcon(rootView.context, file.fileType))
            titleView.text = file.name
            subtitleView.text = DataManager.formatTimestamp(rootView.context, file.timestamp)

            val decimalFormat = DecimalFormat("#.##")
            sizeView.text = String.format(rootView.resources.getString(R.string.file_size_megabytes),
                decimalFormat.format((file.fileSize / 1024) / 1024))
        }
    }

    private fun invokeBottomSheet(file: File?){
        val argumentsBundle = Bundle().also {
            it.putParcelable("file", file)
        }

        val bottomSheet = DetailsBottomSheet()
        bottomSheet.arguments = argumentsBundle
        bottomSheet.show(fragmentManager, "bottomSheet")
    }

    override fun getFilter(): Filter {
        if (filter == null)
            filter = SentFiler()
        return filter as SentFiler
    }

    inner class SentFiler: Filter(){

        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val searchTerm: String = constraint.toString().toLowerCase()
            val filterResults = FilterResults()
            val originalList = filterList
            val listCount = filterList.size

            var resultList: ArrayList<File> = ArrayList(listCount)
            var filterableString: String

            if (searchTerm.isNotBlank() && searchTerm.isNotEmpty()) {
                for (i in 0 until listCount){
                    filterableString = originalList[i].name as String
                    if (filterableString.toLowerCase().contains(searchTerm))
                        resultList.add(originalList[i])
                }
            } else
                resultList = itemList

            filterResults.values = resultList
            filterResults.count = listCount

            return filterResults
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            @Suppress("UNCHECKED_CAST")
            filterList = results?.values as ArrayList<File>
            notifyDataSetChanged()
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return if (filterList.size == 0) viewTypeEmpty
        else viewTypeCore
    }
}