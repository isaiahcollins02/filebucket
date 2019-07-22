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
import com.isaiahvonrundstedt.bucket.adapters.core.LocalAdapter
import com.isaiahvonrundstedt.bucket.architecture.viewmodel.recycler.LocalViewModel
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseFragment
import com.isaiahvonrundstedt.bucket.components.custom.ItemDecoration
import com.isaiahvonrundstedt.bucket.components.modules.GlideApp
import kotlinx.android.synthetic.main.fragment_local.*

class LocalFragment: BaseFragment() {

    private var adapter: LocalAdapter? = null
    private var viewModel: LocalViewModel? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_local, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        viewModel = ViewModelProviders.of(this).get(LocalViewModel::class.java)
    }

    override fun onStart() {
        super.onStart()

        adapter = LocalAdapter(context, childFragmentManager, GlideApp.with(this))

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.addItemDecoration(ItemDecoration(context))
        recyclerView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        viewModel?.items?.observe(this, Observer { items ->
            adapter?.setObservableItems(items)
        })
    }

}