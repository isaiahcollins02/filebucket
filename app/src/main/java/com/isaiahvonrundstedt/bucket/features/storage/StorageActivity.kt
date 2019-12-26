package com.isaiahvonrundstedt.bucket.features.storage

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
import com.isaiahvonrundstedt.bucket.features.shared.abstracts.BaseAdapter
import com.isaiahvonrundstedt.bucket.features.shared.abstracts.BaseAppBarActivity
import com.isaiahvonrundstedt.bucket.features.shared.custom.GlideApp
import com.isaiahvonrundstedt.bucket.utils.Permissions
import kotlinx.android.synthetic.main.activity_storage.*
import kotlinx.android.synthetic.main.layout_empty_no_access.*
import kotlinx.android.synthetic.main.layout_empty_no_local.*

class StorageActivity: BaseAppBarActivity(), BaseAdapter.DirectoryListener {

    private var onBackgroundState: Parcelable? = null

    private var adapter: LocalAdapter? = null
    private var viewModel: LocalViewModel? = null

    private var layoutManager: LinearLayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_storage)
        setToolbarTitle(R.string.navigation_downloads)

        layoutManager = LinearLayoutManager(this)
        adapter = LocalAdapter(this, supportFragmentManager, GlideApp.with(this), this)

        recyclerView.layoutManager = layoutManager
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        recyclerView.adapter = adapter
    }

    override fun onFolderPressed(directory: String?) {
        viewModel?.fetchFromDirectory(directory)
    }

    override fun onStart() {
        super.onStart()

        noItemView.isVisible = false
        if (onBackgroundState != null)
            recyclerView.layoutManager?.onRestoreInstanceState(onBackgroundState)

        noAccessView.isVisible = false
        requestButton.isVisible = false

        viewModel = ViewModelProviders.of(this).get(LocalViewModel::class.java)

        viewModel?.itemList?.observe(this, Observer { items ->
            adapter?.setObservableItems(items)
        })

        viewModel?.itemSize?.observe(this, Observer { size ->
            if (Permissions(this).writeAccessGranted)
                noItemView.isVisible = size == 0
        })
    }

    override fun onStop() {
        super.onStop()
        onBackgroundState = recyclerView.layoutManager?.onSaveInstanceState()
    }

    override fun onResume() {
        super.onResume()

        if (!Permissions(this).readAccessGranted){
            noAccessView.isVisible = true
            requestButton.isVisible = true
        }

        requestButton.setOnClickListener {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                Permissions.readRequestCode)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == Permissions.readRequestCode && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            viewModel?.setStorageAccessChanged()

            noAccessView.isVisible = false
            requestButton.isVisible = false
        } else
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

}