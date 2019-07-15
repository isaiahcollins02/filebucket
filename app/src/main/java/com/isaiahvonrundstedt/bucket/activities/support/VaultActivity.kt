package com.isaiahvonrundstedt.bucket.activities.support

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.adapters.filterable.VaultAdapter
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseAppBarActivity
import com.isaiahvonrundstedt.bucket.components.custom.ItemDecoration
import com.isaiahvonrundstedt.bucket.constants.Firestore
import com.isaiahvonrundstedt.bucket.constants.Params
import com.isaiahvonrundstedt.bucket.objects.core.File
import com.isaiahvonrundstedt.bucket.utils.managers.DataManager
import timber.log.Timber

class VaultActivity: BaseAppBarActivity(), SearchView.OnQueryTextListener {

    private var file: java.io.File? = null
    private var downloadURL: String? = null
    private var author: String? = null
    private var downloadID: Long? = 0

    private val itemList: ArrayList<File> = ArrayList()
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private lateinit var searchView: SearchView
    private lateinit var swipeRefreshContainer: SwipeRefreshLayout
    private lateinit var adapter: VaultAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var downloadManager: DownloadManager
    private lateinit var request: DownloadManager.Request

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vault)

        val intent = intent
        author = intent.getStringExtra(Params.author)

        setToolbarTitle(String.format(resources.getString(R.string.file_user_repository), DataManager.sliceFullName(author!!)))

        downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        adapter = VaultAdapter(itemList, supportFragmentManager)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(ItemDecoration(this))
        recyclerView.adapter = adapter

        swipeRefreshContainer = findViewById(R.id.swipeRefreshContainer)
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

    }

    override fun onStart() {
        super.onStart()

        onPopulate()
    }

    private fun onPopulate(){
        if (itemList.size > 0)
            itemList.clear()
        else {
            val query: Query = firestore.collection(Firestore.files)
            query.whereEqualTo(Params.author, author)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        for (queryDocumentSnapshot: QueryDocumentSnapshot in task.result!!){
                            val file: File = queryDocumentSnapshot.toObject(File::class.java)
                            file.fileID = queryDocumentSnapshot.id
                            itemList.add(file)
                        }
                        if (swipeRefreshContainer.isRefreshing)
                            swipeRefreshContainer.isRefreshing = false
                        Timber.i("Success Fetching Client from server")
                        adapter.notifyDataSetChanged()
                    } else
                        Timber.e("Error Occurred when connecting to server")
                }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId){
            android.R.id.home -> super.onBackPressed()
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onBackPressed() {
        if (searchView.isIconified)
            searchView.isIconified = true
        else
            super.onBackPressed()
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null)
            adapter.filter.filter(newText)
        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
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

}