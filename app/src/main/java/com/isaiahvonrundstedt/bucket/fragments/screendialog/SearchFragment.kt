package com.isaiahvonrundstedt.bucket.fragments.screendialog

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.adapters.experience.SearchAdapter
import com.isaiahvonrundstedt.bucket.architecture.viewmodel.SearchViewModel
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseScreenDialog
import com.isaiahvonrundstedt.bucket.components.custom.ItemDecoration
import com.isaiahvonrundstedt.bucket.components.modules.GlideApp
import kotlinx.android.synthetic.main.layout_dialog_search.*

class SearchFragment: BaseScreenDialog(), SearchView.OnQueryTextListener {

    private var layoutManager: LinearLayoutManager? = null

    private var searchAdapter: SearchAdapter? = null
    private var viewModel: SearchViewModel? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_dialog_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbarTitle?.text = getString(R.string.menu_search)

        viewModel = ViewModelProviders.of(this).get(SearchViewModel::class.java)
    }

    override fun onStart() {
        super.onStart()

        searchAdapter = SearchAdapter(context!!, childFragmentManager, GlideApp.with(this))
        layoutManager = LinearLayoutManager(context)

        recyclerView.layoutManager = layoutManager
        recyclerView.addItemDecoration(ItemDecoration(context))
        recyclerView.adapter = searchAdapter

        searchView.setOnQueryTextListener(this)
        searchView.findViewById<View?>(androidx.appcompat.R.id.search_plate)?.setBackgroundColor(Color.TRANSPARENT)

        viewModel?.itemList?.observe(this, Observer { items ->
            searchAdapter?.setObservableItems(items)
        })
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        viewModel?.filter(query)
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        viewModel?.filter(newText)
        return true
    }

}