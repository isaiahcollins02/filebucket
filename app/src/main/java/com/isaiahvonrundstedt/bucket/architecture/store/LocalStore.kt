package com.isaiahvonrundstedt.bucket.architecture.store

import android.app.Application
import android.net.Uri
import com.isaiahvonrundstedt.bucket.objects.core.LocalFile
import com.isaiahvonrundstedt.bucket.utils.Preferences
import com.isaiahvonrundstedt.bucket.utils.managers.DataManager
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class LocalStore (app: Application) {

    private val currentDirectory: String? = Preferences(app).downloadDirectory

    fun fetch( onFetch: (List<LocalFile>) -> Unit) {
        val items: ArrayList<LocalFile> = ArrayList()
        val directory = File(currentDirectory)
        val files = directory.listFiles()
        for (bufferedFile: File in files){
            val currentLocalFile = LocalFile().apply {
                id = DataManager.generateRandomID()
                name = bufferedFile.name
                type = if (bufferedFile.isFile) LocalFile.file else LocalFile.directory
                args = Uri.parse(bufferedFile.path)
                size = bufferedFile.length()
                date = Date(bufferedFile.lastModified())
            }
            items.add(currentLocalFile)
        }
        onFetch(items)
    }

}