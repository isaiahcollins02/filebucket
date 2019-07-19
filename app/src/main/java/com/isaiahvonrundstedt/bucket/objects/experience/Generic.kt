package com.isaiahvonrundstedt.bucket.objects.experience

import android.os.Bundle
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Generic(var id: String? = null, var payload: Bundle? = null): Parcelable {

    companion object {
        const val typeFile = 0
        const val typeBox = 1
    }

}