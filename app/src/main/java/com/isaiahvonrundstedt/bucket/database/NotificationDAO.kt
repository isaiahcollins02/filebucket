package com.isaiahvonrundstedt.bucket.database

import androidx.room.*
import com.isaiahvonrundstedt.bucket.features.notifications.Notification

@Dao
interface NotificationDAO {

    @Insert
    suspend fun insert(notification: Notification)

    @Update
    suspend fun update(notification: Notification)

    @Delete
    suspend fun remove(notification: Notification)

    @Query("SELECT * FROM notifications")
    suspend fun notifications(): List<Notification>

    @Query("SELECT * FROM notifications WHERE objectID = :id")
    suspend fun notificationByObjectID(id: String): List<Notification>

    @Query("DELETE FROM notifications")
    fun clearAll()
}