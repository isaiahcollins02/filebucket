package com.isaiahvonrundstedt.bucket.experience.fragments.preference

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.preference.Preference
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.files.folderChooser
import com.afollestad.materialdialogs.list.listItems
import com.google.firebase.auth.FirebaseAuth
import com.isaiahvonrundstedt.bucket.BuildConfig
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.core.components.abstracts.BasePreference
import com.isaiahvonrundstedt.bucket.core.utils.Client
import com.isaiahvonrundstedt.bucket.core.utils.Permissions
import com.isaiahvonrundstedt.bucket.core.utils.Preferences
import com.isaiahvonrundstedt.bucket.experience.activities.auth.FirstRunActivity
import com.isaiahvonrundstedt.bucket.experience.activities.support.ProfileActivity
import com.isaiahvonrundstedt.bucket.experience.activities.support.SupportActivity

class SettingsFragment: BasePreference() {

    private val firebaseAuth = FirebaseAuth.getInstance()

    private lateinit var directoryPref: Preference

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_settings, rootKey)

        val accountPref: Preference? = findPreference("accountPref")
        accountPref?.title = Client(context!!).fullName
        accountPref?.summary = Client(context!!).email
        accountPref?.setOnPreferenceClickListener {
            startActivity(Intent(context, ProfileActivity::class.java))
            return@setOnPreferenceClickListener true
        }

        val signoutPref: Preference? = findPreference("signoutPref")
        signoutPref?.setOnPreferenceClickListener {
            MaterialDialog(context!!).show {
                title(R.string.sign_out_title)
                message(R.string.sign_out_summary)
                positiveButton(R.string.button_continue){
                    firebaseAuth.signOut()

                    Preferences(it.context).clear()

                    if (firebaseAuth.currentUser == null)
                        startActivity(Intent(context, FirstRunActivity::class.java))
                }
                negativeButton(R.string.button_cancel)
            }
            return@setOnPreferenceClickListener true
        }

        val supportPref: Preference? = findPreference("supportPref")
        supportPref?.setOnPreferenceClickListener {
            startActivity(Intent(it.context, SupportActivity::class.java))
            true
        }

        directoryPref = findPreference("directoryPref")
        directoryPref.summary = Preferences(context).downloadDirectory
        directoryPref.setOnPreferenceClickListener {
            if (Permissions(context!!).readAccessGranted){
                invokeChooser()
            } else {
                ActivityCompat.requestPermissions(activity!!,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), Permissions.READ_REQUEST)
            }
            true
        }

        val themeList = listOf(
                getString(R.string.pref_application_theme_item_default),
                getString(R.string.pref_application_theme_item_light),
                getString(R.string.pref_application_theme_item_dark),
                getString(R.string.pref_application_theme_item_amoled))

        val themePref: Preference = findPreference("appThemePref")
        themePref.summary =
            if (Preferences(context).theme != Preferences.THEME_AMOLED) Preferences(
                context
            ).theme.capitalize()
            else getString(R.string.pref_application_theme_item_amoled)
        themePref.setOnPreferenceClickListener {
            MaterialDialog(it.context).show {
                title(R.string.pref_application_theme_dialog)
                listItems(items = themeList){ _, _, theme ->
                    val newTheme: String = when (theme){
                        getString(R.string.pref_application_theme_item_default) -> Preferences.THEME_DEFAULT
                        getString(R.string.pref_application_theme_item_light) -> Preferences.THEME_LIGHT
                        getString(R.string.pref_application_theme_item_dark) -> Preferences.THEME_DARK
                        getString(R.string.pref_application_theme_item_amoled) -> Preferences.THEME_AMOLED
                        else -> Preferences.THEME_DEFAULT
                    }
                    themePref.summary = theme
                    Preferences(it.context).theme = newTheme
                }
            }
            return@setOnPreferenceClickListener true
        }

        val metadataList = listOf(
            getString(R.string.pref_metadata_item_timestamp),
            getString(R.string.pref_metadata_item_author)
        )

        val metadataPref: Preference = findPreference("metadataPref")
        metadataPref.summary = if (Preferences(context).metadata == Preferences.METADATA_TIMESTAMP)
            getString(R.string.pref_metadata_item_timestamp) else getString(R.string.pref_metadata_item_author)
        metadataPref.setOnPreferenceClickListener {
            MaterialDialog(it.context).show {
                title(R.string.pref_metadata_dialog)
                listItems(items = metadataList){ _, _, metadata ->
                    val newItem = when (metadata){
                        getString(R.string.pref_metadata_item_timestamp) -> Preferences.METADATA_TIMESTAMP
                        getString(R.string.pref_metadata_item_author) -> Preferences.METADATA_AUTHOR
                        else -> Preferences.METADATA_TIMESTAMP
                    }
                    metadataPref.summary = newItem.capitalize()
                    Preferences(it.context).metadata = newItem
                }
            }
            return@setOnPreferenceClickListener true
        }

        var count = 0

        val versionPref: Preference? = findPreference("versionPref")
        versionPref?.summary = BuildConfig.VERSION_NAME
        versionPref?.setOnPreferenceClickListener {
            if (count >= 25) {
                MaterialDialog(context!!).show {
                    title(R.string.surprise_easter_title)
                    message(R.string.surprise_easter_summary)
                }
                count = 0
            } else
                count++
            true
        }
    }

    fun invokeChooser(){
        MaterialDialog(context!!).show {
            folderChooser { _, file ->
                Preferences(context).downloadDirectory = file.path
                directoryPref.summary = Preferences(context).downloadDirectory
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        activity?.finish()
    }

}