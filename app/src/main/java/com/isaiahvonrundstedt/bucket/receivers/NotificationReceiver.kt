package com.isaiahvonrundstedt.bucket.receivers

import android.app.Application
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.net.toUri
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.architecture.store.SavedRepository
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseService
import com.isaiahvonrundstedt.bucket.objects.core.File
import com.isaiahvonrundstedt.bucket.utils.Preferences
import timber.log.Timber

class NotificationReceiver: BroadcastReceiver() {

    private var downloadID: Long? = 0L

    private lateinit var request: DownloadManager.Request
    private lateinit var downloadManager: DownloadManager

    override fun onReceive(context: Context?, intent: Intent?) {
        downloadManager = context?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val file: File? = intent?.getParcelableExtra(BaseService.objectArgs)
        val externalDir: String? = Preferences(context).downloadDirectory
        val bufferedFile = java.io.File(externalDir, file?.name)

        when (intent?.action){
            BaseService.actionDownload -> {
                request = DownloadManager.Request(Uri.parse(file?.downloadURL))
                    .setTitle(context.getString(R.string.notification_downloading_file))
                    .setDestinationUri(bufferedFile.toUri())

                downloadID = downloadManager.enqueue(request)
                Timber.i("Data Received")
            }
            BaseService.actionSave -> {
                SavedRepository(context.applicationContext as Application).insert(file!!)
            }
        }
    }

}