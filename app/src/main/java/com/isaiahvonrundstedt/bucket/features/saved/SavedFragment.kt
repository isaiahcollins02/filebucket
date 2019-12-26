package com.isaiahvonrundstedt.bucket.features.saved

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.features.core.CoreAdapter
import com.isaiahvonrundstedt.bucket.features.shared.abstracts.BaseFragment
import com.isaiahvonrundstedt.bucket.features.shared.custom.GlideApp
import kotlinx.android.synthetic.main.fragment_main_saved.*
import kotlinx.android.synthetic.main.layout_empty_no_items.*

class SavedFragment: BaseFragment() {

    private var viewModel: SavedViewModel? = null
    private var adapter: CoreAdapter? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        viewModel = ViewModelProviders.of(this).get(SavedViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main_saved, container, false)
    }

    override fun onStart() {
        super.onStart()

        adapter = CoreAdapter(context!!, childFragmentManager, GlideApp.with(this))

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        recyclerView.adapter = adapter

        swipeRefreshContainer.setOnRefreshListener { viewModel?.refresh(); swipeRefreshContainer.isRefreshing = false }
    }

    override fun onResume() {
        super.onResume()

        viewModel?.itemList?.observe(this, Observer { items ->
            adapter?.setObservableItems(items)
        })

        viewModel?.itemSize?.observe(this, Observer { size ->
            noItemView.isVisible = size == 0
        })
    }
}