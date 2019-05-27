package com.isaiahvonrundstedt.bucket.experience.fragments.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.google.android.material.button.MaterialButton
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.core.components.abstracts.BaseBottomSheet
import com.isaiahvonrundstedt.bucket.core.interfaces.TransferListener
import com.isaiahvonrundstedt.bucket.core.objects.File
import com.isaiahvonrundstedt.bucket.core.utils.Database
import com.isaiahvonrundstedt.bucket.core.utils.managers.ItemManager
import com.isaiahvonrundstedt.bucket.experience.activities.MainActivity
import java.text.DecimalFormat

class DetailsBottomSheet: BaseBottomSheet(), TransferListener {

    private var file: File? = null

    private var listener: TransferListener? = null

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
        fileTypeView = rootView.findViewById(R.id.fileTypeView)
        fileSizeView = rootView.findViewById(R.id.fileSizeView)
        authorView = rootView.findViewById(R.id.authorView)
        saveButton = rootView.findViewById(R.id.saveButton)

        return rootView
    }

    override fun onDownloadQueued(downloadID: Long) {
        if (context is MainActivity)
            listener?.onDownloadQueued(downloadID)
    }

    override fun onStart() {
        super.onStart()

        iconView.setImageDrawable(ItemManager.getFileIcon(rootView.context, file?.fileType))
        titleView.text = file?.name
        val decimalFormat = DecimalFormat("#.##")
        fileSizeView.text = String.format(rootView.resources.getString(R.string.sheet_file_size), decimalFormat.format((file!!.fileSize / 1024) / 1024))
        fileTypeView.text = String.format(rootView.resources.getString(R.string.sheet_file_type), ItemManager.getFileType(rootView.context, file?.fileType))
        authorView.text = String.format(rootView.resources.getString(R.string.sheet_file_author), file?.author)

        saveButton.setOnClickListener {
            if (!Database(it.context).checkFromCollections(file)){
                Database(it.context).saveToCollections(file)
                (it as MaterialButton).text = getString(R.string.button_remove_from_collections)
            } else {
                Database(it.context).removeFromCollections(file)
                (it as MaterialButton).text = getString(R.string.button_save_to_collections)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (Database(context!!).checkFromCollections(file))
            saveButton.text = getString(R.string.button_remove_from_collections)
        else
            saveButton.text = getString(R.string.button_save_to_collections)
    }

}