package com.isaiahvonrundstedt.bucket.fragments.navigation

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.files.fileChooser
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.adapters.core.PublicAdapter
import com.isaiahvonrundstedt.bucket.architecture.viewmodel.network.CoreViewModel
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseFragment
import com.isaiahvonrundstedt.bucket.components.custom.ItemDecoration
import com.isaiahvonrundstedt.bucket.components.modules.GlideApp
import com.isaiahvonrundstedt.bucket.fragments.bottomsheet.PickerBottomSheet
import com.isaiahvonrundstedt.bucket.interfaces.BottomSheetPicker
import com.isaiahvonrundstedt.bucket.objects.experience.Picker
import com.isaiahvonrundstedt.bucket.service.TransferService
import com.isaiahvonrundstedt.bucket.utils.Permissions
import gun0912.tedbottompicker.TedBottomPicker
import kotlinx.android.synthetic.main.fragment_cloud.*
import kotlinx.android.synthetic.main.layout_empty_no_items.*

class CloudFragment: BaseFragment(), BottomSheetPicker {

    private var fileUri: Uri? = null
    private var downloadUri: Uri? = null

    private var receiver: BroadcastReceiver? = null
    private var adapter: PublicAdapter? = null
    private var layoutManager: LinearLayoutManager? = null
    private var viewModel: CoreViewModel? = null

    private var itemPicker: PickerBottomSheet? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        receiver = object: BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                // Get a new intent from Upload Service with a success or failure
                downloadUri = intent?.getParcelableExtra(TransferService.extraDownloadURL)
                fileUri = intent?.getParcelableExtra(TransferService.extraFileURI)

                if (intent?.action == TransferService.statusError && downloadUri == null && fileUri == null)
                    Snackbar.make(view!!, R.string.notification_file_upload_error, Snackbar.LENGTH_SHORT).show()
                else if (intent?.action == TransferService.statusCompleted)
                    Snackbar.make(view!!, R.string.notification_file_upload_success, Snackbar.LENGTH_SHORT).show()
            }
        }

        viewModel = ViewModelProviders.of(this).get(CoreViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_cloud, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        layoutManager = LinearLayoutManager(context)
        adapter = PublicAdapter(context, childFragmentManager, GlideApp.with(this))

        recyclerView.layoutManager = layoutManager
        recyclerView.addOnScrollListener(onScrollListener)
        recyclerView.addItemDecoration(ItemDecoration(context))
        recyclerView.adapter = adapter

        itemPicker = PickerBottomSheet.Builder
            .setItems(getPickerItems())
            .setListener(this)
            .build()
    }

    override fun onStart() {
        super.onStart()

        val manager = LocalBroadcastManager.getInstance(context!!)
        manager.registerReceiver(receiver!!, TransferService.intentFilter)

        swipeRefreshContainer.setOnRefreshListener { onRefreshData() }
        addAction.setOnClickListener {
            if (Permissions(it.context).readAccessGranted)
                itemPicker?.invoke(childFragmentManager)
            else
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), Permissions.readRequestCode)
        }

        viewModel?.itemList?.observe(this, Observer { items ->
            adapter?.setObservableItems(items)
        })

        viewModel?.itemSize?.observe(this, Observer { size ->
            noItemView.isVisible = size == 0
        })
    }

    private var isScrolling: Boolean = false
    private var isLastItemReached: Boolean = false
    private var onScrollListener = object : RecyclerView.OnScrollListener(){
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                isScrolling = true
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val totalItemCount: Int = layoutManager?.itemCount!!
            val visibleItemCount: Int = layoutManager?.childCount!!
            val firstVisibleItems: Int = layoutManager?.findFirstVisibleItemPosition()!!

            if ((firstVisibleItems + visibleItemCount >= totalItemCount) && isScrolling && !isLastItemReached){
                isScrolling = false
                viewModel?.fetch()

                if (viewModel?.itemSize?.value!! >= 15)
                    isLastItemReached = true
            }
        }
    }

    override fun onItemSelected(index: Int) {
        when (index){
            0 -> {
                TedBottomPicker.with(activity).setImageProvider { imageView, imageUri ->
                    val requestOptions = RequestOptions()
                    requestOptions.centerCrop()
                    requestOptions.priority(Priority.NORMAL)

                    GlideApp.with(this).load(imageUri).apply(requestOptions).into(imageView)
                }.show { uri -> transferFromUri(uri) }
            }
            1 -> MaterialDialog(context!!).show { fileChooser { _, file -> transferFromUri(file.toUri()) } }
        }
    }

    override fun onStop() {
        super.onStop()

        // Unregister broadcast receiver
        LocalBroadcastManager.getInstance(context!!).unregisterReceiver(receiver!!)
    }

    private fun transferFromUri(uri: Uri?){
        fileUri = uri
        downloadUri = null

        context?.startService(Intent(context, TransferService::class.java)
            .putExtra(TransferService.extraFileURI, uri)
            .setAction(TransferService.actionFile))
    }

    private fun onRefreshData(){
        viewModel?.refresh()

        if (swipeRefreshContainer.isRefreshing)
            swipeRefreshContainer.isRefreshing = false
    }

    private fun getPickerItems(): List<Picker> {
        return listOf(Picker(R.drawable.ic_add_media, R.string.sheet_picker_image),
            Picker(R.drawable.ic_add_file, R.string.sheet_picker_file))
    }
}