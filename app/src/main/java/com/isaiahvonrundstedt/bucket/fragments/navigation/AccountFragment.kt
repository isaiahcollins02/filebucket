package com.isaiahvonrundstedt.bucket.fragments.navigation

import android.content.Intent
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.preference.Preference
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.google.firebase.auth.FirebaseAuth
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.activities.auth.FirstRunActivity
import com.isaiahvonrundstedt.bucket.activities.generic.AboutActivity
import com.isaiahvonrundstedt.bucket.activities.generic.SettingsActivity
import com.isaiahvonrundstedt.bucket.activities.support.NotificationActivity
import com.isaiahvonrundstedt.bucket.activities.support.SharedActivity
import com.isaiahvonrundstedt.bucket.activities.support.StorageActivity
import com.isaiahvonrundstedt.bucket.activities.wrapper.FrameActivity
import com.isaiahvonrundstedt.bucket.architecture.database.AppDatabase
import com.isaiahvonrundstedt.bucket.components.abstracts.BasePreference
import com.isaiahvonrundstedt.bucket.constants.Params
import com.isaiahvonrundstedt.bucket.utils.Preferences

class AccountFragment: BasePreference() {

    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_account, rootKey)
    }

    override fun onStart() {
        super.onStart()

        val sharedNav: Preference? = findPreference(getKey(R.string.sharedNavKey))
        sharedNav?.setOnPreferenceClickListener {
            startActivity(Intent(context, SharedActivity::class.java))
            return@setOnPreferenceClickListener true
        }

        val notificationNav: Preference? = findPreference(getKey(R.string.notificationNavKey))
        notificationNav?.setOnPreferenceClickListener {
            startActivity(Intent(context, NotificationActivity::class.java))
            return@setOnPreferenceClickListener true
        }

        val storageNav: Preference? = findPreference(getKey(R.string.storageNavKey))
        storageNav?.setOnPreferenceClickListener {
            startActivity(Intent(context, StorageActivity::class.java))
            return@setOnPreferenceClickListener true
        }

        val secureNav: Preference? = findPreference(getKey(R.string.secureNavKey))
        secureNav?.setOnPreferenceClickListener {
            startActivity(Intent(context, FrameActivity::class.java)
                .putExtra(Params.viewType, FrameActivity.viewTypePassword))
            return@setOnPreferenceClickListener true
        }

        val resetNav: Preference? = findPreference(getKey(R.string.resetNavKey))
        resetNav?.setOnPreferenceClickListener {
            startActivity(Intent(context, FrameActivity::class.java)
                .putExtra(Params.viewType, FrameActivity.viewTypeReset))
            return@setOnPreferenceClickListener true
        }

        val signoutNav: Preference? = findPreference(getKey(R.string.signoutNavKey))
        signoutNav?.setOnPreferenceClickListener {
            MaterialDialog(context!!).show {
                lifecycleOwner(this@AccountFragment)
                title(R.string.dialog_sign_out_title)
                message(R.string.dialog_sign_out_summary)
                positiveButton(R.string.navigation_signout) {
                    firebaseAuth.signOut()

                    Preferences(context).clear()
                    AppDatabase.destroyDatabase()

                    if (firebaseAuth.currentUser == null)
                        startActivity(Intent(context, FirstRunActivity::class.java))
                }
                negativeButton(R.string.button_cancel)
            }
            return@setOnPreferenceClickListener true
        }

        val settingsNav: Preference? = findPreference(getKey(R.string.settingsNavKey))
        settingsNav?.setOnPreferenceClickListener {
            startActivity(Intent(context, SettingsActivity::class.java))
            return@setOnPreferenceClickListener true
        }

        val aboutNav: Preference? = findPreference(getKey(R.string.aboutNavKey))
        aboutNav?.setOnPreferenceClickListener {
            startActivity(Intent(context, AboutActivity::class.java))
            return@setOnPreferenceClickListener true
        }
    }

    private fun getKey(@StringRes id: Int) = getString(id)

}