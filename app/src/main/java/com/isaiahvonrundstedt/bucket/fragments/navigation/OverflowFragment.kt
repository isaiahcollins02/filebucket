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
import com.isaiahvonrundstedt.bucket.activities.support.AccountActivity
import com.isaiahvonrundstedt.bucket.activities.support.account.NotificationActivity
import com.isaiahvonrundstedt.bucket.activities.support.account.SharedActivity
import com.isaiahvonrundstedt.bucket.activities.support.account.StorageActivity
import com.isaiahvonrundstedt.bucket.activities.wrapper.FrameActivity
import com.isaiahvonrundstedt.bucket.architecture.database.AppDatabase
import com.isaiahvonrundstedt.bucket.components.abstracts.BasePreference
import com.isaiahvonrundstedt.bucket.constants.Params
import com.isaiahvonrundstedt.bucket.utils.Preferences

class OverflowFragment: BasePreference() {


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference_overflow, rootKey)
    }

    override fun onStart() {
        super.onStart()

        val accountNav: Preference? = findPreference(getKey(R.string.accountNavKey))
        accountNav?.setOnPreferenceClickListener {
            startActivity(Intent(context, AccountActivity::class.java))
            return@setOnPreferenceClickListener true
        }

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