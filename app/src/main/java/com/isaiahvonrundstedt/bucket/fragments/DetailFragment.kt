package com.isaiahvonrundstedt.bucket.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.adapters.RelatedAdapter
import com.isaiahvonrundstedt.bucket.architecture.database.AppDatabase
import com.isaiahvonrundstedt.bucket.architecture.database.SavedDAO
import com.isaiahvonrundstedt.bucket.architecture.store.SavedRepository
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseFragment
import com.isaiahvonrundstedt.bucket.constants.Firebase
import com.isaiahvonrundstedt.bucket.objects.File
import com.isaiahvonrundstedt.bucket.utils.managers.DataManager
import com.isaiahvonrundstedt.bucket.utils.managers.ItemManager
import kotlinx.android.synthetic.main.fragment_detail.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.text.DecimalFormat

class DetailFragment: BaseFragment() {

    private var file: File? = null
    private var fileInDatabase: Boolean? = false

    private var appDB: AppDatabase? = null
    private var savedDAO: SavedDAO? = null
    private var repository: SavedRepository? = null

    private val firestore by lazy { FirebaseFirestore.getInstance() }

    private lateinit var adapter: RelatedAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        file = arguments?.getParcelable("fileArgs")
        appDB = AppDatabase.getDatabase(context!!)
        savedDAO = appDB?.collectionAccessor()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onStart() {
        super.onStart()

        val query: Query = firestore.collection(Firebase.FILES.string)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .whereEqualTo("author", file?.author)
            .limit(5)

        val firestoreOptions = FirestoreRecyclerOptions.Builder<File>()
            .setLifecycleOwner(this)
            .setQuery(query, File::class.java)
            .build()

        adapter = RelatedAdapter(firestoreOptions)
        adapter.startListening()
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        iconView.setImageDrawable(ItemManager.getFileIcon(context, file?.fileType))
        titleView.text = file?.name
        authorView.text = file?.author
        fileSizeView.text = file?.fileSize.toString()
        fileTypeView?.text = ItemManager.getFileType(context, file?.fileType)
        timestampView?.text = DataManager.formatTimestamp(context, file?.timestamp)

    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    override fun onResume() = runBlocking {
        super.onResume()

        fileInDatabase = withContext(Dispatchers.Default){
            savedDAO?.checkIfExists(file)
        }

        iconView.setImageDrawable(ItemManager.getFileIcon(context, file?.fileType))
        titleView.text = file?.name
        val decimalFormat = DecimalFormat("#.##")
        fileSizeView.text = String.format(context?.resources?.getString(R.string.sheet_file_size)!!, decimalFormat.format((file!!.fileSize / 1024) / 1024))
        fileTypeView.text = String.format(context?.resources?.getString(R.string.sheet_file_type)!!, ItemManager.getFileType(context, file?.fileType))
        authorView.text = String.format(context?.resources?.getString(R.string.sheet_file_author)!!, file?.author)

        collectionsButton.setOnClickListener {
            if (!fileInDatabase!!){
                repository?.insert(file!!)
                (it as MaterialButton).text = getString(R.string.button_remove)
            } else {
                repository?.remove(file!!)
                (it as MaterialButton).text = getString(R.string.button_save)
            }
        }

    }

    override fun onDetach() {
        super.onDetach()
        activity?.finish()
    }

}