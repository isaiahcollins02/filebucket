package com.isaiahvonrundstedt.bucket.fragments.navigation

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
import android.widget.ProgressBar
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.files.fileChooser
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.activities.MainActivity
import com.isaiahvonrundstedt.bucket.adapters.filterable.CoreAdapter
import com.isaiahvonrundstedt.bucket.architecture.viewmodel.FileViewModel
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseFragment
import com.isaiahvonrundstedt.bucket.components.custom.ItemDecoration
import com.isaiahvonrundstedt.bucket.components.modules.GlideApp
import com.isaiahvonrundstedt.bucket.fragments.bottomsheet.PickerBottomSheet
import com.isaiahvonrundstedt.bucket.interfaces.BottomSheetPicker
import com.isaiahvonrundstedt.bucket.interfaces.ScreenAction
import com.isaiahvonrundstedt.bucket.objects.core.File
import com.isaiahvonrundstedt.bucket.objects.experience.PickerItem
import com.isaiahvonrundstedt.bucket.service.TransferService
import com.isaiahvonrundstedt.bucket.utils.Permissions
import com.kaopiz.kprogresshud.KProgressHUD
import gun0912.tedbottompicker.TedBottomPicker

class FilesFragment: BaseFragment(), ScreenAction.Search, BottomSheetPicker {

    private var fileUri: Uri? = null
    private var downloadUri: Uri? = null
    private var receiver: BroadcastReceiver? = null
    private var viewModel: FileViewModel? = null

    private val itemList: ArrayList<File> = ArrayList()

    private lateinit var rootView: View

    private lateinit var progressBar: ProgressBar
    private lateinit var swipeRefreshContainer: SwipeRefreshLayout
    private lateinit var defaultAction: FloatingActionButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var adapter: CoreAdapter
    private lateinit var itemPicker: PickerBottomSheet

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        receiver = object: BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    TransferService.actionCompleted, TransferService.transferError -> onUploadResultIntent(intent)
                }
            }
        }

        layoutManager = LinearLayoutManager(context)
        adapter =
            CoreAdapter(itemList, childFragmentManager, GlideApp.with(this))
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (activity is MainActivity)
            (activity as MainActivity).initializeSearch(this)

        viewModel = ViewModelProviders.of(this).get(FileViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_files, container, false)

        progressBar = rootView.findViewById(R.id.progressBar)
        swipeRefreshContainer = rootView.findViewById(R.id.swipeRefreshContainer)
        defaultAction = rootView.findViewById(R.id.addAction)

        var isScrolling = false

        recyclerView = rootView.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = layoutManager
        recyclerView.addItemDecoration(ItemDecoration(context, false))
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                    isScrolling = true
            }
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val firstVisibleItem = layoutManager.findFirstVisibleItemPosition()
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount

                if (isScrolling && (firstVisibleItem + visibleItemCount == totalItemCount)) {
                    isScrolling = false

                    viewModel?.onLoad()
                }
            }
        })
        recyclerView.adapter = adapter

        onLoadAssets()

        swipeRefreshContainer.setColorSchemeResources(
            R.color.colorIndicatorBlue,
            R.color.colorIndicatorGreen,
            R.color.colorIndicatorRed,
            R.color.colorIndicatorYellow
        )
        swipeRefreshContainer.setOnRefreshListener {
            viewModel?.refresh()
        }
        return rootView
    }

    override fun onStart() {
        super.onStart()

        val manager = LocalBroadcastManager.getInstance(context!!)
        manager.registerReceiver(receiver!!, TransferService.intentFilter)

        val items = arrayListOf(
            PickerItem(R.drawable.ic_vector_photo, R.string.bottom_sheet_picker_image),
            PickerItem(R.drawable.ic_vector_files, R.string.bottom_sheet_picker_file)
        )

        itemPicker = PickerBottomSheet()
        itemPicker.setItems(items)
        itemPicker.setOnItemSelectedListener(this)

        defaultAction.setOnClickListener {
            itemPicker.show(childFragmentManager, "pickerTag")
        }
    }

    override fun onItemSelected(index: Int) {
        when (index){
            0 -> invokeVideoPicker()
            1 -> invokeFilePicker()
        }
    }

    private fun invokeVideoPicker(){
        TedBottomPicker.with(activity)
            .show { uri ->
                transferFromUri(uri)
            }
    }

    private fun invokeFilePicker(){
        MaterialDialog(context!!).show {
            fileChooser { _, file ->
                transferFromUri(file.toUri())
            }
        }
    }

    override fun onStop() {
        super.onStop()

        // Unregister broadcast receiver
        LocalBroadcastManager.getInstance(context!!).unregisterReceiver(receiver!!)
    }

    private fun transferFromUri(uri: Uri?){
        val progress = KProgressHUD(rootView.context)
            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setAnimationSpeed(2)
            .setCancellable(false)
            .setDimAmount(.50f)
            .show()

        fileUri = uri
        downloadUri = null

        context?.startService(Intent(context, TransferService::class.java)
            .putExtra(TransferService.extraFileURI, uri)
            .setAction(TransferService.actionUpload))

        progress.dismiss()
    }

    private fun onLoadAssets(){
        viewModel?.itemList?.observe(this, Observer { itemList ->
            this.itemList.addAll(itemList)
            adapter.notifyDataSetChanged()

            recyclerView.isVisible = true
            progressBar.isVisible = false
            if (swipeRefreshContainer.isRefreshing)
                swipeRefreshContainer.isRefreshing = false
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode){
            Permissions.readRequestCode -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    invokeFilePicker()
            }
        }
    }
    private fun onUploadResultIntent(intent: Intent){
        // Get a new intent from Upload Service with a success or failure
        downloadUri = intent.getParcelableExtra(TransferService.extraDownloadURL)
        fileUri = intent.getParcelableExtra(TransferService.extraFileURI)

        // Show a feedback to the user when a task in the service has been completed
        if (downloadUri != null && fileUri != null)
            Snackbar.make(rootView, R.string.file_upload_success, Snackbar.LENGTH_SHORT).show()
        else
            Snackbar.make(rootView, R.string.file_upload_error, Snackbar.LENGTH_SHORT).show()
    }

    override fun onSearch(searchQuery: String?) {
        adapter.filter.filter(searchQuery)
    }
}