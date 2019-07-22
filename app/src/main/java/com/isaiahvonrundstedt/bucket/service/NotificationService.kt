package com.isaiahvonrundstedt.bucket.service

import android.app.PendingIntent
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.activities.wrapper.FrameActivity
import com.isaiahvonrundstedt.bucket.architecture.store.NotificationStore
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseService
import com.isaiahvonrundstedt.bucket.constants.Firestore
import com.isaiahvonrundstedt.bucket.constants.Params
import com.isaiahvonrundstedt.bucket.objects.core.File
import com.isaiahvonrundstedt.bucket.objects.core.Notification
import com.isaiahvonrundstedt.bucket.utils.Preferences
import timber.log.Timber

class NotificationService: BaseService() {

    private val firestore by lazy { FirebaseFirestore.getInstance() }
    private val repository by lazy { NotificationStore(application) }

    companion object {
        private const val actionPath = "com.isaiahvonrundstedt.bucket.receivers.RestartReceiver"

        internal const val newFileNotificationID = 0
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val filesReference = firestore.collection(Firestore.files)
        filesReference.addSnapshotListener { querySnapshot, exception ->
            if (exception == null){
                for (documentSnapshot in querySnapshot!!.documents){
                    val file = documentSnapshot.toObject(File::class.java)
                    if (Preferences(this@NotificationService).fileNotification)
                        showNewFileNotification(file)
                }
            } else
                Timber.e(exception.toString())
        }
        return START_STICKY_COMPATIBILITY
    }

    override fun onDestroy() {
        super.onDestroy()

        // Send a message to the broadcast receiver if the service is being destroyed
        val broadcastIntent = Intent(actionPath)
        sendBroadcast(broadcastIntent)
    }

    private fun showNewFileNotification(file: File?){
        createDefaultChannel()

        val defaultAction = Intent(applicationContext, FrameActivity::class.java)
        defaultAction.putExtra(Params.viewArgs, file)
        val defaultIntent = PendingIntent.getBroadcast(applicationContext, 0, defaultAction, 0)

        val downloadAction = Intent(applicationContext, NotificationService::class.java)
        downloadAction.action = actionDownload
        downloadAction.putExtra(objectArgs, file)
        val downloadIntent = PendingIntent.getBroadcast(applicationContext, 0, downloadAction, 0)

        val saveAction = Intent(applicationContext, NotificationService::class.java)
        saveAction.action = actionSave
        saveAction.putExtra(objectArgs, file)
        val saveIntent = PendingIntent.getBroadcast(applicationContext, 0, saveAction, 0)

        val notification = Notification().apply {
            title = String.format(getString(R.string.notification_new_file_title), file?.author)
            content = String.format(getString(R.string.notification_new_file_content), file?.name)
            type = Notification.typeNewFile
            objectID = file?.fileID
            objectArgs = file?.downloadURL
        }
        repository.insert(notification)

        val builder = NotificationCompat.Builder(this, Notification.defaultChannel)
            .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
            .setSmallIcon(R.drawable.ic_vector_new)
            .setContentTitle(notification.title)
            .setContentText(notification.content)
            .setContentIntent(defaultIntent)
            .addAction(R.drawable.ic_vector_check, getString(R.string.button_download), downloadIntent)
            .addAction(R.drawable.ic_vector_collections, getString(R.string.button_save), saveIntent)

        manager.notify(newFileNotificationID, builder.build())
    }

}