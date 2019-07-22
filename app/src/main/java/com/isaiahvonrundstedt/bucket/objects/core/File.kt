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
                                           var fileType: Int = typeGeneric,
                                           var fileSize: Double = 0.0,
                                           var downloadURL: String? = null,
                                           @TypeConverters(TimestampConverter::class)
                                           var timestamp: Timestamp? = null): Comparable<File>, Parcelable {

    override fun compareTo(other: File): Int {
        return this.name?.toLowerCase()?.compareTo(other.name?.toLowerCase()!!) as Int
    }

    companion object {
        const val typeGeneric = 0
        const val typeCode = 1
        const val typeDocument = 2
        const val typeImage = 3
        const val typePackage = 4
        const val typeAudio = 5
        const val typeVideo = 6
    }
}