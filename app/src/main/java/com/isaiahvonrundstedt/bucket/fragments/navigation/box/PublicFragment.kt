package com.isaiahvonrundstedt.bucket.fragments.navigation.box

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.adapters.filterable.BoxesAdapter
import com.isaiahvonrundstedt.bucket.architecture.viewmodel.core.BoxesViewModel
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseFragment
import com.isaiahvonrundstedt.bucket.components.custom.ItemDecoration
import kotlinx.android.synthetic.main.fragment_box_child.*

class PublicFragment: BaseFragment() {

    private var adapter: BoxesAdapter? = null
    private var layoutManager: LinearLayoutManager? = null

    private var viewModel: BoxesViewModel? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_box_child, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        viewModel = ViewModelProviders.of(this).get(BoxesViewModel::class.java)
    }

    override fun onStart() {
        super.onStart()

        layoutManager = LinearLayoutManager(context)
        adapter = BoxesAdapter()

        recyclerView.layoutManager = layoutManager
        recyclerView.addItemDecoration(ItemDecoration(context))
        recyclerView.addOnScrollListener(onScrollListener)
        recyclerView.adapter = adapter

        swipeRefreshContainer.setOnRefreshListener { onRefresh() }
    }
    private fun onRefresh(){
        viewModel?.refresh()

        if (swipeRefreshContainer.isRefreshing)
            swipeRefreshContainer.isRefreshing = false
    }

    private var isScrolling: Boolean = false
    private var isLastItemReached: Boolean = false
    private var onScrollListener = object : RecyclerView.OnScrollListener() {
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

    override fun onResume() {
        super.onResume()

        viewModel?.itemList?.observe(this, Observer { items ->
            adapter?.setObservableItems(items)
        })
    }
}