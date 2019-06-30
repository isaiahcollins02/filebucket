package com.isaiahvonrundstedt.bucket.service

import android.app.PendingIntent
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.isaiahvonrundstedt.bucket.BuildConfig
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.activities.MainActivity
import com.isaiahvonrundstedt.bucket.architecture.database.AppDatabase
import com.isaiahvonrundstedt.bucket.architecture.database.NotificationDAO
import com.isaiahvonrundstedt.bucket.constants.Firebase

class SupportService: BaseService() {

    private var appDB: AppDatabase? = null
    private var notificationDAO: NotificationDAO? = null

    private lateinit var firestore: FirebaseFirestore

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    companion object {
        const val actionLife = "life"
        const val actionSupport = "check"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        appDB = AppDatabase.getDatabase(this)
        notificationDAO = appDB?.notificationAccessor()

        if (actionLife == intent?.action)
            checkIfSupported()
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
        val builder = NotificationCompat.Builder(this, defaultChannel)
            .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
            .setSmallIcon(icon)
            .setContentTitle(getString(R.string.notification_unsupported_version_title))
            .setContentText(getString(R.string.notification_unsupported_version_content))
            .setAutoCancel(false)
            .setContentIntent(resultPendingIntent)

        manager.notify(availableNotificationID, builder.build())
    }

}