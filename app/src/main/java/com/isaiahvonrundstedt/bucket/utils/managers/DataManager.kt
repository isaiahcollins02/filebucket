package com.isaiahvonrundstedt.bucket.utils.managers

import android.content.Context
import android.text.format.DateUtils
import com.google.firebase.Timestamp
import com.isaiahvonrundstedt.bucket.R
import java.text.SimpleDateFormat
import java.util.*

object DataManager {

    // Takes a client's full name (string) as parameter then
    // returns a substring from index 0 of the parent string
    // until the last index of the character ' '
    fun sliceFullName(value: String): String {
        return value.substring(0, value.lastIndexOf(' '))
    }

    // Takes a string then capitalize its every word
    fun capitalizeEachWord(s: String): String {
        return s.split(" ").joinToString(" ") { it.capitalize() }
    }

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
}