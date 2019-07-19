package com.isaiahvonrundstedt.bucket.fragments.screendialog

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.adapters.experience.SearchAdapter
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseScreenDialog
import com.isaiahvonrundstedt.bucket.components.custom.ItemDecoration
import kotlinx.android.synthetic.main.layout_appbar_flat.*
import kotlinx.android.synthetic.main.layout_sheet_search.*

class SearchFragment: BaseScreenDialog(), SearchView.OnQueryTextListener {

    companion object {
        const val tag = "searchFragment"
    }

    private var searchQuery: String? = null

    private var adapter: SearchAdapter? = null
    private var layoutManager: LinearLayoutManager? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_sheet_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.setNavigationOnClickListener { dismiss() }
        toolbar.navigationIcon = ResourcesCompat.getDrawable(context?.resources!!, R.drawable.ic_vector_error, null)
        toolbarTitle.text = getString(R.string.menu_search)
    }

    override fun onStart() {
        super.onStart()

        adapter = SearchAdapter(context)
        layoutManager = LinearLayoutManager(context)

        recyclerView.layoutManager = layoutManager
        recyclerView.addItemDecoration(ItemDecoration(context))
        recyclerView.adapter = adapter

        searchView.setOnQueryTextListener(this)
        searchView.findViewById<View?>(androidx.appcompat.R.id.search_plate)?.setBackgroundColor(Color.TRANSPARENT)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        searchQuery = query
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return false
    }

}