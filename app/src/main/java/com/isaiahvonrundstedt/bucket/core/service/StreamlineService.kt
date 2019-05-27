package com.isaiahvonrundstedt.bucket.core.service

import android.app.PendingIntent
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import com.google.firebase.firestore.FirebaseFirestore
import com.isaiahvonrundstedt.bucket.BuildConfig
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.core.constants.Firebase
import com.isaiahvonrundstedt.bucket.core.objects.Package
import com.isaiahvonrundstedt.bucket.core.utils.Preferences
import com.isaiahvonrundstedt.bucket.experience.activities.MainActivity

class StreamlineService: BaseService() {

    private lateinit var firestore: FirebaseFirestore

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    companion object {
        const val ACTION_CHECK = "check"
        const val ACTION_LIFE = "life"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (ACTION_LIFE == intent?.action)
            checkifVersionSupported()
        return START_REDELIVER_INTENT
    }

    private fun checkifVersionSupported(){
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

    private fun checkForAvailableUpdates(){
        firestore = FirebaseFirestore.getInstance()
        val packageReference = firestore.collection(Firebase.CORE.string).document(Firebase.PACKAGE.string)

        packageReference.get().addOnCompleteListener {
            if (it.isSuccessful){
                val newPackage = it.result?.toObject(Package::class.java) as Package
                if (newPackage.exists){
                    Preferences(this).apply {
                        updateExists = true
                        downloadURL = newPackage.downloadURL
                        versionName = newPackage.version
                    }
                    showAvailableNotification()
                }
            } else
                Log.e("DataFetchError", "Error Fetching Required Client")
        }
    }
    private fun showAvailableNotification(){
        val resultIntent = Intent(this, MainActivity::class.java)
        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
            // Add the intent, which inflates the back stack
            addNextIntent(resultIntent)
            // Get the pending intent containing the entire back stack
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        createDefaultChannel()
        val builder = NotificationCompat.Builder(this, CHANNEL_ID_DEFAULT)
            .setSmallIcon(R.drawable.ic_vector_update)
            .setContentTitle(getString(R.string.notification_update_available_title))
            .setContentText(getString(R.string.notification_update_available_content))
            .setAutoCancel(true)
            .setContentIntent(resultPendingIntent)

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
            .setSmallIcon(icon)
            .setContentTitle(getString(R.string.notification_unsupported_version_title))
            .setContentText(getString(R.string.notification_unsupported_version_content))
            .setAutoCancel(false)
            .setContentIntent(resultPendingIntent)

        manager.notify(AVAILABLE_NOTIFICATION_ID, builder.build())
    }

}