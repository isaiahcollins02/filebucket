package com.isaiahvonrundstedt.bucket.architecture.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.isaiahvonrundstedt.bucket.objects.File
import com.isaiahvonrundstedt.bucket.objects.Notification
import com.isaiahvonrundstedt.bucket.utils.converters.TimestampConverter

@Database(entities = [File::class, Notification::class], version = 1)
@TypeConverters(TimestampConverter::class)
abstract class AppDatabase: RoomDatabase() {

    abstract fun collectionAccessor(): SavedDAO
    abstract fun notificationAccessor(): NotificationDAO

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase? {
            if (INSTANCE == null)
                synchronized(AppDatabase::class){
                    INSTANCE = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "appDB")
                        .build()
                }
            return INSTANCE
        }

        fun destroyDatabase(){
            INSTANCE = null
        }
    }

}