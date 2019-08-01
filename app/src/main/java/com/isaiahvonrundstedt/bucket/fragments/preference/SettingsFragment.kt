package com.isaiahvonrundstedt.bucket.fragments.preference

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.preference.Preference
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.files.folderChooser
import com.afollestad.materialdialogs.list.listItems
import com.google.firebase.auth.FirebaseAuth
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.activities.auth.FirstRunActivity
import com.isaiahvonrundstedt.bucket.activities.support.ProfileActivity
import com.isaiahvonrundstedt.bucket.architecture.database.AppDatabase
import com.isaiahvonrundstedt.bucket.components.abstracts.BasePreference
import com.isaiahvonrundstedt.bucket.utils.Permissions
import com.isaiahvonrundstedt.bucket.utils.Preferences
import com.isaiahvonrundstedt.bucket.utils.User

class SettingsFragment: BasePreference() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private var directoryPref: Preference? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_settings, rootKey)

        val accountPref: Preference? = findPreference("accountPreference")
        accountPref?.title = User(context!!).fullName
        accountPref?.summary = User(context!!).email
        accountPref?.setOnPreferenceClickListener {
            startActivity(Intent(context, ProfileActivity::class.java))
            return@setOnPreferenceClickListener true
        }

        val signoutPref: Preference? = findPreference("signoutPreference")
        signoutPref?.setOnPreferenceClickListener {
            MaterialDialog(context!!).show {
                title(R.string.dialog_sign_out_title)
                message(R.string.dialog_sign_out_summary)
                positiveButton(R.string.button_continue){
                    firebaseAuth.signOut()

                    Preferences(it.context).clear()
                    AppDatabase.destroyDatabase()

                    if (firebaseAuth.currentUser == null)
                        startActivity(Intent(context, FirstRunActivity::class.java))
                }
                negativeButton(R.string.button_cancel)
            }
            return@setOnPreferenceClickListener true
        }

        directoryPref = findPreference("directoryPreference")
        directoryPref?.summary = Preferences(context).downloadDirectory
        directoryPref?.setOnPreferenceClickListener {
            if (Permissions(context!!).readAccessGranted)
                invokeChooser()
            else
                ActivityCompat.requestPermissions(activity!!,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), Permissions.readRequestCode)
            true
        }

        val themeList = obtainAPIDependentThemes()
        val themePref: Preference? = findPreference("appThemePreference")
        themePref?.summary = getThemeByID(Preferences(context).theme)
        themePref?.setOnPreferenceClickListener {
            MaterialDialog(context!!, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                title(R.string.settings_theme_sheet_title)
                message(R.string.settings_theme_sheet_summary)
                listItems(items = themeList){ _, _, theme ->
                    notifyDelegate(getThemeID(theme))
                    themePref.summary = theme
                    Preferences(it.context).theme = getThemeID(theme)
                }
            }
            return@setOnPreferenceClickListener true
        }

        val metadataList = listOf(getString(R.string.settings_metadata_item_timestamp), getString(R.string.settings_metadata_item_author), getString(R.string.settings_metadata_item_type))
        val metadataPref: Preference? = findPreference("metadataPreference")
        metadataPref?.summary = if (Preferences(context).metadata == Preferences.metadataTimestamp)
            getString(R.string.settings_metadata_item_timestamp) else getString(R.string.settings_metadata_item_author)
        metadataPref?.setOnPreferenceClickListener {
            MaterialDialog(context!!, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
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
    }

    private fun notifyDelegate(themeID: Int){
        when (themeID){
            Preferences.themeLight -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            Preferences.themeDark -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            Preferences.themeBattery -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
            Preferences.themeSystem -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
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

    private fun getThemeID(item: String): Int {
        return when (item){
            getString(R.string.settings_theme_item_light) -> Preferences.themeLight
            getString(R.string.settings_theme_item_dark) -> Preferences.themeDark
            getString(R.string.settings_theme_item_battery) -> Preferences.themeBattery
            getString(R.string.settings_theme_item_system) -> Preferences.themeSystem
            else -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) Preferences.themeSystem else Preferences.themeBattery
        }
    }

    private fun obtainAPIDependentThemes(): List<String> {
        return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P)
            listOf(getString(R.string.settings_theme_item_light), getString(R.string.settings_theme_item_dark), getString(R.string.settings_theme_item_system))
        else
            listOf(getString(R.string.settings_theme_item_light), getString(R.string.settings_theme_item_dark), getString(R.string.settings_theme_item_battery))
    }

    private fun invokeChooser(){
        MaterialDialog(context!!).show {
            folderChooser { _, file ->
                Preferences(context).downloadDirectory = file.path
                directoryPref?.summary = Preferences(context).downloadDirectory
            }
        }
    }
}