package com.isaiahvonrundstedt.bucket.architecture.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.isaiahvonrundstedt.bucket.architecture.store.NotificationRepository
import com.isaiahvonrundstedt.bucket.objects.core.Notification

class NotificationViewModel(application: Application): AndroidViewModel(application){

    private val repository: NotificationRepository = NotificationRepository(application)
    internal val items: LiveData<List<Notification>>

    init {
        items = repository.getNotifications()
    }
}