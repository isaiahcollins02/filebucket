package com.isaiahvonrundstedt.bucket.core.objects

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Support @JvmOverloads constructor (var userID: String?,
                                               var body: String? = null,
                                               var title: String? = null,
                                               var type: Int? = 0): Parcelable