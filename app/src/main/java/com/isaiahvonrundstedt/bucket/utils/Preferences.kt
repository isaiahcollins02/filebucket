package com.isaiahvonrundstedt.bucket.utils

import android.content.Context
import android.content.SharedPreferences
import android.os.Environment
import androidx.preference.PreferenceManager

class Preferences(val context: Context?) {

    companion object {
        const val metadataTimestamp = "timestamp"
        const val metadataAuthor = "author"

        const val themeLight = "light"
        const val themeDark = "dark"
        const val themeBattery = "battery"
        const val themeSystem = "system"
    }

    private var sharedPreferences: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null

    fun clear(){
        editor = PreferenceManager.getDefaultSharedPreferences(context!!).edit()
        editor?.clear()
        editor?.apply()
    }

    var updateExists: Boolean
        set(value) {
            editor = context?.getSharedPreferences("corePreference", Context.MODE_PRIVATE)?.edit()
            editor?.putBoolean("updateExists", value)
            editor?.apply()
        }
        get() {
            sharedPreferences = context?.getSharedPreferences("corePreference", Context.MODE_PRIVATE)
            return sharedPreferences?.getBoolean("updateExists", false) as Boolean
        }

    var downloadURL: String?
        set(value) {
            editor = context?.getSharedPreferences("corePreference", Context.MODE_PRIVATE)?.edit()
            editor?.putString("newPackageURL", value)
            editor?.apply()
        }
        get() {
            sharedPreferences = context?.getSharedPreferences("corePreference", Context.MODE_PRIVATE)
            return sharedPreferences?.getString("newPackageURL", null)
        }

    var theme: String
        set(value) {
            editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor?.putString("appThemePreference", value)
            editor?.apply()
        }
        get() {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            return sharedPreferences?.getString("appThemePreference", themeLight) as String
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