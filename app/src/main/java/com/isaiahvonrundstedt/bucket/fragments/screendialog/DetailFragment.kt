package com.isaiahvonrundstedt.bucket.fragments.screendialog

import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.adapters.support.InfoAdapter
import com.isaiahvonrundstedt.bucket.architecture.database.AppDatabase
import com.isaiahvonrundstedt.bucket.architecture.database.SavedDAO
import com.isaiahvonrundstedt.bucket.architecture.store.room.SavedStore
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseScreenDialog
import com.isaiahvonrundstedt.bucket.constants.Params
import com.isaiahvonrundstedt.bucket.objects.core.StorageItem
import com.isaiahvonrundstedt.bucket.objects.experience.Info
import com.isaiahvonrundstedt.bucket.service.UsageService
import kotlinx.android.synthetic.main.layout_dialog_detail.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class DetailFragment: BaseScreenDialog() {

    private var storageItem: StorageItem? = null
    private var exists: Boolean = false

    private var savedDAO: SavedDAO? = null
    private var store: SavedStore? = null

    private var adapter: InfoAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        storageItem = arguments?.getParcelable(Params.payload)
        savedDAO = AppDatabase.getDatabase(activity?.applicationContext!!)?.saved()
        store = SavedStore(activity?.application!!)

        context?.startService(Intent(context, UsageService::class.java)
            .setAction(UsageService.sendFileUsage)
            .putExtra(UsageService.extraObjectID, storageItem?.id))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_dialog_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbarTitle?.text = getString(R.string.detail_file_title)
    }

    override fun onStart() {
        super.onStart()
        databaseInit()

        titleView.text = storageItem?.name
        authorView.text = storageItem?.author
        iconView.setImageDrawable(getIconDrawable(storageItem?.type))

        val supportItems = listOf(
            Info(R.string.detail_file_size, storageItem?.formatSize(context!!) ?: getString(R.string.unknown_file_size)),
            Info(R.string.detail_file_type, getString(StorageItem.obtainItemTypeID(storageItem?.type))),
            Info(R.string.detail_file_timestamp, storageItem?.formatTimestamp(context!!) ?: getString(R.string.unknown_file_timestamp)))

        adapter = InfoAdapter(supportItems)

        recyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()

        collectionsButton.setOnClickListener {
            if (exists){
                store?.remove(storageItem!!)
                Snackbar.make(it, R.string.status_item_removed, Snackbar.LENGTH_SHORT).show()
                (it as MaterialButton).text = getString(R.string.button_save)
            } else {
                store?.insert(storageItem!!)
                Snackbar.make(it, R.string.status_item_saved, Snackbar.LENGTH_SHORT).show()
                (it as MaterialButton).text = getString(R.string.button_remove)
            }
        }
    }

    private fun databaseInit() = runBlocking {
        exists = withContext(Dispatchers.Default) { savedDAO?.checkIfExists(storageItem) ?: false }
        collectionsButton.text = if (exists) getString(R.string.button_remove) else getString(R.string.button_save)
    }

    private fun getIconDrawable(type: Int?): Drawable? {
        val drawable = ResourcesCompat.getDrawable(context?.resources!!, StorageItem.obtainIconID(type), null)
        drawable?.setColorFilter(ContextCompat.getColor(context!!, StorageItem.obtainColorID(type)), PorterDuff.Mode.SRC_ATOP)
        return drawable
    }

}