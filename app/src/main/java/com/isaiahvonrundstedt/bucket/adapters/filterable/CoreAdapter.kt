package com.isaiahvonrundstedt.bucket.adapters.filterable

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.bumptech.glide.RequestManager
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.fragments.bottomsheet.DetailsBottomSheet
import com.isaiahvonrundstedt.bucket.objects.core.File
import com.isaiahvonrundstedt.bucket.service.FetchService
import com.isaiahvonrundstedt.bucket.service.UsageService
import com.isaiahvonrundstedt.bucket.utils.Preferences
import com.isaiahvonrundstedt.bucket.utils.managers.DataManager
import com.isaiahvonrundstedt.bucket.utils.managers.ItemManager
import java.text.DecimalFormat

class CoreAdapter (
    private val context: Context?,
    private val manager: FragmentManager,
    private val requestManager: RequestManager
        ): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var itemList: ArrayList<File> = ArrayList()

    companion object {
        const val itemTypeFile = 0
        const val itemTypeImage = 1
    }

    fun setObservableItems(items: List<File>){
        val callback = ItemCallback(itemList, items)
        val result = DiffUtil.calculateDiff(callback)

        itemList.clear()
        itemList.addAll(items)

        result.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(container: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
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
        return itemList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentFile: File = itemList[position]
        when (holder.itemViewType){
            itemTypeImage -> (holder as ImageViewHolder).bindData(currentFile)
            else -> (holder as FileViewHolder).bindData(currentFile)
        }
    }

    inner class ItemCallback (private var oldItems: List<File>, private var newItems: List<File>): DiffUtil.Callback() {

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItems[oldItemPosition].fileID == newItems[newItemPosition].fileID
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
    
    inner class FileViewHolder (itemView: View): RecyclerView.ViewHolder(itemView){
        private val rootView: View = itemView.findViewById(R.id.rootView)
        private val iconView: AppCompatImageView = itemView.findViewById(R.id.iconView)
        private val titleView: TextView = itemView.findViewById(R.id.titleView)
        private val subtitleView: TextView = itemView.findViewById(R.id.subtitleView)
        private val sizeView: TextView = itemView.findViewById(R.id.sizeView)

        fun bindData(file: File){
            titleView.text = file.name
            subtitleView.text =
                when (Preferences(context).metadata){
                    Preferences.metadataTimestamp -> DataManager.formatTimestamp(rootView.context, file.timestamp)
                    Preferences.metadataAuthor -> file.author
                    else -> null
                }
            sizeView.text = DataManager.formatSize(itemView.context, file.fileSize)
            iconView.setImageDrawable(ItemManager.getFileIcon(rootView.context, file.fileType))

            rootView.setOnClickListener { download(file) }
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
                .load(file.downloadURL)
                .centerCrop()
                .into(containerView)

            titleView.text = file.name
            subtitleView.text = when (Preferences(context).metadata){
                Preferences.metadataTimestamp -> DataManager.formatTimestamp(context, file.timestamp)
                Preferences.metadataAuthor -> file.author
                else -> null
            }
            val decimalFormat = DecimalFormat("#.##")
            sizeView.text = String.format(itemView.resources.getString(R.string.file_size_megabytes), decimalFormat.format((file.fileSize / 1024) / 1024))

            rootView.setOnClickListener {
                download(file)
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

    private fun download(file: File?){
        MaterialDialog(context!!).show {
            title(text = String.format(context.getString(R.string.dialog_file_download_title), file?.name))
            message(R.string.dialog_file_download_summary)
            positiveButton(R.string.button_download) {
                it.context.startService(Intent(it.context, FetchService::class.java)
                    .setAction(FetchService.actionDownload)
                    .putExtra(FetchService.extraFileName, file?.name)
                    .putExtra(FetchService.extraDownloadURL, file?.downloadURL))

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
        return when (itemList[position].fileType){
            File.TYPE_IMAGE -> {
                if (Preferences(context).previewPreference != false)
                    itemTypeImage
                else itemTypeFile
            }
            else -> itemTypeFile
        }
    }
}