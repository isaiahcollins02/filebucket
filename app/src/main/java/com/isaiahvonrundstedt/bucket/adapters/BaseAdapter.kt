package com.isaiahvonrundstedt.bucket.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.isaiahvonrundstedt.bucket.BaseApp
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.activities.support.VaultActivity
import com.isaiahvonrundstedt.bucket.constants.Params
import com.isaiahvonrundstedt.bucket.fragments.bottomsheet.DetailsBottomSheet
import com.isaiahvonrundstedt.bucket.fragments.screendialog.DetailFragment
import com.isaiahvonrundstedt.bucket.fragments.screendialog.ViewerFragment
import com.isaiahvonrundstedt.bucket.objects.core.Account
import com.isaiahvonrundstedt.bucket.objects.core.File
import com.isaiahvonrundstedt.bucket.objects.core.LocalFile
import com.isaiahvonrundstedt.bucket.service.FetchService
import com.isaiahvonrundstedt.bucket.service.UsageService
import com.isaiahvonrundstedt.bucket.utils.Preferences
import com.isaiahvonrundstedt.bucket.utils.managers.DataManager
import com.isaiahvonrundstedt.bucket.utils.managers.ItemManager

abstract class BaseAdapter(private var context: Context?,
                           private var fragmentManager: FragmentManager,
                           private var requestManager: RequestManager): RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    
    companion object {
        const val viewTypeImage = 1
        const val viewTypeFilePublic = 2
        const val viewTypeFileSent = 3
        const val viewTypeFileLocal = 4
        const val viewTypeBox = 5

        internal const val bottomSheetTag = "detailsBottomSheet"
        internal const val detailScreenTag = "detailScreenDialog"
        internal const val viewerScreenTag = "viewerScreenDialog"
    }
    
    internal class FileDiffCallback(private val oldItems: List<File>, private val newItems: List<File>): DiffUtil.Callback(){
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItems[oldItemPosition].fileID == newItems[newItemPosition].fileID
        }

        override fun getOldListSize(): Int = oldItems.size

        override fun getNewListSize(): Int = newItems.size

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItems[oldItemPosition] == newItems[newItemPosition]
        }
    }
    internal class LocalFileDiffCallback(private val oldItems: List<LocalFile>, private val newItems: List<LocalFile>): DiffUtil.Callback(){
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItems[oldItemPosition].id == newItems[newItemPosition].id
        }

        override fun getOldListSize(): Int = oldItems.size

        override fun getNewListSize(): Int = newItems.size

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItems[oldItemPosition] == newItems[newItemPosition]
        }
    }
    internal class BoxDiffCallback(private val oldItems: List<Account>, private val newItems: List<Account>): DiffUtil.Callback(){
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItems[oldItemPosition].accountID == newItems[newItemPosition].accountID
        }

        override fun getOldListSize(): Int = oldItems.size

        override fun getNewListSize(): Int = newItems.size

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItems[oldItemPosition] == newItems[newItemPosition]
        }
    }


    abstract class FileViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        internal var rootView: View = itemView.findViewById(R.id.rootView)
        internal var iconView: AppCompatImageView = itemView.findViewById(R.id.iconView)
        internal var titleView: AppCompatTextView = itemView.findViewById(R.id.titleView)
        internal var subtitleView: AppCompatTextView = itemView.findViewById(R.id.subtitleView)
        internal var sizeView: AppCompatTextView = itemView.findViewById(R.id.sizeView)

        abstract fun onBindData(file: File?)
    }
    
    internal inner class SentFileViewHolder(itemView: View): FileViewHolder(itemView){
        override fun onBindData(file: File?) {
            rootView.setOnClickListener { showDetailDialog(file) }
            rootView.setOnLongClickListener { showDetailSheet(file); true }
            iconView.setImageDrawable(ItemManager.getFileIcon(context, file?.fileType))
            titleView.text = file?.name
            subtitleView.text = setMetadata(file)
            sizeView.text = DataManager.formatSize(context, file?.fileSize)
        }
    }
    
    internal inner class PublicFileViewHolder(itemView: View): FileViewHolder(itemView){
        override fun onBindData(file: File?) {
            rootView.setOnClickListener { onDownload(file) }
            rootView.setOnLongClickListener { showDetailSheet(file); true }
            iconView.setImageDrawable(ItemManager.getFileIcon(context, file?.fileType))
            titleView.text = file?.name
            subtitleView.text = setMetadata(file)
            sizeView.text = DataManager.formatSize(context, file?.fileSize)
        }
        
    }

    internal inner class ImageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val rootView: View = itemView.findViewById(R.id.rootView)
        private val containerView: AppCompatImageView = itemView.findViewById(R.id.containerView)
        private val titleView: AppCompatTextView = itemView.findViewById(R.id.titleView)
        private val subtitleView: AppCompatTextView = itemView.findViewById(R.id.subtitleView)

        fun onBindData(file: File?){
            rootView.setOnClickListener { viewImage(file) }
            rootView.setOnLongClickListener { showDetailSheet(file); true }
            fetchImageAsset(file?.downloadURL, containerView)
            titleView.text = file?.name
            subtitleView.text = setMetadata(file)
        }
    }
    
    internal inner class LocalViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        internal var rootView: View = itemView.findViewById(R.id.rootView)
        internal var iconView: AppCompatImageView = itemView.findViewById(R.id.iconView)
        internal var titleView: AppCompatTextView = itemView.findViewById(R.id.titleView)
        internal var subtitleView: AppCompatTextView = itemView.findViewById(R.id.subtitleView)
        internal var sizeView: AppCompatTextView = itemView.findViewById(R.id.sizeView)
        
        fun onBindData(localFile: LocalFile?){
            rootView.setOnClickListener { onParseIntent(localFile) }
            iconView.setImageDrawable(ItemManager.getLocalIcon(context, localFile?.type))
            titleView.text = localFile?.name
            subtitleView.text = DataManager.formatDate(context, localFile?.date)
            sizeView.text = DataManager.formatSize(itemView.context, localFile?.size)
        }
    }

    internal inner class BoxViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val rootView: View = itemView.findViewById(R.id.rootView)
        val iconView: AppCompatImageView = itemView.findViewById(R.id.iconView)
        val titleView: AppCompatTextView = itemView.findViewById(R.id.titleView)
        val subtitleView: AppCompatTextView = itemView.findViewById(R.id.subtitleView)

        fun onBindData(account: Account?){
            val fullName: String? = "${account?.firstName} ${account?.lastName}"
            rootView.setOnClickListener { it.context.startActivity(Intent(it.context, VaultActivity::class.java)
                .putExtra(Params.author, fullName)) }
            fetchProfileImage(account?.imageURL, iconView)
            titleView.text = fullName
            subtitleView.text = account?.email
        }
    }
    
    private fun fetchProfileImage(imageURL: String?, container: AppCompatImageView){
        requestManager.clear(container)
        requestManager.asBitmap()
            .placeholder(R.drawable.ic_brand_user)
            .error(R.drawable.ic_brand_user)
            .load(imageURL)
            .apply(RequestOptions().circleCrop())
            .into(container)
    }
    private fun fetchImageAsset(imageURL: String?, container: AppCompatImageView){
        requestManager.clear(container)
        requestManager.asBitmap()
            .load(imageURL)
            .into(container)
    }
    private fun showDetailSheet(file: File?){
        val bundleArgs = Bundle()
        bundleArgs.putParcelable(Params.args, file)

        val bottomSheet = DetailsBottomSheet()
        bottomSheet.arguments = bundleArgs
        bottomSheet.show(fragmentManager, bottomSheetTag)
    }
    private fun showDetailDialog(file: File?){
        val args = Bundle()
        args.putParcelable(Params.args, file)

        val detailDialog = DetailFragment()
        detailDialog.arguments = args
        detailDialog.show(fragmentManager, detailScreenTag)
    }
    private fun onDownload(file: File?){
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
    private fun setMetadata(file: File?): String? {
        return when (Preferences(context).metadata){
            Preferences.metadataAuthor -> file?.author
            Preferences.metadataTimestamp -> DataManager.formatTimestamp(context, file?.timestamp)
            else -> null
        }
    }
    private fun onParseIntent(localFile: LocalFile?){
        if (localFile?.type == LocalFile.file){
            val uri: Uri? = FileProvider.getUriForFile(context!!, BaseApp.appPackage + ".components.AppFileProvider", java.io.File(localFile?.args?.path))
            context?.grantUriPermission(context?.packageName, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            val intent = Intent(Intent.ACTION_VIEW)
                .setDataAndType(uri, context?.contentResolver?.getType(uri!!))
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            if (intent.resolveActivity(context?.packageManager!!) != null)
                context?.startActivity(intent)
        }
    }
    private fun viewImage(file: File?){
        if (fragmentManager.findFragmentByTag(viewerScreenTag)?.isAdded != true){
            val args = Bundle()
            args.putParcelable(Params.args, file)

            val viewer = ViewerFragment()
            viewer.arguments = args
            viewer.show(fragmentManager, viewerScreenTag)
        }
    }

}