package com.isaiahvonrundstedt.bucket.fragments.profile

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.adapters.filterable.VaultAdapter
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseFragment
import com.isaiahvonrundstedt.bucket.constants.Firebase
import com.isaiahvonrundstedt.bucket.constants.Parameters
import com.isaiahvonrundstedt.bucket.interfaces.TransferListener
import com.isaiahvonrundstedt.bucket.objects.File
import com.isaiahvonrundstedt.bucket.utils.Client

class RepoFragment: BaseFragment(), TransferListener{

    private var file: java.io.File? = null
    private var downloadURL: String? = null
    private var downloadID: Long? = 0
    private var fullName: String? = null
    private var query: Query? = null

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val arrayList: ArrayList<File> = ArrayList()

    private lateinit var rootView: View
    private lateinit var swipeRefreshContainer: SwipeRefreshLayout
    private lateinit var adapter: VaultAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var downloadReceiver: BroadcastReceiver
    private lateinit var downloadManager: DownloadManager
    private lateinit var request: DownloadManager.Request

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fullName = Client(context!!).fullName
        downloadManager = context?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        downloadReceiver = object: BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (id == downloadID)
                    sendNotification(NOTIFICATION_TYPE_FINISHED, getString(R.string.notification_download_finished))
            }
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_sent, container, false)

        adapter = VaultAdapter(arrayList, this)

        swipeRefreshContainer = rootView.findViewById(R.id.swipeRefreshContainer)
        swipeRefreshContainer.setColorSchemeResources(
            R.color.colorIndicatorBlue,
            R.color.colorIndicatorRed,
            R.color.colorIndicatorGreen,
            R.color.colorIndicatorYellow
        )
        swipeRefreshContainer.setOnRefreshListener {
            adapter.removeAllData()
            onPopulate()
        }

        recyclerView = rootView.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(container?.context)
        recyclerView.addItemDecoration(DividerItemDecoration(container?.context, DividerItemDecoration.VERTICAL))
        recyclerView.adapter = adapter

        return rootView
    }

    override fun onStart() {
        super.onStart()

        onPopulate()
    }

    private fun onPopulate(){
        if (arrayList.size > 0)
            arrayList.clear()
        else {
            query = firestore.collection(Firebase.FILES.string)
            query!!.whereEqualTo(Parameters.AUTHOR.string, fullName)
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful){
                        for (queryDocumentSnapshot: QueryDocumentSnapshot in it.result!!){
                            val file: File = queryDocumentSnapshot.toObject(File::class.java)
                            file.fileID = queryDocumentSnapshot.id
                            arrayList.add(file)
                        }
                        if (swipeRefreshContainer.isRefreshing)
                            swipeRefreshContainer.isRefreshing = false
                        adapter.notifyDataSetChanged()
                    } else
                        Snackbar.make(rootView, R.string.status_error_occurred, Snackbar.LENGTH_SHORT).show()
                }
        }
    }

    override fun onDetach() {
        super.onDetach()
        activity?.finish()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode){
            4 -> invokeDownload(file, downloadURL)
        }
    }

    private fun invokeDownload(bufferedFile: java.io.File?, downloadURL: String?){
        request = DownloadManager.Request(Uri.parse(downloadURL))
            .setTitle(getString(R.string.notification_downloading_file))
            .setDestinationUri(bufferedFile?.toUri())

        downloadID = downloadManager.enqueue(request)
    }

    override fun onDownloadQueued(downloadID: Long) {
        this.downloadID = downloadID
    }

}