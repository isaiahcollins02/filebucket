package com.isaiahvonrundstedt.bucket.architecture.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.isaiahvonrundstedt.bucket.architecture.store.NotificationStore
import com.isaiahvonrundstedt.bucket.objects.core.Notification

class NotificationViewModel(application: Application): AndroidViewModel(application){

    private val store: NotificationStore = NotificationStore(application)
    internal val items: LiveData<List<Notification>>

    init {
        items = store.getNotifications()
    }

    val size: Int
        get() = items.value?.size ?: 0
}