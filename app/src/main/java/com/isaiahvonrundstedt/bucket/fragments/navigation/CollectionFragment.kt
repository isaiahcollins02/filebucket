package com.isaiahvonrundstedt.bucket.fragments.navigation

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.activities.MainActivity
import com.isaiahvonrundstedt.bucket.adapters.filterable.CoreAdapter
import com.isaiahvonrundstedt.bucket.architecture.viewmodel.CollectionViewModel
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseFragment
import com.isaiahvonrundstedt.bucket.components.custom.ItemDecoration
import com.isaiahvonrundstedt.bucket.components.modules.GlideApp
import com.isaiahvonrundstedt.bucket.interfaces.ScreenAction
import com.isaiahvonrundstedt.bucket.interfaces.TransferListener
import com.isaiahvonrundstedt.bucket.objects.File

class CollectionFragment: BaseFragment(), ScreenAction.Search, TransferListener {

    private var downloadID: Long? = null
    private var viewModel: CollectionViewModel? = null

    private val itemList: ArrayList<File> = ArrayList()

    private lateinit var rootView: View
    private lateinit var adapter: CoreAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshContainer: SwipeRefreshLayout
    private lateinit var downloadReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        downloadReceiver = object: BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val receivedID: Long = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)!!
                if (receivedID == downloadID){
                    sendNotification(NOTIFICATION_TYPE_FINISHED, getString(R.string.notification_download_finished))
                } else
                    Log.e("DataFetchError", "Error Fetching File")
            }

        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (activity is MainActivity)
            (activity as MainActivity).setSearchListener(this)

        viewModel = ViewModelProviders.of(this).get(CollectionViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_collection, container, false)

        adapter = CoreAdapter(itemList, childFragmentManager, GlideApp.with(this), this)
        swipeRefreshContainer = rootView.findViewById(R.id.swipeRefreshContainer)

        recyclerView = rootView.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(container?.context)
        recyclerView.adapter = adapter

        swipeRefreshContainer.setColorSchemeResources(
            R.color.colorIndicatorBlue,
            R.color.colorIndicatorGreen,
            R.color.colorIndicatorRed,
            R.color.colorIndicatorYellow
        )
        swipeRefreshContainer.setOnRefreshListener {
            adapter.removeAllData()
            onLoadAssets()
        }

        return rootView
    }

    override fun onStart() {
        super.onStart()
        onLoadAssets()
        context?.registerReceiver(downloadReceiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }

    override fun onPause() {
        super.onPause()
        context?.unregisterReceiver(downloadReceiver)
    }

    override fun onDownloadQueued(downloadID: Long) {
        this.downloadID = downloadID
    }

    override fun onSearch(searchQuery: String?) {
        adapter.filter.filter(searchQuery)
    }

    private fun onLoadAssets() {

        viewModel?.items?.observe(this, Observer<List<File>> { t ->
            itemList.addAll(t)
            adapter.notifyDataSetChanged()
        })

        if (swipeRefreshContainer.isRefreshing)
            swipeRefreshContainer.isRefreshing = false

    }

}