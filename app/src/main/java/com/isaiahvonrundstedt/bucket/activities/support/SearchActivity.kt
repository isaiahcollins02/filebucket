package com.isaiahvonrundstedt.bucket.activities.support

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.adapters.CoreAdapter
import com.isaiahvonrundstedt.bucket.architecture.viewmodel.FileViewModel
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseActivity
import com.isaiahvonrundstedt.bucket.components.modules.GlideApp
import com.isaiahvonrundstedt.bucket.interfaces.TransferListener
import com.isaiahvonrundstedt.bucket.objects.File
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity: BaseActivity(), TransferListener {

    private var searchView: SearchView? = null
    private var viewModel: FileViewModel? = null

    private val itemList: ArrayList<File> = ArrayList()
    private val adapter: CoreAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setPersistentAppBar()

        viewModel = ViewModelProviders.of(this).get(FileViewModel::class.java)

        CoreAdapter(itemList, supportFragmentManager, GlideApp.with(this), this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    override fun onDownloadQueued(downloadID: Long) {

    }

    private fun setPersistentAppBar(){
        val rootView: ViewGroup? = findViewById(R.id.action_bar_root)
        if (rootView != null) {

            val view: View = LayoutInflater.from(this).inflate(R.layout.layout_appbar_search, rootView, false)
            rootView.addView(view, 0)

            val toolbar: Toolbar = rootView.findViewById(R.id.toolbar)
            setSupportActionBar(toolbar)

            supportActionBar?.title = null
            searchView = toolbar.findViewById(R.id.searchView)
            val titleView: TextView = toolbar.findViewById(R.id.toolbarTitle)
            titleView.text = getString(R.string.menu_search)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel?.itemList?.observe(this, Observer {
            itemList.addAll(it)
            adapter?.notifyDataSetChanged()
        })
    }
}