package com.isaiahvonrundstedt.bucket.fragments.navigation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.adapters.filterable.CoreAdapter
import com.isaiahvonrundstedt.bucket.architecture.viewmodel.core.SavedViewModel
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseFragment
import com.isaiahvonrundstedt.bucket.components.custom.ItemDecoration
import com.isaiahvonrundstedt.bucket.components.modules.GlideApp
import com.isaiahvonrundstedt.bucket.objects.core.File
import kotlinx.android.synthetic.main.fragment_saved.*

class SavedFragment: BaseFragment() {

    private var viewModel: SavedViewModel? = null
    private var adapter: CoreAdapter? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        viewModel = ViewModelProviders.of(this).get(SavedViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_saved, container, false)
    }

    override fun onStart() {
        super.onStart()

        adapter = CoreAdapter(context, childFragmentManager, GlideApp.with(this))

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.addItemDecoration(ItemDecoration(context))
        recyclerView.adapter = adapter

        viewModel?.items?.observe(this, Observer<List<File>> { items ->
            adapter?.setObservableItems(items)
        })
    }
}