package com.isaiahvonrundstedt.bucket.objects

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Package @JvmOverloads constructor (var exists: Boolean = false,
                                              var version: Float = 0.0F,
                                              var downloadURL: String? = null): Parcelable