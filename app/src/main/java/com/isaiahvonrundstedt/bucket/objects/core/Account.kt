package com.isaiahvonrundstedt.bucket.objects.core

import android.os.Parcelable
import com.isaiahvonrundstedt.bucket.R
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Account @JvmOverloads constructor (
                                var exists: Boolean? = true,
                                var accountID: String? = null,
                                var firstName: String? = null,
                                var lastName: String? = null,
                                var email: String? = null,
                                var imageURL: String? = null): Comparable<Account>, Parcelable{

    override fun compareTo(other: Account): Int {
        val fullName = "$firstName $lastName"
        val otherName = "${other.firstName} ${other.lastName}"
        return fullName.compareTo(otherName)
    }

}