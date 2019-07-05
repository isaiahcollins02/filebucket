package com.isaiahvonrundstedt.bucket.fragments.preference

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.preference.Preference
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.files.folderChooser
import com.afollestad.materialdialogs.list.listItems
import com.google.firebase.auth.FirebaseAuth
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.activities.auth.FirstRunActivity
import com.isaiahvonrundstedt.bucket.activities.support.ProfileActivity
import com.isaiahvonrundstedt.bucket.architecture.database.AppDatabase
import com.isaiahvonrundstedt.bucket.components.abstracts.BasePreference
import com.isaiahvonrundstedt.bucket.utils.Account
import com.isaiahvonrundstedt.bucket.utils.Permissions
import com.isaiahvonrundstedt.bucket.utils.Preferences

class SettingsFragment: BasePreference() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private var directoryPref: Preference? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_settings, rootKey)

        val accountPref: Preference? = findPreference("accountPreference")
        accountPref?.title = Account(context!!).fullName
        accountPref?.summary = Account(context!!).email
        accountPref?.setOnPreferenceClickListener {
            startActivity(Intent(context, ProfileActivity::class.java))
            return@setOnPreferenceClickListener true
        }

        val signoutPref: Preference? = findPreference("signoutPreference")
        signoutPref?.setOnPreferenceClickListener {
            MaterialDialog(context!!).show {
                title(R.string.sign_out_title)
                message(R.string.sign_out_summary)
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
            if (Permissions(context!!).readAccessGranted){
                invokeChooser()
            } else {
                ActivityCompat.requestPermissions(activity!!,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), Permissions.readRequestCode)
            }
            true
        }

        val themeList = listOf(getString(R.string.settings_theme_item_light), getString(R.string.settings_theme_item_dark))
        val themePref: Preference? = findPreference("appThemePreference")
        themePref?.summary = Preferences(context).theme.capitalize()
        themePref?.setOnPreferenceClickListener {
            MaterialDialog(it.context).show {
                title(R.string.settings_theme_dialog)
                listItems(items = themeList){ _, _, theme ->
                    val themeID : String? = when (theme){
                        getString(R.string.settings_theme_item_light) -> Preferences.themeLight
                        getString(R.string.settings_theme_item_dark) -> Preferences.themeDark
                        else -> null
                    }
                    when (themeID){
                        Preferences.themeLight -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                        Preferences.themeDark -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    }
                    themePref.summary = theme
                    Preferences(it.context).theme = themeID!!
                }
            }
            return@setOnPreferenceClickListener true
        }

        val metadataList = listOf(getString(R.string.settings_metadata_item_timestamp), getString(R.string.settings_metadata_item_author))
        val metadataPref: Preference? = findPreference("metadataPreference")
        metadataPref?.summary = if (Preferences(context).metadata == Preferences.metadataTimestamp)
            getString(R.string.settings_metadata_item_timestamp) else getString(R.string.settings_metadata_item_author)
        metadataPref?.setOnPreferenceClickListener {
            MaterialDialog(it.context).show {
                title(R.string.settings_metadata_dialog)
                listItems(items = metadataList){ _, _, metadata ->
                    val newItem = when (metadata){
                        getString(R.string.settings_metadata_item_timestamp) -> Preferences.metadataTimestamp
                        getString(R.string.settings_metadata_item_author) -> Preferences.metadataAuthor
                        else -> Preferences.metadataTimestamp
                    }
                    metadataPref.summary = newItem.capitalize()
                    Preferences(it.context).metadata = newItem
                }
            }
            return@setOnPreferenceClickListener true
        }
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