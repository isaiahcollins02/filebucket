package com.isaiahvonrundstedt.bucket.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.isaiahvonrundstedt.bucket.features.notifications.Notification
import com.isaiahvonrundstedt.bucket.features.shared.StorageItem
import com.isaiahvonrundstedt.bucket.utils.converters.TimestampConverter

@Database(entities = [StorageItem::class, Notification::class], version = 1)
@TypeConverters(TimestampConverter::class)
abstract class AppDatabase: RoomDatabase() {

    abstract fun saved(): SavedDAO
    abstract fun notifications(): NotificationDAO

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase? {
            if (INSTANCE == null)
                synchronized(AppDatabase::class){
                    INSTANCE = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "bucketDB")
                        .build()
                }
            return INSTANCE
        }

        fun destroyDatabase(){
            INSTANCE = null
        }
    }

}