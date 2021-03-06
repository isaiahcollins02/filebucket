package com.isaiahvonrundstedt.bucket.features.shared.abstracts

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import com.isaiahvonrundstedt.bucket.CoreApplication
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.features.boxes.BoxContentActivity
import com.isaiahvonrundstedt.bucket.constants.Params
import com.isaiahvonrundstedt.bucket.features.shared.ui.DetailFragment
import com.isaiahvonrundstedt.bucket.features.shared.ui.ViewerFragment
import com.isaiahvonrundstedt.bucket.features.auth.Account
import com.isaiahvonrundstedt.bucket.features.notifications.Notification
import com.isaiahvonrundstedt.bucket.features.shared.StorageItem
import com.isaiahvonrundstedt.bucket.service.FetchService
import com.isaiahvonrundstedt.bucket.features.usage.UsageService
import com.isaiahvonrundstedt.bucket.utils.Preferences
import java.io.File

abstract class BaseAdapter(): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    protected lateinit var context: Context
    
    private lateinit var fragmentManager: FragmentManager
    private lateinit var requestManager: RequestManager

    constructor (_context: Context): this() {
        context = _context
    }

    constructor (_context: Context,
                 _fragmentManager: FragmentManager,
                 _requestManager: RequestManager) : this() {

        context = _context
        fragmentManager = _fragmentManager
        requestManager = _requestManager
    }

    companion object {
        const val viewTypeImage = 1
        const val viewTypeFilePublic = 2
    }

    /**
     *  Used when comparing StorageItem data class objects in the list observed
     *  by the ViewModel
     */
    protected class ItemDiffCallback(private val oldItems: List<StorageItem>,
                                     private val newItems: List<StorageItem>): DiffUtil.Callback(){
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItems[oldItemPosition].id == newItems[newItemPosition].id
        }

        override fun getOldListSize(): Int = oldItems.size

        override fun getNewListSize(): Int = newItems.size

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItems[oldItemPosition] == newItems[newItemPosition]
        }
    }

    /**
     *  Used for comparing Account data objects from the list observed by the
     *  ViewModel
     */
    protected class BoxDiffCallback(private val oldItems: List<Account>,
                                    private val newItems: List<Account>): DiffUtil.Callback(){

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItems[oldItemPosition].accountID == newItems[newItemPosition].accountID
        }

        override fun getOldListSize(): Int = oldItems.size

        override fun getNewListSize(): Int = newItems.size

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItems[oldItemPosition] == newItems[newItemPosition]
        }
    }

    protected abstract class CoreViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        protected val rootView: View = itemView.findViewById(R.id.rootView)
        protected val iconView: AppCompatImageView = itemView.findViewById(R.id.iconView)
        protected val titleView: AppCompatTextView = itemView.findViewById(R.id.titleView)
        protected val subtitleView: AppCompatTextView = itemView.findViewById(R.id.subtitleView)
    }

    /**
     *  Base class for creating a ViewHolder for a StorageItem object, this
     *  class should use the layout R.layout.layout_files to be able to bind
     *  the items successfully.
     */
    protected abstract class FileViewHolder(itemView: View): CoreViewHolder(itemView){
        internal var sizeView: AppCompatTextView = itemView.findViewById(R.id.sizeView)

        abstract fun onBindData(item: StorageItem)
    }

    /**
     *  Used for StorageItem objects that cannot be "Downloaded", the rootView is setup to
     *  show a detail dialog when the user taps on it.
     */
    protected inner class SentFileViewHolder(itemView: View): FileViewHolder(itemView){
        override fun onBindData(item: StorageItem) {
            rootView.setOnClickListener { showDetailDialog(item) }
            titleView.text = item.name
            setMetadata(subtitleView, item)
            sizeView.text = item.formatSize(itemView.context)

            val icon = obtainTintedDrawable(StorageItem.obtainIconID(item.type), StorageItem.obtainColorID(item.type))
            iconView.setImageDrawable(icon)
        }
    }

    /**
     *  Used for StorageItem objects that can be "Downloaded", the rootView is setup to
     *  show a confirmation dialog to Download the file from Firebase.
     */
    protected inner class SharedFileViewHolder(itemView: View): FileViewHolder(itemView){
        override fun onBindData(item: StorageItem) {
            rootView.setOnClickListener { onDownload(item) }
            rootView.setOnLongClickListener { showDetailDialog(item); true }
            titleView.text = item.name
            setMetadata(subtitleView, item)
            sizeView.text = item.formatSize(itemView.context)

           val icon = obtainTintedDrawable(StorageItem.obtainIconID(item.type), StorageItem.obtainColorID(item.type))
            iconView.setImageDrawable(icon)
        }
    }

    /**
     *  Used for display a Reddit-like post for the StorageItem objects that are
     *  images.
     */
    protected inner class ImageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val rootView: View = itemView.findViewById(R.id.rootView)
        private val containerView: AppCompatImageView = itemView.findViewById(R.id.containerView)
        private val titleView: AppCompatTextView = itemView.findViewById(R.id.titleView)
        private val subtitleView: AppCompatTextView = itemView.findViewById(R.id.subtitleView)

        fun onBindData(item: StorageItem){
            rootView.setOnClickListener { viewImage(item) }
            rootView.setOnLongClickListener { onDownload(item); true }
            fetchImageAsset(item.args, containerView)
            titleView.text = item.name
            setMetadata(subtitleView, item)
        }
    }

    /**
     *  Used for local StorageItem objects that are in the default downloads
     *  directory. The rootView is setup to open the app that handles the file
     */
    protected inner class LocalViewHolder(itemView: View, private var listener: DirectoryListener): FileViewHolder(itemView){
        override fun onBindData(item: StorageItem) {
            rootView.setOnClickListener {
                if (item.type != StorageItem.typeDirectory)
                    onParseIntent(rootView, item)
                else listener.onFolderPressed(item.args)
            }
            titleView.text = item.name
            subtitleView.text = context.getString(StorageItem.obtainItemTypeID(item.type))

            val icon = obtainTintedDrawable(StorageItem.obtainIconID(item.type), StorageItem.obtainColorID(item.type))
            iconView.setImageDrawable(icon)
        }
    }

    /**
     *   Used for Account item objects; the rootView is setup to launch VaultActivity
     */
    protected inner class BoxViewHolder(itemView: View): CoreViewHolder(itemView){
        fun onBindData(account: Account){
            val fullName: String? = "${account.firstName} ${account.lastName}"
            rootView.setOnClickListener { it.context.startActivity(Intent(it.context, BoxContentActivity::class.java)
                .putExtra(Params.author, fullName)
                .putExtra(Params.authorID, account.accountID)) }

            fetchProfileImage(account.imageURL, iconView)
            titleView.text = fullName
            subtitleView.text = account.email
        }
    }

    protected inner class NotificationViewHolder(itemView: View): CoreViewHolder(itemView) {
        fun onBindData(notification: Notification) {
            rootView.setOnClickListener { onParseIntent(itemView, notification) }
            titleView.text = notification.title
            subtitleView.text = notification.content

            val icon = obtainTintedDrawable(Notification.obtainIconRes(notification.type), Notification.obtainColorRes(notification.type))
            iconView.setImageDrawable(icon)
        }
    }
    
    private fun fetchProfileImage(imageURL: String?, container: AppCompatImageView){
        var drawable: Drawable? = null
        if (imageURL == null){
            drawable = obtainTintedDrawable(R.drawable.ic_user, R.color.colorPrimary)
        }

        requestManager.clear(container)
        requestManager.asBitmap()
            .placeholder(R.drawable.ic_user)
            .error(drawable)
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
    protected fun showDetailDialog(item: StorageItem?){
        val args = Bundle()
        args.putParcelable(Params.payload, item)

        val detailDialog = DetailFragment()
        detailDialog.arguments = args
        detailDialog.invoke(fragmentManager)
    }
    protected fun onDownload(item: StorageItem) {
        MaterialDialog(context).show {
            title(R.string.dialog_file_download_title)
            message(R.string.dialog_file_download_summary)
            positiveButton(R.string.action_download) {
                it.context.startService(Intent(it.context, FetchService::class.java)
                    .setAction(FetchService.actionDownload)
                    .putExtra(FetchService.extraFileName, item.name)
                    .putExtra(FetchService.extraDownloadURL, item.args))

                it.context.startService(Intent(it.context, UsageService::class.java)
                    .setAction(UsageService.sendFileUsage)
                    .putExtra(UsageService.extraObjectID, item.id))
            }
            negativeButton(R.string.action_cancel)
        }
    }
    private fun setMetadata(view: AppCompatTextView, item: StorageItem) {
        when (Preferences(context).metadata){
            Preferences.metadataAuthor -> item.fetchAuthorName { view.text = it ?: getString(R.string.unknown_file_author) }
            Preferences.metadataTimestamp -> view.text = item.formatTimestamp(context)
            Preferences.metadataType -> view.text = context.getString(StorageItem.obtainItemTypeID(item.type))
        }
    }

    private fun onParseIntent(view: View, item: Notification){
        val uri: Uri? = FileProvider.getUriForFile(context, CoreApplication.appProvider, File(Uri.parse(item.objectArgs).path!!))
        context.grantUriPermission(context.packageName, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        val intent = Intent(Intent.ACTION_VIEW)
            .setDataAndType(uri, context.contentResolver.getType(uri!!))
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        if (intent.resolveActivity(context.packageManager) != null)
            context.startActivity(intent)
        else
            Snackbar.make(view, R.string.status_intent_no_app, Snackbar.LENGTH_SHORT).show()
    }

    private fun onParseIntent(view: View, item: StorageItem){
        if (item.type != StorageItem.typeDirectory){
            val uri: Uri? = FileProvider.getUriForFile(context, CoreApplication.appProvider, File(Uri.parse(item.args).path!!))
            context.grantUriPermission(context.packageName, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            val intent = Intent(Intent.ACTION_VIEW)
                .setDataAndType(uri, context.contentResolver.getType(uri!!))
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

            if (intent.resolveActivity(context.packageManager) != null)
                context.startActivity(intent)
            else
                Snackbar.make(view, R.string.status_intent_no_app, Snackbar.LENGTH_SHORT).show()
        }
    }
    private fun viewImage(item: StorageItem?){
        val args = Bundle()
        args.putParcelable(Params.payload, item)

        val viewer = ViewerFragment()
        viewer.arguments = args
        viewer.invoke(fragmentManager)
    }

    fun getString(@StringRes id: Int): String? = context.getString(id)

    fun obtainTintedDrawable(@DrawableRes drawableRes: Int, @ColorRes colorRes: Int): Drawable {
        var drawable: Drawable = ContextCompat.getDrawable(context, drawableRes) as Drawable

        val color = ContextCompat.getColor(context, colorRes)

        drawable = DrawableCompat.wrap(drawable)
        DrawableCompat.setTint(drawable, color)

        return drawable
    }

    interface DirectoryListener {
        fun onFolderPressed(directory: String?)
    }

}