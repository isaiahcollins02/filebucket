package com.isaiahvonrundstedt.bucket.utils.converters

import androidx.room.TypeConverter
import com.google.firebase.Timestamp
import java.util.*

class TimestampConverter {

    companion object {
        @JvmStatic
        @TypeConverter
        fun fromTimestamp(timestamp: Timestamp?): Long? {
            return timestamp?.toDate()?.time
        }

        @TypeConverter
        @JvmStatic
        fun toTimestamp(value: Long?): Timestamp? {
            return Timestamp(Date(value ?: 0))
        }
    }

}