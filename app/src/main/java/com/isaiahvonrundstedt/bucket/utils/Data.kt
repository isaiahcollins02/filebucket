package com.isaiahvonrundstedt.bucket.utils

import android.util.Patterns
import java.util.*

class Data {

    companion object {
        fun generateRandomID(): String {
            return UUID.randomUUID().toString()
        }

        fun isValidEmailAddress(address: String?): Boolean {
            return Patterns.EMAIL_ADDRESS.matcher(address).matches()
        }
    }
}