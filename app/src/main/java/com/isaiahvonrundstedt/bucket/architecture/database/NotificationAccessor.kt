package com.isaiahvonrundstedt.bucket.architecture.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.isaiahvonrundstedt.bucket.objects.Notification

@Dao
interface NotificationAccessor {

    @Insert
    fun insert(notification: Notification)

    @Update
    fun update(notification: Notification)

    @Delete
    fun remove(notification: Notification)

    @Query("SELECT * FROM notifications")
    fun getNotifications(): LiveData<List<Notification>>

    @Query("SELECT * FROM notifications WHERE objectID = :id")
    fun getNotificationByObjectID(id: String): LiveData<List<Notification>>

    @Query("DELETE FROM notifications")
    fun clearAll()
}