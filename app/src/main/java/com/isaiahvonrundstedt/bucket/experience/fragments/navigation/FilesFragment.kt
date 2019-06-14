package com.isaiahvonrundstedt.bucket.experience.fragments.navigation

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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.files.fileChooser
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.core.adapters.CoreAdapter
import com.isaiahvonrundstedt.bucket.core.components.abstracts.BaseFragment
import com.isaiahvonrundstedt.bucket.core.components.custom.ItemDecoration
import com.isaiahvonrundstedt.bucket.core.components.modules.GlideApp
import com.isaiahvonrundstedt.bucket.core.constants.Firebase
import com.isaiahvonrundstedt.bucket.core.interfaces.ActionBarInvoker
import com.isaiahvonrundstedt.bucket.core.interfaces.TransferListener
import com.isaiahvonrundstedt.bucket.core.objects.File
import com.isaiahvonrundstedt.bucket.core.service.TransferService
import com.isaiahvonrundstedt.bucket.core.utils.Permissions
import com.isaiahvonrundstedt.bucket.core.utils.Preferences
import com.isaiahvonrundstedt.bucket.core.utils.managers.ItemManager
import com.isaiahvonrundstedt.bucket.experience.activities.MainActivity
import com.kaopiz.kprogresshud.KProgressHUD
import com.leinardi.android.speeddial.SpeedDialView
import gun0912.tedbottompicker.TedBottomPicker

class FilesFragment: BaseFragment(), ActionBarInvoker, TransferListener {

    private var fileUri: Uri? = null
    private var downloadUri: Uri? = null
    private var transferListener: TransferListener? = null

    private val itemList: ArrayList<File> = ArrayList()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private lateinit var rootView: View
    private lateinit var progressBar: ProgressBar
    private lateinit var filterSpinner: AppCompatSpinner
    private lateinit var swipeRefreshContainer: SwipeRefreshLayout
    private lateinit var defaultAction: SpeedDialView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CoreAdapter
    private lateinit var uploadReceiver: BroadcastReceiver

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

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_nav_files, container, false)
        setRootBackground(rootView)

        progressBar = rootView.findViewById(R.id.progressBar)
        defaultAction = rootView.findViewById(R.id.addAction)
        filterSpinner = rootView.findViewById(R.id.filterSpinner)
        swipeRefreshContainer = rootView.findViewById(R.id.swipeRefreshContainer)
        defaultAction.inflate(R.menu.action_main)

        adapter = CoreAdapter(itemList, childFragmentManager, GlideApp.with(this),this)

        recyclerView = rootView.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.addItemDecoration(ItemDecoration(context))
        recyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, directionX: Int, directionY: Int) {
                super.onScrolled(recyclerView, directionX, directionY)
                if (directionY > 0){
                    // Scroll down event
                    filterSpinner.visibility = View.GONE
                } else if (directionY < 0){
                    // Scroll up event
                    filterSpinner.visibility = View.VISIBLE
                }
            }
        })
        recyclerView.adapter = adapter

        swipeRefreshContainer.setColorSchemeResources(
            R.color.colorIndicatorBlue,
            R.color.colorIndicatorGreen,
            R.color.colorIndicatorRed,
            R.color.colorIndicatorYellow
        )
        swipeRefreshContainer.setOnRefreshListener {
            adapter.removeAllData()
            onLoadAssets(ItemManager.CATEGORY_ALL)
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

        val filterAdapter = ArrayAdapter.createFromResource(context!!, R.array.filter_items,
            android.R.layout.simple_spinner_dropdown_item)
        filterSpinner.adapter = filterAdapter
        filterSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                onLoadAssets(ItemManager.CATEGORY_ALL)
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val item: Int = when (position){
                    0 -> ItemManager.CATEGORY_ALL
                    1 -> ItemManager.CATEGORY_DOCUMENTS
                    2 -> ItemManager.CATEGORY_CODES
                    3 -> ItemManager.CATEGORY_MEDIA
                    else -> -1
                }
                onLoadAssets(item)
            }
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

    private fun onLoadAssets(fileCategory: Int){
        val fileReference: CollectionReference? = firestore.collection(Firebase.FILES.string)
        fileReference?.get()?.addOnCompleteListener { task ->
            if (task.isSuccessful){
                itemList.clear()
                for (documentSnapshot: QueryDocumentSnapshot in task.result!!){
                    val file: File = documentSnapshot.toObject(File::class.java)
                    file.id = documentSnapshot.id
                    if (fileCategory == ItemManager.CATEGORY_ALL)
                        itemList.add(file)
                    else if (fileCategory == ItemManager.CATEGORY_DOCUMENTS){
                        if (fileCategory == ItemManager.getFileCategory(file.fileType))
                            itemList.add(file)
                    } else if (fileCategory == ItemManager.CATEGORY_CODES){
                        if (fileCategory == ItemManager.getFileCategory(file.fileType))
                            itemList.add(file)
                    } else if (fileCategory == ItemManager.CATEGORY_MEDIA){
                        if (fileCategory == ItemManager.getFileCategory(file.fileType))
                            itemList.add(file)
                    }
                }
                itemList.sort()

                var hasPendingPackage = false
                var targetIndex = 0

                itemList.forEachIndexed { index, file ->
                    if (file.fileType == File.TYPE_PACKAGE){
                        hasPendingPackage = true
                        targetIndex = index
                    }
                }

                if (hasPendingPackage){
                    val swapFile: File = itemList[0]
                    val targetFile: File = itemList[targetIndex]
                    itemList[0] = targetFile
                    itemList[targetIndex] = swapFile
                }

                adapter.notifyDataSetChanged()

                recyclerView.visibility = View.VISIBLE
                progressBar.visibility = View.INVISIBLE
                if (swipeRefreshContainer.isRefreshing)
                    swipeRefreshContainer.isRefreshing = false
            } else
                Snackbar.make(rootView, R.string.status_unknown, Snackbar.LENGTH_SHORT).show()
        }
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
            onLoadAssets(ItemManager.CATEGORY_ALL)

            Snackbar.make(rootView, R.string.file_upload_success, Snackbar.LENGTH_SHORT).show()
        } else
            Snackbar.make(rootView, R.string.file_upload_error, Snackbar.LENGTH_SHORT).show()
    }

    override fun onSearch(query: String?) {
        adapter.filter.filter(query)
    }
}