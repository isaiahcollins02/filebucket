package com.isaiahvonrundstedt.bucket.architecture.store

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.isaiahvonrundstedt.bucket.architecture.database.AppDatabase
import com.isaiahvonrundstedt.bucket.architecture.database.NotificationDAO
import com.isaiahvonrundstedt.bucket.objects.core.Notification
import kotlinx.coroutines.runBlocking

class NotificationStore(application: Application) {

    private var appDB: AppDatabase? = null
    private var notificationDAO: NotificationDAO? = null
    private lateinit var notificationLiveData: LiveData<List<Notification>>

    init {
        appDB = AppDatabase.getDatabase(application)
        notificationDAO = appDB?.notificationAccessor()
        fetch { notificationLiveData = it }
    }

    private fun fetch( onFetch: (LiveData<List<Notification>>) -> Unit ) = runBlocking {
        val itemList = notificationDAO?.getNotifications()
        val mutableItemList: MutableLiveData<List<Notification>> = MutableLiveData()
        mutableItemList.value = itemList
        onFetch(mutableItemList)
    }

    fun insert(notification: Notification) = runBlocking {
        notificationDAO?.insert(notification)
    }

    fun getNotifications(): LiveData<List<Notification>> = notificationLiveData
}