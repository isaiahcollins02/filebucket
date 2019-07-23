package com.isaiahvonrundstedt.bucket.service

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.net.Uri
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.architecture.store.NotificationStore
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseService
import com.isaiahvonrundstedt.bucket.objects.core.Notification
import com.isaiahvonrundstedt.bucket.utils.Preferences

class FetchService: BaseService(){

    private var intentFilter: IntentFilter? = null
    private var receiver: BroadcastReceiver? = null

    private val idList: ArrayList<Long> = ArrayList()
    private val downloadManager: DownloadManager by lazy { getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager }
    private val notificationStore: NotificationStore? by lazy { NotificationStore(application) }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        val downloadComplete = DownloadManager.ACTION_DOWNLOAD_COMPLETE
        intentFilter = IntentFilter(downloadComplete)
        receiver = object: BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                val downloadID: Long? = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0)
                if (idList.contains(downloadID))
                    initializeResult(downloadID)
            }
        }
        registerReceiver(receiver, intentFilter)
    }

    companion object {
        const val actionDownload = "action_download"

        const val extraFileName = "extra_file_name"
        const val extraDownloadURL = "extra_download_url"

        const val notificationSuccess = 0
        const val notificationFailed = 1
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == actionDownload){
            val fileName: String? = intent.getStringExtra(extraFileName)
            val downloadURL: String? = intent.getStringExtra(extraDownloadURL)
            initiateDownload(downloadURL, fileName)
        }
        return START_REDELIVER_INTENT
    }
    private fun initiateDownload(downloadURL: String?, fileName: String?){
        taskStarted()

        val externalDir: String? = Preferences(this).downloadDirectory
        val bufferedFile = java.io.File(externalDir, fileName)

        val request = DownloadManager.Request(Uri.parse(downloadURL))
            .setTitle(String.format(getString(R.string.notification_downloading_file), fileName))
            .setDestinationUri(bufferedFile.toUri())

        val downloadID = downloadManager.enqueue(request)
        idList.add(downloadID)
    }
    private fun initializeResult(downloadID: Long?){
        taskCompleted()
        val query = DownloadManager.Query()
        query.setFilterById(downloadID!!)

        val cursor: Cursor = downloadManager.query(query)
        if (!cursor.moveToFirst())
            return

        val urlIndex: Int = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)
        val downloadURL: String? = cursor.getString(urlIndex)

        val statusIndex: Int = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
        if (DownloadManager.STATUS_SUCCESSFUL != cursor.getInt(statusIndex)){
            showFailedNotification(downloadID, downloadURL)
            return
        }

        val uriIndex: Int = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)
        val localPayload: String = cursor.getString(uriIndex)
        showSuccessNotification(downloadID, localPayload)
    }

    private fun showFailedNotification(id: Long, downloadURL: String?) {
        createTransferChannel()

        val notification = Notification().apply {
            title = getString(R.string.notification_download_failed_title)
            content = getString(R.string.notification_download_failed_content)
            type = Notification.typeFetched
            objectID = id.toString()
            objectArgs = downloadURL
        }
        notificationStore?.insert(notification)

        val builder = NotificationCompat.Builder(this, Notification.transferChannel)
            .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
            .setSmallIcon(R.drawable.ic_vector_error)
            .setContentTitle(notification.title)
            .setContentText(notification.content)
        manager.notify(notificationFailed, builder.build())
    }

    private fun showSuccessNotification(id: Long, currentDirectory: String?){
        createTransferChannel()

        val notification = Notification().apply {
            title = getString(R.string.notification_download_finished_title)
            content = getString(R.string.notification_download_finished_content)
            type = Notification.typeFetched
            objectID = id.toString()
            objectArgs = currentDirectory
        }
        notificationStore?.insert(notification)

        val builder = NotificationCompat.Builder(this, Notification.transferChannel)
            .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
            .setSmallIcon(R.drawable.ic_vector_check)
            .setContentTitle(notification.title)
            .setContentText(notification.content)


        manager.notify(notificationSuccess, builder.build())
    }
}