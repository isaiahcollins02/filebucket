package com.isaiahvonrundstedt.bucket.architecture.store

import android.app.Application
import androidx.core.net.toUri
import com.google.firebase.Timestamp
import com.isaiahvonrundstedt.bucket.objects.core.StorageItem
import com.isaiahvonrundstedt.bucket.utils.Preferences
import com.isaiahvonrundstedt.bucket.utils.managers.DataManager
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class LocalStore (app: Application) {

    private val currentDirectory: String? = Preferences(app).downloadDirectory

    fun fetch(onFetch: (List<StorageItem>) -> Unit) {
        val items: ArrayList<StorageItem> = ArrayList()
        val directory = File(currentDirectory)
        val files = directory.listFiles()
        for (bufferedFile: File in files){
            val currentLocalFile = StorageItem().apply {
                id = DataManager.generateRandomID()
                name = bufferedFile.name
                type = if (bufferedFile.isDirectory) StorageItem.typeDirectory else StorageItem.determineExtension(bufferedFile.toUri())
                args = bufferedFile.path
                size = bufferedFile.length()
                timestamp = Timestamp.now()
            }
            items.add(currentLocalFile)
            items.sortWith(compareBy({it.name?.toLowerCase(Locale.getDefault())}, {it.name?.toLowerCase(Locale.getDefault())}))
        }
        onFetch(items)
    }

}