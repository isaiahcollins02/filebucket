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
import com.isaiahvonrundstedt.bucket.adapters.core.SentAdapter
import com.isaiahvonrundstedt.bucket.architecture.factory.FileFactory
import com.isaiahvonrundstedt.bucket.architecture.viewmodel.recycler.network.FileViewModel
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseFragment
import com.isaiahvonrundstedt.bucket.components.custom.ItemDecoration
import com.isaiahvonrundstedt.bucket.components.modules.GlideApp
import com.isaiahvonrundstedt.bucket.utils.User
import kotlinx.android.synthetic.main.fragment_box_child.*

class UserFragment: BaseFragment() {

    private var currentAuthor: String? = null
    private var factory: FileFactory? = null
    private var viewModel: FileViewModel? = null

    private var adapter: SentAdapter? = null
    private var layoutManager: LinearLayoutManager? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_box_child, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        currentAuthor = User(context).fullName

        factory = FileFactory(currentAuthor)
        viewModel = ViewModelProviders.of(this, factory).get(FileViewModel::class.java)
    }

    override fun onStart() {
        super.onStart()

        layoutManager = LinearLayoutManager(context)
        adapter = SentAdapter(context, childFragmentManager, GlideApp.with(this))

        recyclerView.addItemDecoration(ItemDecoration(context))
        recyclerView.addOnScrollListener(onScrollListener)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        swipeRefreshContainer.setOnRefreshListener { onRefresh() }
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

                if (viewModel?.size!! >= 15)
                    isLastItemReached = true
            }
        }
    }

    private fun onRefresh(){
        viewModel?.refresh()

        if (swipeRefreshContainer.isRefreshing)
            swipeRefreshContainer.isRefreshing = false
    }

    override fun onResume() {
        super.onResume()

        viewModel?.itemList?.observe(this, Observer { items ->
            adapter?.setObservableItems(items)
        })
    }

}