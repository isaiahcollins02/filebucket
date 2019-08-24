package com.isaiahvonrundstedt.bucket.fragments.screendialog

import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AbsListView
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.adapters.support.SearchAdapter
import com.isaiahvonrundstedt.bucket.architecture.viewmodel.SearchViewModel
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseScreenDialog
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseViewModel
import com.isaiahvonrundstedt.bucket.components.modules.GlideApp
import com.isaiahvonrundstedt.bucket.receivers.NetworkReceiver
import kotlinx.android.synthetic.main.layout_banner_network.*
import kotlinx.android.synthetic.main.layout_dialog_search.*
import kotlinx.android.synthetic.main.layout_empty_no_result.*
import timber.log.Timber

class SearchFragment: BaseScreenDialog(), SearchView.OnQueryTextListener {

    private var searchQuery: String? = null
    private var layoutManager: LinearLayoutManager? = null

    private var searchAdapter: SearchAdapter? = null
    private var viewModel: SearchViewModel? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_dialog_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbarTitle?.text = getString(R.string.menu_search)
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        viewModel = ViewModelProviders.of(this).get(SearchViewModel::class.java)
    }

    override fun onStart() {
        super.onStart()

        searchAdapter = SearchAdapter(context!!, childFragmentManager, GlideApp.with(this))
        layoutManager = LinearLayoutManager(context)

        recyclerView.layoutManager = layoutManager
        recyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        recyclerView.addOnScrollListener(onScrollListener)
        recyclerView.adapter = searchAdapter

        searchView.setOnQueryTextListener(this)
        searchView.findViewById<View?>(androidx.appcompat.R.id.search_plate)?.setBackgroundColor(Color.TRANSPARENT)

        viewModel?.itemList?.observe(this, Observer { items ->
            searchAdapter?.setObservableItems(items)
        })

        viewModel?.itemSize?.observe(this, Observer { size ->
            noResultView.isVisible = size == 0
            noResultViewTitle.text = String.format(getString(R.string.main_empty_no_result_title), searchView.query)
        })

        viewModel?.dataState?.observe(this, Observer { dataState ->
            if (dataState == BaseViewModel.stateDataPreparing){
                noResultView.isVisible = false
                progressBar.isVisible = true
            } else if (dataState == BaseViewModel.stateDataReady)
                progressBar.isVisible = false
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
                viewModel?.fetchFiltered(searchQuery)

                if (viewModel?.itemSize?.value!! >= 15)
                    isLastItemReached = true
            }
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        searchQuery = newText
        viewModel?.filter(newText)
        return true
    }

}