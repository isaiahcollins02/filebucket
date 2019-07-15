package com.isaiahvonrundstedt.bucket.objects.core

import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.firebase.Timestamp
import com.isaiahvonrundstedt.bucket.utils.converters.TimestampConverter
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "collections")
@Parcelize
data class File @JvmOverloads constructor (@PrimaryKey
                                           @NonNull
                                           var fileID: String = "",
                                           var name: String? = null,
                                           var author: String? = null,
                                           var fileType: Int = TYPE_GENERIC,
                                           var fileSize: Double = 0.0,
                                           var downloadURL: String? = null,
                                           @TypeConverters(TimestampConverter::class)
                                           var timestamp: Timestamp? = null): Comparable<File>, Parcelable {

    override fun compareTo(other: File): Int {
        return this.name?.toLowerCase()?.compareTo(other.name?.toLowerCase()!!) as Int
    }

    companion object {
        const val TYPE_GENERIC = 0
        const val TYPE_CODE = 1
        const val TYPE_DOCUMENT = 2
        const val TYPE_IMAGE = 3
        const val TYPE_PACKAGE = 4
        const val TYPE_AUDIO = 5
        const val TYPE_VIDEO = 6
    }
}