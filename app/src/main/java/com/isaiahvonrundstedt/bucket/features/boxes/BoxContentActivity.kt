package com.isaiahvonrundstedt.bucket.features.boxes

import android.os.Bundle
import android.view.MenuItem
import android.widget.AbsListView
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.features.core.CoreAdapter
import com.isaiahvonrundstedt.bucket.features.core.file.FileFactory
import com.isaiahvonrundstedt.bucket.features.core.file.FileViewModel
import com.isaiahvonrundstedt.bucket.features.shared.abstracts.BaseAppBarActivity
import com.isaiahvonrundstedt.bucket.features.shared.abstracts.BaseViewModel
import com.isaiahvonrundstedt.bucket.features.shared.custom.GlideApp
import com.isaiahvonrundstedt.bucket.constants.Params
import kotlinx.android.synthetic.main.activity_vault.*
import kotlinx.android.synthetic.main.layout_empty_no_items.*

class BoxContentActivity: BaseAppBarActivity() {

    private var author: String? = null
    private var authorID: String? = null

    private var adapter: CoreAdapter? = null
    private var layoutManager: LinearLayoutManager? = null

    private var viewModel: FileViewModel? = null
    private var factory: FileFactory? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vault)

        author = intent.getStringExtra(Params.author)
        authorID = intent.getStringExtra(Params.authorID)
        setToolbarTitle(String.format(resources.getString(R.string.user_repository_title), author?.substring(0, author?.indexOf(' ') ?: author?.length!!)))

        factory = FileFactory(author)
        viewModel = ViewModelProviders.of(this, factory).get(FileViewModel::class.java)
    }

    override fun onStart() {
        super.onStart()

        layoutManager = LinearLayoutManager(this)
        adapter = CoreAdapter(this, supportFragmentManager, GlideApp.with(this))

        swipeRefreshContainer.setColorSchemeResources(R.color.colorPrimary)
        swipeRefreshContainer.setOnRefreshListener { viewModel?.refresh(); swipeRefreshContainer.isRefreshing = false }

        recyclerView.layoutManager = layoutManager
        recyclerView.addOnScrollListener(onScrollListener)
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        recyclerView.adapter = adapter
    }

    override fun onResume(){
        super.onResume()

        viewModel?.itemList?.observe(this, Observer { itemList ->
            adapter?.setObservableItems(itemList)
        })

        viewModel?.itemSize?.observe(this, Observer { size ->
            noItemView.isVisible = size == 0
        })

        viewModel?.dataState?.observe(this, Observer { dataState ->
            if (dataState == BaseViewModel.stateDataPreparing){
                noItemView.isVisible = false
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
                viewModel?.fetch()

                if (viewModel?.itemSize?.value!! >= 15)
                    isLastItemReached = true
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

}