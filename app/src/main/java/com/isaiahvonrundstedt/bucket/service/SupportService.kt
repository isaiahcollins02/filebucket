package com.isaiahvonrundstedt.bucket.service

import android.app.PendingIntent
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.ContextCompat
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.isaiahvonrundstedt.bucket.BuildConfig
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.activities.MainActivity
import com.isaiahvonrundstedt.bucket.activities.wrapper.FrameActivity
import com.isaiahvonrundstedt.bucket.architecture.store.NotificationRepository
import com.isaiahvonrundstedt.bucket.constants.Firebase
import com.isaiahvonrundstedt.bucket.architecture.database.AppDatabase
import com.isaiahvonrundstedt.bucket.architecture.database.NotificationAccessor
import com.isaiahvonrundstedt.bucket.objects.File
import com.isaiahvonrundstedt.bucket.objects.Notification
import com.isaiahvonrundstedt.bucket.receivers.NotificationReceiver
import com.isaiahvonrundstedt.bucket.utils.Preferences

class SupportService: BaseService() {

    private var appDB: AppDatabase? = null
    private var notificationAccessor: NotificationAccessor? = null

    private lateinit var firestore: FirebaseFirestore

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    companion object {
        const val ACTION_LIFE = "life"
        const val ACTION_CHECK = "check"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        appDB = AppDatabase.getDatabase(this)
        notificationAccessor = appDB?.notificationAccessor()

        if (ACTION_LIFE == intent?.action)
            checkIfSupported()
        else if (ACTION_CHECK == intent?.action)
            checkForPackages()
        return START_REDELIVER_INTENT
    }

    private fun checkIfSupported(){
        firestore = FirebaseFirestore.getInstance()

        val reference = firestore.collection(Firebase.CORE.string).document(Firebase.LIFE.string)
        reference.get().addOnCompleteListener {
            if (it.isSuccessful){
                val currentVersion = BuildConfig.VERSION_CODE.toDouble()
                val maxVersion: Double? = it.result?.getDouble(Firebase.MAXVERSION.string)
                val minVersion: Double? = it.result?.getDouble(Firebase.MINVERSION.string)

                if (!(minVersion!! >= currentVersion && maxVersion!! <= currentVersion))
                    showUnsupportedVersionNotification()
            } else
                Log.e("DataFetchError", "Error Fetching Client")
        }
    }
    private fun checkForPackages(){
        firestore = FirebaseFirestore.getInstance()

        val query: Query = firestore.collection(Firebase.FILES.string)
        query.whereEqualTo("fileType", File.TYPE_PACKAGE)
            .get()
            .addOnSuccessListener {
                for (documentSnapshot: QueryDocumentSnapshot in it){
                    val file: File? = documentSnapshot.toObject(File::class.java).apply {
                        fileID = documentSnapshot.id
                    }
                    if (file?.fileType == File.TYPE_PACKAGE){
                        if (!Preferences(this).updateExists) {
                            val notification = Notification().apply {
                                title = getString(R.string.notification_update_available_title)
                                content = getString(R.string.notification_update_available_content)
                                timestamp = Timestamp.now()
                                objectArgs = file.downloadURL
                                objectID = file.fileID
                                type = Notification.TYPE_PACKAGE
                            }
                            NotificationRepository(application).insert(notification)
                        }
                        Preferences(this).updateExists = true

                        if (Preferences(this).updateNotification)
                            showPackageNotification(file)

                        Preferences(this).updateExists = true
                    } else
                        Log.i("DataFetchResult", "No Package Available")
                }
            }
            .addOnFailureListener {
                Log.e("DataFetchError", "Error Fetching Package Data")
            }
    }
    private fun showPackageNotification(file: File) {
        createDefaultChannel()

        val defaultAction = Intent(applicationContext, FrameActivity::class.java)
        defaultAction.putExtra("VIEW_TYPE", FrameActivity.VIEW_TYPE_DETAILS)
        defaultAction.putExtra("VIEW_ARGS", file)
        val defaultIntent = PendingIntent.getBroadcast(applicationContext, 0, defaultAction, 0)

        val downloadAction = Intent(applicationContext, NotificationReceiver::class.java)
        downloadAction.action = ACTION_DOWNLOAD
        downloadAction.putExtra(BUNDLE_ARGS, file)
        val downloadIntent = PendingIntent.getBroadcast(applicationContext, 0, downloadAction, 0)

        val builder = NotificationCompat.Builder(this, CHANNEL_ID_DEFAULT)
            .setColor(ContextCompat.getColor(this, R.color.colorDefault))
            .setSmallIcon(R.drawable.ic_vector_update)
            .setContentTitle(getString(R.string.notification_update_available_title))
            .setContentText(getString(R.string.notification_update_available_content))
            .setStyle(NotificationCompat.BigTextStyle().bigText(getString(R.string.notification_update_available_content)))
            .setContentIntent(defaultIntent)
            .setAutoCancel(false)
            .addAction(R.drawable.ic_vector_download, getString(R.string.button_download), downloadIntent)

        manager.notify(AVAILABLE_NOTIFICATION_ID, builder.build())
    }

    private fun showUnsupportedVersionNotification(){
        val icon = R.drawable.ic_vector_warning

        val resultIntent = Intent(this, MainActivity::class.java)
        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
            // Add the intent, which inflates the back stack
            addNextIntent(resultIntent)
            // Get the pending intent containing the entire back stack
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        createDefaultChannel()
        val builder = NotificationCompat.Builder(this, CHANNEL_ID_DEFAULT)
            .setColor(ContextCompat.getColor(this, R.color.colorDefault))
            .setSmallIcon(icon)
            .setContentTitle(getString(R.string.notification_unsupported_version_title))
            .setContentText(getString(R.string.notification_unsupported_version_content))
            .setAutoCancel(false)
            .setContentIntent(resultPendingIntent)

        manager.notify(AVAILABLE_NOTIFICATION_ID, builder.build())
    }

}