package com.isaiahvonrundstedt.bucket.components.abstracts

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import com.isaiahvonrundstedt.bucket.R

abstract class BaseFragment: Fragment() {

    companion object {
        internal const val CHANNEL_ID_DEFAULT = "default"

        internal const val NOTIFICATION_TYPE_FINISHED = 1
    }

    val manager by lazy {
        context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    internal fun sendNotification(type: Int, title: String?){

        createDefaultChannel()

        val icon = if (type == NOTIFICATION_TYPE_FINISHED) R.drawable.ic_vector_check else R.drawable.ic_vector_warning

        val builder = NotificationCompat.Builder(context!!,
            CHANNEL_ID_DEFAULT
        )
            .setSmallIcon(icon)
            .setContentTitle(title)
            .setAutoCancel(true)

        manager.notify(NOTIFICATION_TYPE_FINISHED, builder.build())
    }

    private fun createDefaultChannel(){
        // Since Android O (API Level 26), a notification channel
        // is needed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                CHANNEL_ID_DEFAULT, "Default",
                NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(channel)
        }
    }

}