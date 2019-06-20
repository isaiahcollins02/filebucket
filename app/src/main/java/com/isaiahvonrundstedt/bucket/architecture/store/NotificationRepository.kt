package com.isaiahvonrundstedt.bucket.architecture.store

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import com.isaiahvonrundstedt.bucket.architecture.database.AppDatabase
import com.isaiahvonrundstedt.bucket.architecture.database.NotificationAccessor
import com.isaiahvonrundstedt.bucket.objects.Notification

class NotificationRepository(application: Application) {

    private var appDB: AppDatabase? = null
    private var notificationAccessor: NotificationAccessor? = null

    private var notificationLiveData: LiveData<List<Notification>>

    init {
        appDB = AppDatabase.getDatabase(application)
        notificationAccessor = appDB?.notificationAccessor()
        notificationLiveData = notificationAccessor?.getNotifications()!!
    }

    fun insert(notification: Notification) {
        InsertTask(notificationAccessor!!).execute(notification)
    }

    fun removeAll(){
        RemoveAllTask(notificationAccessor!!).execute()
    }

    fun getNotifications(): LiveData<List<Notification>> = notificationLiveData

    private class InsertTask (private var accessor: NotificationAccessor): AsyncTask<Notification, Void, Void>(){
        override fun doInBackground(vararg params: Notification): Void? {
            accessor.insert(params[0])
            return null
        }
    }

    private class RemoveAllTask(private var accessor: NotificationAccessor): AsyncTask<Void, Void, Void>(){
        override fun doInBackground(vararg params: Void?): Void? {
            accessor.clearAll()
            return null
        }
    }
}