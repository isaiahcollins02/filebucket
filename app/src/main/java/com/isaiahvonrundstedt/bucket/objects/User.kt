package com.isaiahvonrundstedt.bucket.objects

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User @JvmOverloads constructor (
                                var exists: Boolean? = true,
                                var accountID: String? = null,
                                var firstName: String? = null,
                                var lastName: String? = null,
                                var email: String? = null,
                                var password: String? = null,
                                var imageURL: String? = null): Comparable<User>, Parcelable{

    override fun compareTo(other: User): Int {
        val fullName = "$firstName $lastName"
        val otherName = "${other.firstName} ${other.lastName}"
        return fullName.compareTo(otherName)
    }

}