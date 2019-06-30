package com.isaiahvonrundstedt.bucket.components.abstracts

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.NotificationCompat
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.objects.Notification
import com.isaiahvonrundstedt.bucket.utils.Preferences

abstract class BaseActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        onThemeApplied()
        super.onCreate(savedInstanceState)
        // Check if the running device is a tablet or a smartphone. Then if its a smartphone
        // lock the orientation to portrait
        val screenLayoutSize =
            this.resources?.configuration?.screenLayout!! and Configuration.SCREENLAYOUT_SIZE_MASK
        if (screenLayoutSize == Configuration.SCREENLAYOUT_SIZE_SMALL || screenLayoutSize == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
            this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    private fun onThemeApplied() {
        when (Preferences(this).theme){
            Preferences.THEME_LIGHT -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            Preferences.THEME_DARK -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }

    companion object {
        internal const val AVAILABLE_NOTIFICATION_ID = 2
        internal const val NOTIFICATION_TYPE_FINISHED = 1
    }

    val manager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    internal fun sendNotification(type: Int, title: String?){
        createDefaultChannel()

        val icon = if (type == NOTIFICATION_TYPE_FINISHED) R.drawable.ic_vector_check else R.drawable.ic_vector_warning
        val builder = NotificationCompat.Builder(this, Notification.defaultChannel)
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
                Notification.defaultChannel, getString(R.string.notification_channel_default),
                NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(channel)
        }
    }

}