package com.isaiahvonrundstedt.bucket.receivers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.objects.Notification

class TransferReceiver: BroadcastReceiver() {

    companion object {
        private const val notificationFinished = 0
    }

    private var context: Context? = null
    private val manager by lazy {
        context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    private fun createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                Notification.defaultChannel, context?.getString(R.string.notification_channel_default),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification(){
        createNotificationChannel()
        val builder = NotificationCompat.Builder(context!!, Notification.defaultChannel)
            .setColor(ContextCompat.getColor(context!!, R.color.colorPrimary))
            .setSmallIcon(R.drawable.ic_vector_check)
            .setContentTitle(context?.getString(R.string.notification_download_finished))
            .setAutoCancel(true)

        manager.notify(notificationFinished, builder.build())
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        this.context = context
        sendNotification()
    }



}