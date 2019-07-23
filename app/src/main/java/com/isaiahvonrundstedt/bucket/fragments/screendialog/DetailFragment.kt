package com.isaiahvonrundstedt.bucket.fragments.screendialog

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.button.MaterialButton
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.adapters.experience.InfoAdapter
import com.isaiahvonrundstedt.bucket.architecture.database.AppDatabase
import com.isaiahvonrundstedt.bucket.architecture.database.SavedDAO
import com.isaiahvonrundstedt.bucket.architecture.store.SavedStore
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseScreenDialog
import com.isaiahvonrundstedt.bucket.components.custom.ItemDecoration
import com.isaiahvonrundstedt.bucket.constants.Params
import com.isaiahvonrundstedt.bucket.objects.core.File
import com.isaiahvonrundstedt.bucket.objects.experience.Information
import com.isaiahvonrundstedt.bucket.service.UsageService
import com.isaiahvonrundstedt.bucket.utils.managers.DataManager
import com.isaiahvonrundstedt.bucket.utils.managers.ItemManager
import kotlinx.android.synthetic.main.layout_dialog_detail.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.text.DecimalFormat

class DetailFragment: BaseScreenDialog() {

    private var file: File? = null
    private var fileInDatabase: Boolean? = false

    private var appDB: AppDatabase? = null
    private var savedDAO: SavedDAO? = null
    private var store: SavedStore? = null

    private var adapter: InfoAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        file = arguments?.getParcelable(Params.args)
        appDB = AppDatabase.getDatabase(context!!)
        savedDAO = appDB?.collectionAccessor()

        context?.startService(Intent(context, UsageService::class.java)
            .setAction(UsageService.sendFileUsage)
            .putExtra(UsageService.extraObjectID, file?.fileID))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_dialog_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbarTitle(R.string.file_details)
    }

    override fun onResume() = runBlocking {
        super.onResume()

        fileInDatabase = withContext(Dispatchers.Default){
            savedDAO?.checkIfExists(file)
        }

        with(file!!){
            titleView.text = name
            authorView.text = author
            iconView.setImageDrawable(ItemManager.getFileIcon(context, fileType))
        }

        val supportItems = listOf(
            Information(R.string.detail_file_size, String.format(getString(R.string.file_size_megabytes), DecimalFormat("#.##").format((file!!.fileSize / 1024) / 1024))),
            Information(R.string.detail_file_type, ItemManager.getFileType(context, file!!.fileType) ?: ""),
            Information(R.string.detail_file_timestamp, DataManager.formatTimestamp(context, file!!.timestamp) ?: ""))
        adapter = InfoAdapter(supportItems)
        recyclerView.addItemDecoration(ItemDecoration(context))
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        collectionsButton.setOnClickListener {
            if (!fileInDatabase!!){
                store?.insert(file!!)
                (it as MaterialButton).text = getString(R.string.button_remove)
            } else {
                store?.remove(file!!)
                (it as MaterialButton).text = getString(R.string.button_save)
            }
        }
    }
}