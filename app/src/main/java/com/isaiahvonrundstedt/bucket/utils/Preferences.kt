package com.isaiahvonrundstedt.bucket.utils

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Environment
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager

class Preferences(val context: Context?) {

    companion object {
        const val metadataTimestamp = "timestamp"
        const val metadataAuthor = "author"
        const val metadataType = "type"

        const val themeLight = AppCompatDelegate.MODE_NIGHT_NO
        const val themeDark = AppCompatDelegate.MODE_NIGHT_YES
        const val themeBattery = AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
        const val themeSystem = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    }

    private var sharedPreferences: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null

    fun clear(){
        editor = PreferenceManager.getDefaultSharedPreferences(context!!).edit()
        editor?.clear()
        editor?.apply()
    }

    var theme: Int
        set(value) {
            editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor?.putInt("appThemePreference", value)
            editor?.apply()
        }
        get() {
            val defaultThemeValue = when {
                Build.VERSION.SDK_INT > Build.VERSION_CODES.P -> themeSystem
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> themeBattery
                else -> themeLight
            }
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            return sharedPreferences?.getInt("appThemePreference", defaultThemeValue) as Int
        }

    var updateNotification: Boolean
        set(value) {
            editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor?.putBoolean("notificationPackagePreference", value)
            editor?.apply()
        }
        get() {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            return sharedPreferences?.getBoolean("notificationPackagePreference", true) as Boolean
        }

    var fileNotification: Boolean
        set(value) {
            editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor?.putBoolean("notificationFilePreference", value)
            editor?.apply()
        }
        get() {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            return sharedPreferences?.getBoolean("notificationFilePreference", true) as Boolean
        }

    var downloadDirectory: String?
        set(value) {
            editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor?.putString("directoryPreference", value)
            editor?.apply()
        }
        get() {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            return sharedPreferences?.getString("directoryPreference",
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path)
        }

    var previewPreference: Boolean?
        set(value) {
            editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor?.putBoolean("previewPreference", value!!)
            editor?.apply()
        }
        get() {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            return sharedPreferences?.getBoolean("previewPreference", true)
        }

    var metadata: String?
        set(value) {
            editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor?.putString("metadataPreference", value)
            editor?.apply()
        }
        get() {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            return sharedPreferences?.getString("metadataPreference", metadataTimestamp)
        }
}