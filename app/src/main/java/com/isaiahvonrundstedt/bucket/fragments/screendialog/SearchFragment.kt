package com.isaiahvonrundstedt.bucket.fragments.screendialog

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseScreenDialog
import com.isaiahvonrundstedt.bucket.components.custom.ItemDecoration
import kotlinx.android.synthetic.main.layout_dialog_search.*

class SearchFragment: BaseScreenDialog(), SearchView.OnQueryTextListener {

    private var searchQuery: String? = null

    private var layoutManager: LinearLayoutManager? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_dialog_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbarTitle?.text = getString(R.string.menu_search)
    }

    override fun onStart() {
        super.onStart()
        layoutManager = LinearLayoutManager(context)

        recyclerView.layoutManager = layoutManager
        recyclerView.addItemDecoration(ItemDecoration(context))

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