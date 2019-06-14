package com.isaiahvonrundstedt.bucket.core.adapters

import android.app.DownloadManager
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.google.android.material.button.MaterialButton
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.core.components.modules.GlideApp
import com.isaiahvonrundstedt.bucket.core.interfaces.TransferListener
import com.isaiahvonrundstedt.bucket.core.objects.File
import com.isaiahvonrundstedt.bucket.core.utils.Preferences
import com.isaiahvonrundstedt.bucket.core.utils.managers.DataManager
import com.isaiahvonrundstedt.bucket.core.utils.managers.ItemManager
import com.isaiahvonrundstedt.bucket.core.utils.managers.Metrics
import com.isaiahvonrundstedt.bucket.experience.fragments.bottomsheet.DetailsBottomSheet
import jp.wasabeef.glide.transformations.CropSquareTransformation

class CoreAdapter constructor (
    private val itemList: ArrayList<File>,
    private val manager: FragmentManager,
    private val requestManager: RequestManager,
    private val transferListener: TransferListener
        ): RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    private var downloadID: Long = 0
    private var filterList: ArrayList<File> = itemList
    private var filter: FileFilter? = null
    private var windowContext: Context? = null

    private lateinit var request: DownloadManager.Request
    private lateinit var downloadManager: DownloadManager

    companion object {
        private const val ITEM_TYPE_FILE = 0
        private const val ITEM_TYPE_IMAGE = 1
        private const val ITEM_TYPE_PACKAGE = 2
    }

    override fun onCreateViewHolder(container: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        downloadManager = container.context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        windowContext = container.context

        val rowView: View?
        return when (viewType){
            ITEM_TYPE_IMAGE -> {
                rowView = LayoutInflater.from(container.context).inflate(R.layout.layout_item_photo, container, false)
                ImageViewHolder(rowView)
            } ITEM_TYPE_PACKAGE -> {
                rowView = LayoutInflater.from(container.context).inflate(R.layout.layout_item_package, container, false)
                PackageViewHolder(rowView)
            } else -> {
                rowView = LayoutInflater.from(container.context).inflate(R.layout.layout_item_files_main, container, false)
                FileViewHolder(rowView)
            }
        }
    }

    private fun handleThemeChanges(view: View){
        val backgroundColor = when (Preferences(view.context).theme){
            Preferences.THEME_LIGHT -> ContextCompat.getColor(view.context, R.color.colorCardLight)
            Preferences.THEME_DARK -> ContextCompat.getColor(view.context, R.color.colorCardDark)
            Preferences.THEME_AMOLED -> ContextCompat.getColor(view.context, R.color.colorGenericBlack)
            else -> ContextCompat.getColor(view.context, android.R.color.transparent)
        }

        view.setBackgroundColor(backgroundColor)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentFile: File = filterList[position]
        when (holder.itemViewType){
            ITEM_TYPE_IMAGE -> (holder as ImageViewHolder).bindData(currentFile)
            ITEM_TYPE_PACKAGE -> (holder as PackageViewHolder).bindData(currentFile)
            else -> (holder as FileViewHolder).bindData(currentFile)
        }
    }

    override fun getItemCount(): Int {
        return filterList.size
    }

    inner class FileViewHolder (itemView: View): RecyclerView.ViewHolder(itemView){
        private val rootView: View = itemView.findViewById(R.id.rootView)
        private val iconView: AppCompatImageView = itemView.findViewById(R.id.iconView)
        private val titleView: TextView = itemView.findViewById(R.id.titleView)
        private val subtitleView: TextView = itemView.findViewById(R.id.subtitleView)

        fun bindData(file: File){
            handleThemeChanges(rootView)
            titleView.text = file.name
            subtitleView.text =
                when (Preferences(windowContext).metadata){
                    Preferences.METADATA_TIMESTAMP -> DataManager.formatTimestamp(rootView.context, file.timestamp)
                    Preferences.METADATA_AUTHOR -> file.author
                    else -> null
                }
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

    inner class PackageViewHolder (itemView: View): RecyclerView.ViewHolder(itemView){
        private val rootView: View = itemView.findViewById(R.id.rootView)
        private val titleView: TextView = itemView.findViewById(R.id.titleView)
        private val subtitleView: TextView = itemView.findViewById(R.id.subtitleView)
        private val actionButton: MaterialButton = itemView.findViewById(R.id.actionButton)

        fun bindData(file: File?){
            handleThemeChanges(rootView)
            titleView.text = file?.name
            subtitleView.text = file?.author
            actionButton.setOnClickListener {
                handleFileDownload(file)
            }
            rootView.setOnClickListener {
                handleFileDownload(file)
            }
        }
    }

    inner class ImageViewHolder (itemView: View): RecyclerView.ViewHolder(itemView){
        private val rootView: View = itemView.findViewById(R.id.rootView)
        private val containerView: AppCompatImageView = itemView.findViewById(R.id.containerView)
        private val titleView: TextView = itemView.findViewById(R.id.titleView)
        private val subtitleView: TextView = itemView.findViewById(R.id.subtitleView)

        fun bindData(file: File?){
            handleThemeChanges(rootView)

            requestManager.clear(containerView)
            requestManager.asBitmap()
                .load(file?.downloadURL)
                .centerCrop()
                .into(containerView)

            titleView.text = file?.name
            subtitleView.text = when (Preferences(windowContext).metadata){
                Preferences.METADATA_TIMESTAMP -> DataManager.formatTimestamp(windowContext, file?.timestamp)
                Preferences.METADATA_AUTHOR -> file?.author
                else -> null
            }

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

                downloadID = downloadManager.enqueue(request)
                transferListener.onDownloadQueued(downloadID)
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
                if (Preferences(windowContext).previewPreference)
                    ITEM_TYPE_IMAGE
                else ITEM_TYPE_FILE
            }
            File.TYPE_PACKAGE -> ITEM_TYPE_PACKAGE
            else -> ITEM_TYPE_FILE
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