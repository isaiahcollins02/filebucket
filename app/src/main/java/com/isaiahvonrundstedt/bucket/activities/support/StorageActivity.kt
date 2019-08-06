package com.isaiahvonrundstedt.bucket.activities.support

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Parcelable
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.adapters.core.LocalAdapter
import com.isaiahvonrundstedt.bucket.architecture.viewmodel.LocalViewModel
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseAppBarActivity
import com.isaiahvonrundstedt.bucket.components.modules.GlideApp
import com.isaiahvonrundstedt.bucket.utils.Permissions
import kotlinx.android.synthetic.main.activity_storage.*
import kotlinx.android.synthetic.main.layout_empty_no_access.*
import kotlinx.android.synthetic.main.layout_empty_no_items.*

class StorageActivity: BaseAppBarActivity() {

    private var onBackgroundState: Parcelable? = null

    private var adapter: LocalAdapter? = null
    private var viewModel: LocalViewModel? = null

    private var layoutManager: LinearLayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_storage)
        setToolbarTitle(R.string.navigation_downloads)

        layoutManager = LinearLayoutManager(this)
        adapter = LocalAdapter(this, supportFragmentManager, GlideApp.with(this))

        recyclerView.layoutManager = layoutManager
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
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

        if (Permissions(this).writeAccessGranted)
            initStore()
    }

    override fun onStop() {
        super.onStop()
        onBackgroundState = recyclerView.layoutManager?.onSaveInstanceState()
    }

    override fun onResume() {
        super.onResume()

        viewModel?.itemSize?.observe(this, Observer { size ->
            if (Permissions(this).readAccessGranted)
                noItemView.isVisible = false
            else if (size == 0)
                noItemView.isVisible = true
        })

        requestButton.setOnClickListener {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                Permissions.readRequestCode)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == Permissions.storageRequestCode && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            initStore()
        else
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

}