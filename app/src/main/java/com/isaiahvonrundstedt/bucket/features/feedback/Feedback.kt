package com.isaiahvonrundstedt.bucket.features.feedback

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Feedback @JvmOverloads constructor (var userID: String?,
                                               var body: String? = null,
                                               var title: String? = null,
                                               var type: Int? = 0): Parcelable {

    companion object {
        const val supportTypeGeneric = 0
        const val supportTypePrivacy = 1
        const val supportTypeInterface = 2
        const val supportTypeUsage = 3
        const val supportTypeAccessibility = 4
    }

}