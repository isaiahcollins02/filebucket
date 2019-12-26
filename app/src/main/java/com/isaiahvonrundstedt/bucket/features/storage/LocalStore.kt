package com.isaiahvonrundstedt.bucket.features.storage

import android.app.Application
import androidx.core.net.toUri
import com.google.firebase.Timestamp
import com.isaiahvonrundstedt.bucket.features.shared.StorageItem
import com.isaiahvonrundstedt.bucket.utils.Preferences
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class LocalStore (app: Application) {

    constructor(app: Application, directory: String?): this(app) {
        fun fetchFromDirectory( onFetch:(List<StorageItem>) -> Unit ) {
            GlobalScope.launch {
                val items: ArrayList<StorageItem> = ArrayList()
                val filesInDirectory = File(directory)
                val files = filesInDirectory.listFiles()
                for (bufferedFile: File in files){
                    val currentItem = StorageItem().apply {
                        id = UUID.randomUUID().toString()
                        name = bufferedFile.name
                        type = if (bufferedFile.isDirectory) StorageItem.typeDirectory else StorageItem.determineExtension(bufferedFile.toUri())
                        args = bufferedFile.path
                        size = bufferedFile.length()
                        timestamp = Timestamp.now()
                    }
                    items.add(currentItem)
                }
                items.sortWith(compareBy({it.name?.toLowerCase(Locale.getDefault())}, {it.name?.toLowerCase(Locale.getDefault())}))
                onFetch(items)
            }
        }
    }

    private val storageDirectory: String? = Preferences(app).downloadDirectory



    fun fetch ( onFetch:(List<StorageItem> ) -> Unit) {
        GlobalScope.launch {
            val items: ArrayList<StorageItem> = ArrayList()
            val directory = File(storageDirectory)
            val files = directory.listFiles()
            for (bufferedFile: File in files){
                val currentItem = StorageItem().apply {
                    id = UUID.randomUUID().toString()
                    name = bufferedFile.name
                    type = if (bufferedFile.isDirectory) StorageItem.typeDirectory else StorageItem.determineExtension(bufferedFile.toUri())
                    args = bufferedFile.path
                    size = bufferedFile.length()
                    timestamp = Timestamp.now()
                }
                items.add(currentItem)
            }
            items.sortWith(compareBy({it.name?.toLowerCase(Locale.getDefault())}, {it.name?.toLowerCase(Locale.getDefault())}))
            onFetch(items)
        }
    }
}