package com.isaiahvonrundstedt.bucket.fragments.bottomsheet

import android.app.Application
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.button.MaterialButton
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.architecture.database.AppDatabase
import com.isaiahvonrundstedt.bucket.architecture.database.SavedDAO
import com.isaiahvonrundstedt.bucket.architecture.store.SavedRepository
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseBottomSheet
import com.isaiahvonrundstedt.bucket.objects.core.File
import com.isaiahvonrundstedt.bucket.utils.Preferences
import com.isaiahvonrundstedt.bucket.utils.managers.ItemManager
import kotlinx.android.synthetic.main.layout_sheet_details.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class DetailsBottomSheet: BaseBottomSheet() {

    private var file: File? = null
    private var downloadID: Long? = 0L
    private var appDB: AppDatabase? = null
    private var collectionDAO: SavedDAO? = null
    private var repository: SavedRepository? = null
    private var fileInDatabase: Boolean? = false

    private lateinit var request: DownloadManager.Request
    private lateinit var downloadManager: DownloadManager

    private lateinit var rootView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bundle = arguments
        file = bundle?.getParcelable("file")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.layout_sheet_details, container, false)
        downloadManager = activity?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        return rootView
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        appDB = AppDatabase.getDatabase(context)
        collectionDAO = appDB?.collectionAccessor()
        repository = SavedRepository(context.applicationContext as Application)
    }

    override fun onStart() = runBlocking {
        super.onStart()

        fileInDatabase = withContext(Dispatchers.Default){
            collectionDAO?.checkIfExists(file)
        }

        iconView.setImageDrawable(ItemManager.getFileIcon(rootView.context, file?.fileType))
        titleView.text = file?.name

        saveButton.setOnClickListener {
            if (!fileInDatabase!!){
                repository?.insert(file!!)
                (it as MaterialButton).text = getString(R.string.button_remove_from_collections)
            } else {
                repository?.remove(file!!)
                (it as MaterialButton).text = getString(R.string.button_save_to_collections)
            }
        }
        downloadButton.setOnClickListener {
            val externalDir: String? = Preferences(it.context).downloadDirectory
            val bufferedFile = java.io.File(externalDir, file?.name)

            MaterialDialog(it.context).show {
                title(text = String.format(context.getString(R.string.dialog_file_download_title), file?.name))
                message(R.string.dialog_file_download_summary)
                positiveButton(R.string.button_download) {
                    request = DownloadManager.Request(Uri.parse(file?.downloadURL))
                        .setTitle(context.getString(R.string.notification_downloading_file))
                        .setDestinationUri(bufferedFile.toUri())

                    downloadID = downloadManager.enqueue(request)
                }
                negativeButton(R.string.button_cancel)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (fileInDatabase!!)
            saveButton.text = getString(R.string.button_remove_from_collections)
        else
            saveButton.text = getString(R.string.button_save_to_collections)
    }

}