package com.isaiahvonrundstedt.bucket.architecture.work

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.FirebaseFirestore
import com.isaiahvonrundstedt.bucket.BuildConfig
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.constants.Firebase
import com.isaiahvonrundstedt.bucket.objects.Notification

class SupportWorker(context: Context, workerParams: WorkerParameters): Worker(context, workerParams) {

    private val firestore = FirebaseFirestore.getInstance()
    private val manager by lazy { context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }

    override fun doWork(): Result {
        firestore.collection(Firebase.CORE.string).document(Firebase.LIFE.string)
            .get().addOnCompleteListener { task ->
                if (task.isSuccessful){
                    val currentVersion = BuildConfig.VERSION_CODE.toDouble()
                    val maxVersion: Double? = task.result?.getDouble(Firebase.MAXVERSION.string)
                    val minVersion: Double? = task.result?.getDouble(Firebase.MINVERSION.string)

                    if (!(minVersion!! >= currentVersion && maxVersion!! <= currentVersion))
                        showUnsupportedVersionNotification()
                } else
                    Log.e("DataFetchError", "Error Fetching Data")
            }
        return Result.success()
    }
    private fun createSupportChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            manager.createNotificationChannel(NotificationChannel(Notification.supportChannel,
                applicationContext.getString(R.string.notification_channel_support),
                NotificationManager.IMPORTANCE_HIGH))
    }
    private fun showUnsupportedVersionNotification(){
        createSupportChannel()

        val builder = NotificationCompat.Builder(applicationContext, Notification.supportChannel)
            .setColor(ContextCompat.getColor(applicationContext, R.color.colorDefault))
            .setSmallIcon(R.drawable.ic_vector_warning)
            .setContentTitle(applicationContext.getString(R.string.notification_unsupported_version_title))
            .setContentText(applicationContext.getString(R.string.notification_unsupported_version_content))
            .setAutoCancel(false)

        manager.notify(0, builder.build())
    }
}