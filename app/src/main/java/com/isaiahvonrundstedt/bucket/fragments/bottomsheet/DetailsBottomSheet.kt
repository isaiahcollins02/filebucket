package com.isaiahvonrundstedt.bucket.fragments.bottomsheet

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.button.MaterialButton
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.architecture.database.AppDatabase
import com.isaiahvonrundstedt.bucket.architecture.database.SavedDAO
import com.isaiahvonrundstedt.bucket.architecture.store.SavedStore
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseBottomSheet
import com.isaiahvonrundstedt.bucket.constants.Params
import com.isaiahvonrundstedt.bucket.objects.core.File
import com.isaiahvonrundstedt.bucket.service.FetchService
import com.isaiahvonrundstedt.bucket.service.UsageService
import com.isaiahvonrundstedt.bucket.utils.managers.ItemManager
import kotlinx.android.synthetic.main.layout_sheet_details.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class DetailsBottomSheet: BaseBottomSheet() {

    private var file: File? = null
    private var fileInDatabase: Boolean? = false

    private var appDB: AppDatabase? = null
    private var collectionDAO: SavedDAO? = null
    private var store: SavedStore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bundle = arguments
        file = bundle?.getParcelable(Params.args)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_sheet_details, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appDB = AppDatabase.getDatabase(context)
        collectionDAO = appDB?.collectionAccessor()
        store = SavedStore(context.applicationContext as Application)
    }

    override fun onStart() = runBlocking {
        super.onStart()

        fileInDatabase = withContext(Dispatchers.Default){
            collectionDAO?.checkIfExists(file)
        }

        iconView.setImageDrawable(ItemManager.getFileIcon(context, file?.fileType))
        titleView.text = file?.name

        saveButton.setOnClickListener {
            if (!fileInDatabase!!){
                store?.insert(file!!)
                (it as MaterialButton).text = getString(R.string.button_remove)
            } else {
                store?.remove(file!!)
                (it as MaterialButton).text = getString(R.string.button_save)
            }
        }
        downloadButton.setOnClickListener {
            MaterialDialog(it.context).show {
                title(text = String.format(context.getString(R.string.dialog_file_download_title), file?.name))
                message(R.string.dialog_file_download_summary)
                positiveButton(R.string.button_download) { button ->
                    it.context.startService(Intent(it.context, FetchService::class.java)
                        .setAction(FetchService.actionDownload)
                        .putExtra(FetchService.extraFileName, file?.name)
                        .putExtra(FetchService.extraDownloadURL, file?.downloadURL))

                    it.context.startService(Intent(it.context, UsageService::class.java)
                        .setAction(UsageService.sendFileUsage)
                        .putExtra(UsageService.extraObjectID, file?.fileID))
                }
                negativeButton(R.string.button_cancel)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (fileInDatabase!!)
            saveButton.text = getString(R.string.button_remove)
        else
            saveButton.text = getString(R.string.button_save)
    }

}