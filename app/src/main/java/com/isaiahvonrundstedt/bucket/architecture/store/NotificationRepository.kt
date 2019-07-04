package com.isaiahvonrundstedt.bucket.architecture.store

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import com.isaiahvonrundstedt.bucket.architecture.database.AppDatabase
import com.isaiahvonrundstedt.bucket.architecture.database.NotificationDAO
import com.isaiahvonrundstedt.bucket.objects.Notification
import kotlinx.coroutines.Dispatchers

class NotificationRepository(application: Application) {

    private var appDB: AppDatabase? = null
    private var notificationDAO: NotificationDAO? = null

    private var notificationLiveData: LiveData<List<Notification>>

    init {
        appDB = AppDatabase.getDatabase(application)
        notificationDAO = appDB?.notificationAccessor()
        notificationLiveData = notificationDAO?.getNotifications()!!
    }

    fun insert(notification: Notification) {
        InsertTask(notificationDAO!!).execute(notification)
    }

    fun getNotifications(): LiveData<List<Notification>> = notificationLiveData

    private class InsertTask (private var DAO: NotificationDAO): AsyncTask<Notification, Void, Void>(){
        override fun doInBackground(vararg params: Notification): Void? {
            DAO.insert(params[0])
            return null
        }
    }

    private class RemoveAllTask(private var DAO: NotificationDAO): AsyncTask<Void, Void, Void>(){
        override fun doInBackground(vararg params: Void?): Void? {
            DAO.clearAll()
            return null
        }
    }
}