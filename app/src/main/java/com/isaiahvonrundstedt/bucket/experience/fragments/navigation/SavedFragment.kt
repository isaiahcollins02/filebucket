package com.isaiahvonrundstedt.bucket.experience.fragments.navigation

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.core.adapters.CoreAdapter
import com.isaiahvonrundstedt.bucket.core.components.abstracts.BaseFragment
import com.isaiahvonrundstedt.bucket.core.components.custom.ItemDecoration
import com.isaiahvonrundstedt.bucket.core.components.modules.GlideApp
import com.isaiahvonrundstedt.bucket.core.interfaces.ActionBarInvoker
import com.isaiahvonrundstedt.bucket.core.interfaces.TransferListener
import com.isaiahvonrundstedt.bucket.core.objects.File
import com.isaiahvonrundstedt.bucket.core.utils.Database
import com.isaiahvonrundstedt.bucket.experience.activities.MainActivity
import kotlinx.android.synthetic.main.fragment_nav_files.*

class SavedFragment: BaseFragment(), ActionBarInvoker, TransferListener {

    private var downloadID: Long? = null

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
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_nav_saved, container, false)
        setRootBackground(rootView)

        adapter = CoreAdapter(itemList, childFragmentManager, GlideApp.with(this),this)
        swipeRefreshContainer = rootView.findViewById(R.id.swipeRefreshContainer)

        recyclerView = rootView.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(container?.context)
        recyclerView.addItemDecoration(ItemDecoration(context))
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

    override fun onDownloadQueued(downloadID: Long) {
        this.downloadID = downloadID
    }

    override fun onSearch(query: String?) {
        adapter.filter.filter(query)
    }

    private fun onLoadAssets() {
        val sqliteDatabase: SQLiteDatabase = rootView.context.openOrCreateDatabase(Database.localCache, Context.MODE_PRIVATE, null)
        val cursor: Cursor = sqliteDatabase.rawQuery("SELECT * FROM collections", null)
        cursor.moveToFirst()

        while (!cursor.isAfterLast){
            val newFile: File = File().apply {
                id = cursor.getString(cursor.getColumnIndex(Database.COLUMN_FILE_ID))
                name = cursor.getString(cursor.getColumnIndex(Database.COLUMN_FILE_NAME))
                author = cursor.getString(cursor.getColumnIndex(Database.COLUMN_FILE_AUTHOR))
                fileType = cursor.getInt(cursor.getColumnIndex(Database.COLUMN_FILE_TYPE))
                fileSize = cursor.getDouble(cursor.getColumnIndex(Database.COLUMN_FILE_SIZE))
                downloadURL = cursor.getString(cursor.getColumnIndex(Database.COLUMN_FILE_DOWNLOAD_URL))
                timestamp = Database(context!!).convertLong(cursor.getInt(cursor.getColumnIndex(Database.COLUMN_FILE_TIMESTAMP)).toLong())
            }
            itemList.add(newFile)
            cursor.moveToNext()
        }
        adapter.notifyDataSetChanged()

        if (swipeRefreshContainer.isRefreshing)
            swipeRefreshContainer.isRefreshing = false

        cursor.close()
        sqliteDatabase.close()
    }
}