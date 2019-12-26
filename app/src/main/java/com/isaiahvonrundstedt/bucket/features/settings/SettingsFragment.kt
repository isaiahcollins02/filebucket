package com.isaiahvonrundstedt.bucket.features.settings

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.preference.Preference
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.files.folderChooser
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.afollestad.materialdialogs.list.listItems
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.features.shared.abstracts.BasePreference
import com.isaiahvonrundstedt.bucket.utils.Permissions
import com.isaiahvonrundstedt.bucket.utils.Preferences

class SettingsFragment: BasePreference() {

    private val appThemeKey by lazy { getString(R.string.settings_key_theme) }
    private val metadataKey by lazy { getString(R.string.settings_key_metadata) }
    private val appNotificationKey by lazy { getString(R.string.settings_key_app_notification) }
    private val directoryKey by lazy { getString(R.string.settings_key_directory) }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference_settings, rootKey)
    }

    private fun notifyDelegate(themeID: Int){
        when (themeID){
            Preferences.themeLight -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            Preferences.themeDark -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            Preferences.themeBattery -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
            Preferences.themeSystem -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

    override fun onStart() {
        super.onStart()

        val themePreference: Preference? = findPreference(appThemeKey)
        themePreference?.summary = getThemeByID(Preferences(context!!).theme)
        themePreference?.setOnPreferenceClickListener {
            MaterialDialog(context!!, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                lifecycleOwner(this@SettingsFragment)
                title(R.string.settings_theme_sheet_title)
                message(com.isaiahvonrundstedt.bucket.R.string.settings_theme_sheet_summary)
                listItems(items = obtainAPIDependentThemes()){ _, _, theme ->
                    notifyDelegate(getThemeID(theme.toString()))
                    themePreference.summary = theme
                    Preferences(it.context).theme = getThemeID(theme.toString())
                }
            }
            return@setOnPreferenceClickListener true
        }

        val metadataList = listOf(getString(R.string.settings_metadata_item_timestamp), getString(R.string.settings_metadata_item_author), getString(R.string.settings_metadata_item_type))
        val metadataPref: Preference? = findPreference(metadataKey)
        metadataPref?.summary = if (Preferences(context).metadata == Preferences.metadataTimestamp)
            getString(R.string.settings_metadata_item_timestamp) else getString(R.string.settings_metadata_item_author)
        metadataPref?.setOnPreferenceClickListener {
            MaterialDialog(context!!, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                lifecycleOwner(this@SettingsFragment)
                title(R.string.settings_metadata_title)
                message(R.string.settings_metadata_summary)
                listItems(items = metadataList){ _, index, metadata ->
                    val newItem = when (index){
                        0 -> Preferences.metadataTimestamp
                        1 -> Preferences.metadataAuthor
                        2 -> Preferences.metadataType
                        else -> Preferences.metadataTimestamp
                    }
                    metadataPref.summary = metadata
                    Preferences(it.context).metadata = newItem
                }
            }
            return@setOnPreferenceClickListener true
        }

        val appNotificationPreference: Preference? = findPreference(appNotificationKey)
        appNotificationPreference?.setOnPreferenceClickListener {
            val intent = Intent()
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, context?.packageName)
                    intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> {
                    intent.putExtra("app_package", context?.packageName)
                    intent.putExtra("app_uid", context?.applicationInfo?.uid)
                    intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
                }
                else -> {
                    intent.addCategory(Intent.CATEGORY_DEFAULT)
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    intent.data = Uri.parse("package:" + context?.packageName)
                }
            }
            startActivity(intent)
            return@setOnPreferenceClickListener true
        }

        val directoryPreference: Preference? = findPreference(directoryKey)
        directoryPreference?.summary = Preferences(context).downloadDirectory
        directoryPreference?.setOnPreferenceClickListener {
            if (Permissions(context!!).readAccessGranted){
                MaterialDialog(context!!).show {
                    lifecycleOwner(this@SettingsFragment)
                    folderChooser { _, file ->
                        Preferences(context).downloadDirectory = file.path
                        directoryPreference.summary = Preferences(context).downloadDirectory
                    }
                }
            }
            else
                ActivityCompat.requestPermissions(activity!!,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), Permissions.readRequestCode)
            true
        }
    }

    private fun getThemeByID(item: Int): String? {
        return when (item) {
            Preferences.themeLight -> getString(R.string.settings_theme_item_light)
            Preferences.themeDark -> getString(R.string.settings_theme_item_dark)
            Preferences.themeBattery -> getString(R.string.settings_theme_item_battery)
            Preferences.themeSystem -> getString(R.string.settings_theme_item_system)
            else -> null
        }
    }

    private fun getThemeID(item: String?): Int {
        return when (item){
            getString(R.string.settings_theme_item_light) -> Preferences.themeLight
            getString(R.string.settings_theme_item_dark) -> Preferences.themeDark
            getString(R.string.settings_theme_item_battery) -> Preferences.themeBattery
            getString(R.string.settings_theme_item_system) -> Preferences.themeSystem
            else -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) Preferences.themeSystem else Preferences.themeBattery
        }
    }

    private fun obtainAPIDependentThemes(): List<String> {
        return when {
            Build.VERSION.SDK_INT > Build.VERSION_CODES.P ->
                listOf(getString(R.string.settings_theme_item_light), getString(R.string.settings_theme_item_dark), getString(R.string.settings_theme_item_system))
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ->
                listOf(getString(R.string.settings_theme_item_light), getString(R.string.settings_theme_item_dark), getString(R.string.settings_theme_item_battery))
            else ->
                listOf(getString(R.string.settings_theme_item_light), getString(R.string.settings_theme_item_dark))
        }
    }

}