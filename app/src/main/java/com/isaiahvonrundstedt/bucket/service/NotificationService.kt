package com.isaiahvonrundstedt.bucket.service

import android.app.PendingIntent
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.activities.wrapper.FrameActivity
import com.isaiahvonrundstedt.bucket.architecture.store.NotificationRepository
import com.isaiahvonrundstedt.bucket.constants.Firebase
import com.isaiahvonrundstedt.bucket.objects.File
import com.isaiahvonrundstedt.bucket.objects.Notification
import com.isaiahvonrundstedt.bucket.utils.Preferences

class NotificationService: BaseService() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth

    companion object {
        private const val ACTION_PATH = "com.isaiahvonrundstedt.bucket.receivers.RestartReceiver"
        private const val CHANNEL_ID_DEFAULT = "default"

        internal const val NEW_FILE_NOTIFICATION_ID = 0
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val filesReference = firestore.collection(Firebase.FILES.string)
        filesReference.addSnapshotListener { querySnapshot, exception ->
            if (exception == null){
                for (documentSnapshot in querySnapshot!!.documents){
                    val file = documentSnapshot.toObject(File::class.java)
                    if (Preferences(this@NotificationService).fileNotification)
                        showNewFileNotification(file)
                }
            } else
                Log.e("FirebaseException", exception.toString())
        }
        return START_STICKY_COMPATIBILITY
    }

    override fun onCreate() {
        super.onCreate()

        firestore = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
    }

    override fun onDestroy() {
        super.onDestroy()

        // Send a message to the broadcast receiver if the service is being destroyed
        val broadcastIntent = Intent(ACTION_PATH)
        sendBroadcast(broadcastIntent)
    }

    private fun showNewFileNotification(file: File?){
        createDefaultChannel()

        val defaultAction = Intent(applicationContext, FrameActivity::class.java)
        defaultAction.putExtra("VIEW_TYPE", FrameActivity.VIEW_TYPE_DETAILS)
        defaultAction.putExtra("VIEW_ARGS", file)
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
            type = Notification.TYPE_NEW_FILE
            objectID = file?.fileID
            objectArgs = file?.downloadURL
        }
        NotificationRepository(application).insert(notification)

        val builder = NotificationCompat.Builder(this, CHANNEL_ID_DEFAULT)
            .setColor(ContextCompat.getColor(this, R.color.colorDefault))
            .setSmallIcon(R.drawable.ic_vector_new)
            .setContentTitle(notification.title)
            .setContentText(notification.content)
            .setContentIntent(defaultIntent)
            .addAction(R.drawable.ic_vector_check, getString(R.string.button_download), downloadIntent)
            .addAction(R.drawable.ic_vector_collections, getString(R.string.button_save_to_collections), saveIntent)

        manager.notify(NEW_FILE_NOTIFICATION_ID, builder.build())
    }

}