package com.isaiahvonrundstedt.bucket.utils

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
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

    var isFirstRun: Boolean
        set(value) {
            editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor?.putBoolean("firstRunPreference", value)
            editor?.apply()
        }
        get() {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            return sharedPreferences?.getBoolean("firstRunPreference", true) ?: false
        }

    var theme: Int
        set(value) {
            editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor?.putInt("appThemePreference", value)
            editor?.apply()
        }
        get() {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            return sharedPreferences?.getInt("appThemePreference", obtainAPIDependentDefaultTheme()) as Int
        }

    private fun obtainAPIDependentDefaultTheme(): Int {
        return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P)
            themeSystem
        else themeBattery
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
            return sharedPreferences?.getString("directoryPreference", Environment.getExternalStorageDirectory().path)
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