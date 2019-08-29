package com.isaiahvonrundstedt.bucket.fragments.screendialog

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.api.Usage
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseScreenDialog
import com.isaiahvonrundstedt.bucket.components.modules.GlideApp
import com.isaiahvonrundstedt.bucket.constants.Params
import com.isaiahvonrundstedt.bucket.objects.core.StorageItem
import com.isaiahvonrundstedt.bucket.service.FetchService
import com.isaiahvonrundstedt.bucket.service.UsageService
import kotlinx.android.synthetic.main.layout_dialog_viewer.*

class ViewerFragment: BaseScreenDialog(), RequestListener<Drawable> {

    private var storageItem: StorageItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        storageItem = arguments?.getParcelable(Params.payload)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_dialog_viewer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar?.inflateMenu(R.menu.menu_viewer)
        toolbar?.setOnMenuItemClickListener {
            when (it.itemId){
                R.id.action_save -> {
                    context?.startService(Intent(context, FetchService::class.java)
                        .setAction(FetchService.actionDownload)
                        .putExtra(FetchService.extraFileName, storageItem?.name)
                        .putExtra(FetchService.extraDownloadURL, storageItem?.args))

                    context?.startService(Intent(context, UsageService::class.java)
                        .setAction(UsageService.sendFileUsage)
                        .putExtra(UsageService.extraObjectID, storageItem?.id))

                    true
                }
                R.id.action_info -> {
                    val bundle = Bundle()
                    bundle.putParcelable(Params.payload, storageItem)

                    val detail = DetailFragment()
                    detail.arguments = bundle
                    detail.invoke(childFragmentManager)

                    true
                } else -> false
            }
        }
        toolbarTitle?.text = storageItem?.name
    }

    override fun onStart() {
        super.onStart()

        GlideApp.with(this)
            .load(storageItem?.args)
            .listener(this)
            .into(containerView)
            .clearOnDetach()

    }

    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
        progressBar.isVisible = false
        return false
    }

    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
        progressBar.isVisible = false
        return false
    }
}