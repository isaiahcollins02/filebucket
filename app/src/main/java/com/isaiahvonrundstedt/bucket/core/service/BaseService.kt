package com.isaiahvonrundstedt.bucket.core.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.isaiahvonrundstedt.bucket.R

abstract class BaseService: Service(){

    private var numTasks = 0

    protected val manager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    fun taskStarted(){
        changeNumberOfTasks(1)
    }

    fun taskCompleted(){
        changeNumberOfTasks(-1)
    }

    companion object {
        const val CHANNEL_ID_DEFAULT = "default"

        internal const val PROGRESS_NOTIFICATION_ID = 0
        internal const val FINISHED_NOTIFICATION_ID = 1
        internal const val AVAILABLE_NOTIFICATION_ID = 2
    }

    @Synchronized
    private fun changeNumberOfTasks(delta: Int){
        numTasks += delta

        if (numTasks <= 0){
            stopSelf()
        }
    }

    protected fun createDefaultChannel(){
        // Since Android 0reo (API Level 26) is needed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(CHANNEL_ID_DEFAULT, "Default", NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(channel)
        }
    }

    // Show notification with a progress bar
    protected fun showProgressNotification(caption: String, completedUnits: Long, totalUnits: Long){
        val percentComplete: Int
        if (totalUnits > 0){
            percentComplete = (100 * completedUnits / totalUnits).toInt()

            createDefaultChannel()

            val builder = NotificationCompat.Builder(this, CHANNEL_ID_DEFAULT)
                .setSmallIcon(R.drawable.ic_vector_upload)
                .setContentTitle(caption)
                .setContentText(String.format(getString(R.string.notification_percent_complete), percentComplete))
                .setProgress(100, percentComplete, false)
                .setOngoing(true)
                .setAutoCancel(false)

            manager.notify(PROGRESS_NOTIFICATION_ID, builder.build())
        }
    }

    protected fun showFinishedNotification(caption: String, intent: Intent, success: Boolean){
        // Make pending intent for notification
        val pendingIntent = PendingIntent.getActivity(this, 0
            /* requestCode */, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val icon = if (success) R.drawable.ic_vector_check else R.drawable.ic_vector_error

        createDefaultChannel()
        val builder = NotificationCompat.Builder(this, CHANNEL_ID_DEFAULT)
            .setSmallIcon(icon)
            .setContentTitle(caption)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        manager.notify(FINISHED_NOTIFICATION_ID, builder.build())

    }

    protected fun dismissProgressNotification(){
        manager.cancel(PROGRESS_NOTIFICATION_ID)
    }

}