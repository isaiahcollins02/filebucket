package com.isaiahvonrundstedt.bucket.fragments.bottomsheet

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.google.android.material.button.MaterialButton
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.activities.MainActivity
import com.isaiahvonrundstedt.bucket.architecture.database.AppDatabase
import com.isaiahvonrundstedt.bucket.architecture.database.SavedDAO
import com.isaiahvonrundstedt.bucket.architecture.store.SavedRepository
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseBottomSheet
import com.isaiahvonrundstedt.bucket.interfaces.TransferListener
import com.isaiahvonrundstedt.bucket.objects.File
import com.isaiahvonrundstedt.bucket.utils.managers.ItemManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.text.DecimalFormat

class DetailsBottomSheet: BaseBottomSheet(), TransferListener {

    private var file: File? = null
    private var listener: TransferListener? = null
    private var appDB: AppDatabase? = null
    private var collectionDAO: SavedDAO? = null
    private var repository: SavedRepository? = null
    private var fileInDatabase: Boolean? = false

    private lateinit var rootView: View
    private lateinit var iconView: AppCompatImageView
    private lateinit var titleView: TextView
    private lateinit var fileTypeView: TextView
    private lateinit var fileSizeView: TextView
    private lateinit var authorView: TextView
    private lateinit var saveButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bundle = arguments
        file = bundle?.getParcelable("file")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.layout_sheet_details, container, false)

        iconView = rootView.findViewById(R.id.iconView)
        titleView = rootView.findViewById(R.id.titleView)
        fileTypeView = rootView.findViewById(R.id.typeView)
        fileSizeView = rootView.findViewById(R.id.sizeView)
        authorView = rootView.findViewById(R.id.authorView)
        saveButton = rootView.findViewById(R.id.saveButton)

        return rootView
    }

    override fun onDownloadQueued(downloadID: Long) {
        if (context is MainActivity)
            listener?.onDownloadQueued(downloadID)
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
        val decimalFormat = DecimalFormat("#.##")
        fileSizeView.text = String.format(rootView.resources.getString(R.string.detail_file_size), decimalFormat.format((file!!.fileSize / 1024) / 1024))
        fileTypeView.text = String.format(rootView.resources.getString(R.string.detail_file_type), ItemManager.getFileType(rootView.context, file?.fileType))
        authorView.text = String.format(rootView.resources.getString(R.string.detail_file_timestamp), file?.author)

        saveButton.setOnClickListener {
            if (!fileInDatabase!!){
                repository?.insert(file!!)
                (it as MaterialButton).text = getString(R.string.button_remove_from_collections)
            } else {
                repository?.remove(file!!)
                (it as MaterialButton).text = getString(R.string.button_save_to_collections)
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