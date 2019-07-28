package com.isaiahvonrundstedt.bucket.utils.managers

import android.content.Context
import android.text.format.DateUtils
import android.util.Patterns
import com.google.firebase.Timestamp
import com.isaiahvonrundstedt.bucket.R
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

object DataManager {

    // Takes a Firebase timestamp object which parses it to a Java
    // date object then formatting it with SimpleDateFormat class
    // to a string
    fun formatTimestamp(context: Context?, timestamp: Timestamp?): String? {
        val date: Date? = timestamp?.toDate()
        return if (date != null) {
            val milliseconds: Long = date.time
            val isToday = DateUtils.isToday(milliseconds)

            return when {
                !isToday -> SimpleDateFormat("h:mm a, MMM d", Locale.getDefault()).format(date)
                else -> String.format(context!!.getString(R.string.file_timestamp_today), SimpleDateFormat("h:mm a", Locale.getDefault()).format(date))
            }
        } else null
    }

    fun formatSize(context: Context?, size: Long?): String {
        return String.format(context?.getString(R.string.file_size_megabytes)!!, DecimalFormat("#.##").format((size!! / 1024) / 1024))
    }

    fun generateRandomID(): String {
        return UUID.randomUUID().toString()
    }

    fun isValidEmailAddress(address: String?): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(address).matches()
    }
}