package com.isaiahvonrundstedt.bucket.core.utils

import android.content.Context
import android.content.SharedPreferences
import android.os.Environment
import androidx.preference.PreferenceManager

class Preferences(val context: Context?) {

    companion object {
        const val METADATA_TIMESTAMP = "timestamp"
        const val METADATA_AUTHOR = "author"

        const val THEME_LIGHT = "light"
        const val THEME_DARK = "dark"
        const val THEME_AMOLED = "amoled"
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

    var versionName: Float
        set(value) {
            editor = context?.getSharedPreferences("corePreference", Context.MODE_PRIVATE)?.edit()
            editor?.putFloat("updateVersion", value)
            editor?.apply()
        }
        get() {
            sharedPreferences = context?.getSharedPreferences("corePreference", Context.MODE_PRIVATE)
            return sharedPreferences?.getFloat("updateVersion", 0.0F) as Float
        }

    var downloadURL: String?
        set(value) {
            editor = context?.getSharedPreferences("corePreference", Context.MODE_PRIVATE)?.edit()
            editor?.putString("updateURL", value)
            editor?.apply()
        }
        get() {
            sharedPreferences = context?.getSharedPreferences("corePreference", Context.MODE_PRIVATE)
            return sharedPreferences?.getString("updateURL", null)
        }

    var theme: String
        set(value) {
            editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor?.putString("appThemePref", value)
            editor?.apply()
        }
        get() {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            return sharedPreferences?.getString("appThemePref", THEME_LIGHT) as String
        }

    var updateNotification: Boolean
        set(value) {
            editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor?.putBoolean("updateNotifPref", value)
            editor?.apply()
        }
        get() {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            return sharedPreferences?.getBoolean("updateNotifPref", true) as Boolean
        }

    var fileNotification: Boolean
        set(value) {
            editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor?.putBoolean("fileNotifPref", value)
            editor?.apply()
        }
        get() {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            return sharedPreferences?.getBoolean("fileNotifPref", true) as Boolean
        }

    var downloadDirectory: String?
        set(value) {
            editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor?.putString("directoryPref", value)
            editor?.apply()
        }
        get() {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            return sharedPreferences?.getString("directoryPref", Environment.getExternalStorageDirectory().path)
        }

    var previewPreference: Boolean
        set(value) {
            editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor?.putBoolean("previewPref", value)
            editor?.apply()
        }
        get() {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            return sharedPreferences?.getBoolean("previewPref", true) as Boolean
        }

    var metadata: String?
        set(value) {
            editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor?.putString("metadataPref", value)
            editor?.apply()
        }
        get() {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            return sharedPreferences?.getString("metadataPref",
                METADATA_TIMESTAMP
            )
        }
}