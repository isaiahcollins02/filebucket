package com.isaiahvonrundstedt.bucket.objects.core

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class LocalFile @JvmOverloads constructor(
    var id: String? = null,
    var name: String? = null,
    var type: Int = 0,
    var size: Long = 0L,
    var args: Uri? = null,
    var date: Date? = null): Parcelable {

    companion object {
        const val file = 0
        const val directory = 1
    }

}