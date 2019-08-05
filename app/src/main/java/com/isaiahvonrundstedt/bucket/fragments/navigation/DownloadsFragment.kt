package com.isaiahvonrundstedt.bucket.fragments.navigation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.adapters.core.LocalAdapter
import com.isaiahvonrundstedt.bucket.architecture.viewmodel.LocalViewModel
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseFragment
import com.isaiahvonrundstedt.bucket.components.custom.ItemDecoration
import com.isaiahvonrundstedt.bucket.components.modules.GlideApp
import com.isaiahvonrundstedt.bucket.constants.Params
import com.isaiahvonrundstedt.bucket.utils.Permissions
import kotlinx.android.synthetic.main.fragment_local.*
import kotlinx.android.synthetic.main.layout_empty_no_access.*
import kotlinx.android.synthetic.main.layout_empty_no_items.*

class DownloadsFragment: BaseFragment() {

    private var onBackgroundState: Parcelable? = null

    private var adapter: LocalAdapter? = null
    private var viewModel: LocalViewModel? = null

    private var layoutManager: LinearLayoutManager? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_local, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        layoutManager = LinearLayoutManager(context)
        adapter = LocalAdapter(context, childFragmentManager, GlideApp.with(this))

        recyclerView.layoutManager = layoutManager
        recyclerView.addItemDecoration(ItemDecoration(context))
        recyclerView.adapter = adapter
    }

    private fun initStore(){
        noAccessView.isVisible = false
        requestButton.isVisible = false

        viewModel = ViewModelProviders.of(this).get(LocalViewModel::class.java)

        viewModel?.itemList?.observe(this, Observer { items ->
            adapter?.setObservableItems(items)
        })
    }

    override fun onStart() {
        super.onStart()

        if (onBackgroundState != null)
            recyclerView.layoutManager?.onRestoreInstanceState(onBackgroundState)

        if (Permissions(context!!).writeAccessGranted)
            initStore()
    }

    override fun onStop() {
        super.onStop()
        onBackgroundState = recyclerView.layoutManager?.onSaveInstanceState()
    }

    override fun onResume() {
        super.onResume()

        viewModel?.itemSize?.observe(this, Observer { size ->
            if (Permissions(context!!).readAccessGranted)
                noItemView.isVisible = false
            else if (size == 0)
                noItemView.isVisible = true
        })

        requestButton.setOnClickListener {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                Permissions.storageRequestCode)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == Permissions.storageRequestCode && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            initStore()
        else
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        val recyclerPastState: Parcelable? = savedInstanceState?.getParcelable(Params.recyclerState)
        if (recyclerPastState != null)
            recyclerView.layoutManager?.onRestoreInstanceState(recyclerPastState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(Params.recyclerState, recyclerView.layoutManager?.onSaveInstanceState())
    }

}