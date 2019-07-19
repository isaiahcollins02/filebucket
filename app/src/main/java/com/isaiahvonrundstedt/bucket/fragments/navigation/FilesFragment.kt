package com.isaiahvonrundstedt.bucket.fragments.navigation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.files.fileChooser
import com.google.android.material.snackbar.Snackbar
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.adapters.filterable.CoreAdapter
import com.isaiahvonrundstedt.bucket.architecture.viewmodel.core.CoreViewModel
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseFragment
import com.isaiahvonrundstedt.bucket.components.custom.ItemDecoration
import com.isaiahvonrundstedt.bucket.components.modules.GlideApp
import com.isaiahvonrundstedt.bucket.fragments.bottomsheet.PickerBottomSheet
import com.isaiahvonrundstedt.bucket.interfaces.BottomSheetPicker
import com.isaiahvonrundstedt.bucket.objects.experience.Picker
import com.isaiahvonrundstedt.bucket.service.TransferService
import gun0912.tedbottompicker.TedBottomPicker
import kotlinx.android.synthetic.main.fragment_files.*

class FilesFragment: BaseFragment(), BottomSheetPicker {

    private var fileUri: Uri? = null
    private var downloadUri: Uri? = null

    private var receiver: BroadcastReceiver? = null
    private var adapter: CoreAdapter? = null
    private var layoutManager: LinearLayoutManager? = null
    private var viewModel: CoreViewModel? = null

    private lateinit var itemPicker: PickerBottomSheet

    override fun onAttach(context: Context) {
        super.onAttach(context)

        receiver = object: BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                // Get a new intent from Upload Service with a success or failure
                downloadUri = intent?.getParcelableExtra(TransferService.extraDownloadURL)
                fileUri = intent?.getParcelableExtra(TransferService.extraFileURI)

                if (intent?.action == TransferService.statusError || downloadUri == null && fileUri == null)
                    Snackbar.make(view!!, R.string.notification_file_upload_error, Snackbar.LENGTH_SHORT).show()
                else if (intent?.action == TransferService.statusCompleted)
                    Snackbar.make(view!!, R.string.notification_file_upload_success, Snackbar.LENGTH_SHORT).show()
            }
        }

        viewModel = ViewModelProviders.of(this).get(CoreViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_files, container, false)
    }

    override fun onStart() {
        super.onStart()

        val manager = LocalBroadcastManager.getInstance(context!!)
        manager.registerReceiver(receiver!!, TransferService.intentFilter)

        layoutManager = LinearLayoutManager(context)
        adapter = CoreAdapter(context, childFragmentManager, GlideApp.with(this))

        recyclerView.layoutManager = layoutManager
        recyclerView.addOnScrollListener(onScrollListener)
        recyclerView.addItemDecoration(ItemDecoration(context))
        recyclerView.adapter = adapter

        itemPicker = PickerBottomSheet()
        itemPicker.setItems(listOf(Picker(R.drawable.ic_vector_photo, R.string.sheet_picker_image), Picker(R.drawable.ic_vector_files, R.string.sheet_picker_file)))
        itemPicker.setOnItemSelectedListener(this)

        swipeRefreshContainer.setOnRefreshListener { onRefreshData() }
        addAction.setOnClickListener { itemPicker.show(childFragmentManager, "pickerTag") }

        viewModel?.itemList?.observe(this, Observer { items ->
            adapter?.setObservableItems(items)
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

                if (viewModel?.size()!! >= 15)
                    isLastItemReached = true
            }
        }
    }

    override fun onItemSelected(index: Int) {
        when (index){
            0 -> TedBottomPicker.with(activity).show { uri -> transferFromUri(uri) }
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
            .setAction(TransferService.actionUpload))
    }

    private fun onRefreshData(){
        viewModel?.refresh()

        if (swipeRefreshContainer.isRefreshing)
            swipeRefreshContainer.isRefreshing = false
    }
}