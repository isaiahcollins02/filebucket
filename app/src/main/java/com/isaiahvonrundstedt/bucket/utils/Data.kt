package com.isaiahvonrundstedt.bucket.utils

import android.content.Context
import android.text.format.DateUtils
import android.util.Patterns
import com.google.firebase.Timestamp
import com.isaiahvonrundstedt.bucket.R
import java.text.DecimalFormat
import java.text.SimpleDateFormat
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