package com.isaiahvonrundstedt.bucket.features.notifications

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.net.toUri
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.features.shared.abstracts.BaseService
import com.isaiahvonrundstedt.bucket.features.shared.StorageItem
import com.isaiahvonrundstedt.bucket.utils.Preferences
import timber.log.Timber
import java.io.File

class NotificationReceiver: BroadcastReceiver() {

    private var downloadID: Long? = 0L

    private lateinit var request: DownloadManager.Request
    private lateinit var downloadManager: DownloadManager

    override fun onReceive(context: Context?, intent: Intent?) {
        downloadManager = context?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val item: StorageItem? = intent?.getParcelableExtra(BaseService.objectArgs)
        val externalDir: String? = Preferences(context).downloadDirectory
        val bufferedFile = File(externalDir, item?.name)

        when (intent?.action){
            BaseService.actionDownload -> {
                request = DownloadManager.Request(Uri.parse(item?.args))
                    .setTitle(context.getString(R.string.notification_downloading_file))
                    .setDestinationUri(bufferedFile.toUri())

                downloadID = downloadManager.enqueue(request)
                Timber.i("Data Received")
            }
            BaseService.actionSave -> {

            }
        }
    }

}