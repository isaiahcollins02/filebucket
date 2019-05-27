package com.isaiahvonrundstedt.bucket.core.objects

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.android.parcel.Parcelize

@Parcelize
data class File @JvmOverloads constructor (var name: String? = null,
                                           var id: String? = null,
                                           var author: String? = null,
                                           var fileType: Int = TYPE_GENERIC,
                                           var fileSize: Double = 0.0,
                                           var downloadURL: String? = null,
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