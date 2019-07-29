package com.isaiahvonrundstedt.bucket.architecture.store.room

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.isaiahvonrundstedt.bucket.architecture.database.AppDatabase
import com.isaiahvonrundstedt.bucket.architecture.database.NotificationDAO
import com.isaiahvonrundstedt.bucket.objects.core.Notification
import kotlinx.coroutines.runBlocking

class NotificationStore(application: Application) {

    private var database = AppDatabase.getDatabase(application)
    private var notificationDAO = database?.notificationAccessor()

    fun fetch( onFetch: (List<Notification>) -> Unit ) = runBlocking {
        onFetch(notificationDAO?.getNotifications() ?: ArrayList())
    }

    fun insert(notification: Notification) = runBlocking {
        notificationDAO?.insert(notification)
    }

    fun remove(notification: Notification) = runBlocking {
        notificationDAO?.remove(notification)
    }
}