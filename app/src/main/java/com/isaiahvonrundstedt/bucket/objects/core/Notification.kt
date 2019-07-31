package com.isaiahvonrundstedt.bucket.objects.core

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.firebase.Timestamp
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.utils.converters.TimestampConverter
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "notifications")
@Parcelize
data class Notification @JvmOverloads constructor (
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    var title: String? = null,
    var content: String? = null,
    var type: Int? = typeGeneric,
    var objectID: String? = null,   // determines the object associated with the notification, e.g. the typeGeneric ID
    var objectArgs: String? = null, // determines the arguments associated with the object
                                                    // e.g. for storageItems, the download url
    @TypeConverters(TimestampConverter::class)
                    var timestamp: Timestamp? = null): Parcelable {

    companion object {
        const val typeGeneric = 0
        const val typeNewFile = 1
        const val typePackage = 2
        const val typeFetched = 3
        const val typeTransferred = 4

        const val defaultChannel = "default"
        const val transferChannel = "transfer"
        const val supportChannel = "support"

        fun obtainIconRes(type: Int?): Int? {
            return when (type){
                typeGeneric -> R.drawable.ic_bell
                typeNewFile -> R.drawable.ic_balloons
                typePackage -> R.drawable.ic_download
                typeFetched -> R.drawable.ic_download
                typeTransferred -> R.drawable.ic_upload
                else -> null
            }
        }
    }

}