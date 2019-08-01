package com.isaiahvonrundstedt.bucket.activities.support

import android.os.Bundle
import android.view.MenuItem
import android.widget.AbsListView
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.adapters.core.PublicAdapter
import com.isaiahvonrundstedt.bucket.architecture.factory.FileFactory
import com.isaiahvonrundstedt.bucket.architecture.viewmodel.network.FileViewModel
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseAppBarActivity
import com.isaiahvonrundstedt.bucket.components.custom.ItemDecoration
import com.isaiahvonrundstedt.bucket.components.modules.GlideApp
import com.isaiahvonrundstedt.bucket.constants.Params
import kotlinx.android.synthetic.main.activity_vault.*
import kotlinx.android.synthetic.main.layout_empty_no_items.*

class VaultActivity: BaseAppBarActivity()  {

    private var author: String? = null

    private var adapter: PublicAdapter? = null
    private var layoutManager: LinearLayoutManager? = null

    private var viewModel: FileViewModel? = null
    private var factory: FileFactory? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vault)

        author = intent.getStringExtra(Params.author)
        setToolbarTitle(String.format(resources.getString(R.string.file_user_repository), author))

        factory = FileFactory(author)
        viewModel = ViewModelProviders.of(this, factory).get(FileViewModel::class.java)
    }

    override fun onStart() {
        super.onStart()

        layoutManager = LinearLayoutManager(this)
        adapter = PublicAdapter(this, supportFragmentManager, GlideApp.with(this))

        recyclerView.layoutManager = layoutManager
        recyclerView.addOnScrollListener(onScrollListener)
        recyclerView.addItemDecoration(ItemDecoration(this))
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