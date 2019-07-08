package com.isaiahvonrundstedt.bucket.adapters

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.net.toUri
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.bumptech.glide.RequestManager
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.fragments.bottomsheet.DetailsBottomSheet
import com.isaiahvonrundstedt.bucket.objects.File
import com.isaiahvonrundstedt.bucket.service.UsageService
import com.isaiahvonrundstedt.bucket.utils.Preferences
import com.isaiahvonrundstedt.bucket.utils.managers.DataManager
import com.isaiahvonrundstedt.bucket.utils.managers.ItemManager
import java.text.DecimalFormat

class CoreAdapter constructor (
    private val itemList: ArrayList<File>,
    private val manager: FragmentManager,
    private val requestManager: RequestManager
        ): RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    private var filterList: ArrayList<File> = itemList
    private var filter: FileFilter? = null
    private var windowContext: Context? = null

    private lateinit var request: DownloadManager.Request
    private lateinit var downloadManager: DownloadManager

    companion object {
        const val itemTypeFile = 0
        const val itemTypeImage = 1
    }

    override fun onCreateViewHolder(container: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        downloadManager = container.context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        windowContext = container.context

        val rowView: View?
        return when (viewType){
            itemTypeImage -> {
                rowView = LayoutInflater.from(container.context).inflate(R.layout.layout_item_photo, container, false)
                ImageViewHolder(rowView)
            } else -> {
                rowView = LayoutInflater.from(container.context).inflate(R.layout.layout_item_files, container, false)
                FileViewHolder(rowView)
            }
        }
    }

    override fun getItemCount(): Int {
        return filterList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentFile: File = filterList[position]
        when (holder.itemViewType){
            itemTypeImage -> (holder as ImageViewHolder).bindData(currentFile)
            else -> (holder as FileViewHolder).bindData(currentFile)
        }
    }
    
    inner class FileViewHolder (itemView: View): RecyclerView.ViewHolder(itemView){
        private val rootView: View = itemView.findViewById(R.id.rootView)
        private val iconView: AppCompatImageView = itemView.findViewById(R.id.iconView)
        private val titleView: TextView = itemView.findViewById(R.id.titleView)
        private val subtitleView: TextView = itemView.findViewById(R.id.subtitleView)
        private val sizeView: TextView = itemView.findViewById(R.id.sizeView)

        fun bindData(file: File){
            titleView.text = file.name
            subtitleView.text =
                when (Preferences(windowContext).metadata){
                    Preferences.metadataTimestamp -> DataManager.formatTimestamp(rootView.context, file.timestamp)
                    Preferences.metadataAuthor -> file.author
                    else -> null
                }
            val decimalFormat = DecimalFormat("#.##")
            sizeView.text = String.format(itemView.resources.getString(R.string.file_size_megabytes), decimalFormat.format((file.fileSize / 1024) / 1024))
            iconView.setImageDrawable(ItemManager.getFileIcon(rootView.context, file.fileType))

            rootView.setOnClickListener {
                handleFileDownload(file)
            }
            rootView.setOnLongClickListener {
                invokeBottomSheet(file)
                true
            }

        }
    }
    inner class ImageViewHolder (itemView: View): RecyclerView.ViewHolder(itemView){
        private val rootView: View = itemView.findViewById(R.id.rootView)
        private val containerView: AppCompatImageView = itemView.findViewById(R.id.iconView)
        private val titleView: TextView = itemView.findViewById(R.id.titleView)
        private val subtitleView: TextView = itemView.findViewById(R.id.subtitleView)
        private val sizeView: TextView = itemView.findViewById(R.id.sizeView)

        fun bindData(file: File){
            requestManager.clear(containerView)
            requestManager.asBitmap()
                .load(file?.downloadURL)
                .centerCrop()
                .into(containerView)

            titleView.text = file.name
            subtitleView.text = when (Preferences(windowContext).metadata){
                Preferences.metadataTimestamp -> DataManager.formatTimestamp(windowContext, file.timestamp)
                Preferences.metadataAuthor -> file.author
                else -> null
            }
            val decimalFormat = DecimalFormat("#.##")
            sizeView.text = String.format(itemView.resources.getString(R.string.file_size_megabytes), decimalFormat.format((file.fileSize / 1024) / 1024))

            rootView.setOnClickListener {
                handleFileDownload(file)
            }
            rootView.setOnLongClickListener {
                invokeBottomSheet(file)
                true
            }
        }
    }

    private fun invokeBottomSheet(file: File?){
        val argumentsBundle = Bundle().also {
            it.putParcelable("file", file)
        }

        val bottomSheet = DetailsBottomSheet()
        bottomSheet.arguments = argumentsBundle
        bottomSheet.show(manager, "bottomSheet")
    }

    override fun getFilter(): Filter {
        if (filter == null)
            filter = FileFilter()
        return filter as FileFilter
    }

    inner class FileFilter: Filter(){

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

    private fun handleFileDownload(file: File?){
        val externalDir: String? = Preferences(windowContext).downloadDirectory
        val bufferedFile = java.io.File(externalDir, file?.name)

        MaterialDialog(windowContext!!).show {
            title(text = String.format(context.getString(R.string.dialog_file_download_title), file?.name))
            message(R.string.dialog_file_download_summary)
            positiveButton(R.string.button_download) {
                request = DownloadManager.Request(Uri.parse(file?.downloadURL))
                    .setTitle(windowContext.getString(R.string.notification_downloading_file))
                    .setDestinationUri(bufferedFile.toUri())

                downloadManager.enqueue(request)

                it.context.startService(Intent(it.context, UsageService::class.java)
                    .setAction(UsageService.sendFileUsage)
                    .putExtra(UsageService.extraObjectID, file?.fileID))
            }
            negativeButton(R.string.button_cancel)
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return when (filterList[position].fileType){
            File.TYPE_IMAGE -> {
                if (Preferences(windowContext).previewPreference != false)
                    itemTypeImage
                else itemTypeFile
            }
            else -> itemTypeFile
        }
    }

    fun removeAllData(){
        if (itemList.size > 0 && filterList.size > 0) {
            itemList.clear()
            filterList.clear()
            notifyDataSetChanged()
        }
    }

}