package com.isaiahvonrundstedt.bucket.architecture.database

import androidx.room.*
import com.isaiahvonrundstedt.bucket.objects.core.Notification

@Dao
interface NotificationDAO {

    @Insert
    suspend fun insert(notification: Notification)

    @Update
    suspend fun update(notification: Notification)

    @Delete
    suspend fun remove(notification: Notification)

    @Query("SELECT * FROM notifications")
    suspend fun getNotifications(): List<Notification>

    @Query("SELECT * FROM notifications WHERE objectID = :id")
    suspend fun getNotificationByObjectID(id: String): List<Notification>

    @Query("DELETE FROM notifications")
    fun clearAll()
}