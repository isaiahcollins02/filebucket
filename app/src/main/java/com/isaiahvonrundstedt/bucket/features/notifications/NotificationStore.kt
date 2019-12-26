package com.isaiahvonrundstedt.bucket.features.notifications

import android.app.Application
import com.isaiahvonrundstedt.bucket.database.AppDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class NotificationStore(application: Application) {

    private var database = AppDatabase.getDatabase(application)
    private var notificationDAO = database?.notifications()

    fun fetch(onFetch: (List<Notification>) -> Unit) = GlobalScope.launch {
        onFetch(notificationDAO?.notifications() ?: ArrayList())
    }

    fun insert(notification: Notification) = GlobalScope.launch {
        notificationDAO?.insert(notification)
    }

    fun update(notification: Notification) = GlobalScope.launch {
        notificationDAO?.update(notification)
    }

    fun remove(notification: Notification) = GlobalScope.launch {
        notificationDAO?.insert(notification)
    }



}