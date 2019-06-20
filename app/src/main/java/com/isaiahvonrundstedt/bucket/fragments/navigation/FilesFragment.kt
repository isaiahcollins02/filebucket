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
import android.widget.ProgressBar
import androidx.core.app.ActivityCompat
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
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.activities.MainActivity
import com.isaiahvonrundstedt.bucket.adapters.filterable.CoreAdapter
import com.isaiahvonrundstedt.bucket.architecture.viewmodel.FileViewModel
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseFragment
import com.isaiahvonrundstedt.bucket.components.custom.ItemDecoration
import com.isaiahvonrundstedt.bucket.components.modules.GlideApp
import com.isaiahvonrundstedt.bucket.interfaces.ScreenAction
import com.isaiahvonrundstedt.bucket.interfaces.TransferListener
import com.isaiahvonrundstedt.bucket.objects.File
import com.isaiahvonrundstedt.bucket.service.TransferService
import com.isaiahvonrundstedt.bucket.utils.Permissions
import com.kaopiz.kprogresshud.KProgressHUD
import com.leinardi.android.speeddial.SpeedDialView
import gun0912.tedbottompicker.TedBottomPicker

class FilesFragment: BaseFragment(), ScreenAction.Search, TransferListener {

    private var fileUri: Uri? = null
    private var downloadUri: Uri? = null
    private var transferListener: TransferListener? = null

    private val itemList: ArrayList<File> = ArrayList()

    private lateinit var rootView: View

    private lateinit var progressBar: ProgressBar
    private lateinit var swipeRefreshContainer: SwipeRefreshLayout
    private lateinit var defaultAction: SpeedDialView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CoreAdapter
    private lateinit var uploadReceiver: BroadcastReceiver
    private lateinit var viewModel: FileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        uploadReceiver = object: BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    TransferService.UPLOAD_COMPLETED, TransferService.UPLOAD_ERROR -> onUploadResultIntent(intent)
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (activity is MainActivity)
            (activity as MainActivity).setSearchListener(this)

        viewModel = ViewModelProviders.of(this).get(FileViewModel::class.java)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_files, container, false)

        progressBar = rootView.findViewById(R.id.progressBar)
        defaultAction = rootView.findViewById(R.id.addAction)
        swipeRefreshContainer = rootView.findViewById(R.id.swipeRefreshContainer)
        defaultAction.inflate(R.menu.action_main)

        adapter = CoreAdapter(itemList, childFragmentManager, GlideApp.with(this), this)
        onLoadAssets()

        recyclerView = rootView.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        swipeRefreshContainer.setColorSchemeResources(
            R.color.colorIndicatorBlue,
            R.color.colorIndicatorGreen,
            R.color.colorIndicatorRed,
            R.color.colorIndicatorYellow
        )
        swipeRefreshContainer.setOnRefreshListener {
            viewModel.refresh()
        }
        return rootView
    }

    override fun onDownloadQueued(downloadID: Long) {
        if (activity is MainActivity)
            transferListener?.onDownloadQueued(downloadID)
    }

    override fun onStart() {
        super.onStart()

        val manager = LocalBroadcastManager.getInstance(context!!)

        manager.registerReceiver(uploadReceiver, TransferService.intentFilter)

        defaultAction.setOnActionSelectedListener {  actionItem ->
            when (actionItem.id){
                R.id.fileAction ->
                    invokeFilePicker()
                R.id.mediaAction ->
                    invokePhotoPicker()
            }
            true
        }
    }

    override fun onStop() {
        super.onStop()

        // Unregister broadcast receiver
        LocalBroadcastManager.getInstance(context!!).unregisterReceiver(uploadReceiver)
    }

    private fun invokePhotoPicker(){
        if (defaultAction.isOpen)
            defaultAction.close()

        TedBottomPicker.with(activity)
            .setImageProvider { imageView, imageUri ->
                val requestOptions = RequestOptions()
                    .centerCrop()
                    .priority(Priority.NORMAL)

                GlideApp.with(this)
                    .load(imageUri.path)
                    .apply(requestOptions)
                    .into(imageView)
            }
            .show {
                uploadFromUri(it)
            }
    }

    private fun invokeFilePicker(){
        if (defaultAction.isOpen)
            defaultAction.close()

        if (Permissions(rootView.context).readAccessGranted) {
            MaterialDialog(context!!).show {
                fileChooser { _, file ->
                    uploadFromUri(file.toUri())
                }
            }
        } else
            ActivityCompat.requestPermissions(activity!!,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), Permissions.READ_REQUEST)
    }

    private fun uploadFromUri(uri: Uri){
        val progress: KProgressHUD = KProgressHUD(context)
            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setAnimationSpeed(2)
            .setCancellable(false)
            .setDimAmount(.50f)
            .show()

        fileUri = uri
        downloadUri = null

        context?.startService(Intent(context, TransferService::class.java)
            .putExtra(TransferService.EXTRA_FILE_URI, uri)
            .putExtra(TransferService.UPLOAD_TYPE, TransferService.TYPE_FILE )
            .setAction(TransferService.ACTION_UPLOAD))

        progress.dismiss()
    }

    private fun onLoadAssets(){
        viewModel.itemList.observe(this, Observer { itemList ->
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
            Permissions.READ_REQUEST -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    invokeFilePicker()
            }
        }
    }
    private fun onUploadResultIntent(intent: Intent){
        // Get a new intent from Upload Service with a success or failure
        downloadUri = intent.getParcelableExtra(TransferService.EXTRA_DOWNLOAD_URL)
        fileUri = intent.getParcelableExtra(TransferService.EXTRA_FILE_URI)

        // Show a feedback to the user when a task in the service has been completed
        if (downloadUri != null && fileUri != null){
            adapter.removeAllData()
            onLoadAssets()

            Snackbar.make(rootView, R.string.file_upload_success, Snackbar.LENGTH_SHORT).show()
        } else
            Snackbar.make(rootView, R.string.file_upload_error, Snackbar.LENGTH_SHORT).show()
    }

    override fun onSearch(searchQuery: String?) {
        adapter.filter.filter(searchQuery)
    }
}