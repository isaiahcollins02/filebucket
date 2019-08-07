package com.isaiahvonrundstedt.bucket.fragments.preference

import android.content.Intent
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.preference.Preference
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.google.firebase.auth.FirebaseAuth
import com.isaiahvonrundstedt.bucket.components.abstracts.BasePreference
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.activities.auth.FirstRunActivity
import com.isaiahvonrundstedt.bucket.activities.wrapper.FrameActivity
import com.isaiahvonrundstedt.bucket.architecture.database.AppDatabase
import com.isaiahvonrundstedt.bucket.constants.Params
import com.isaiahvonrundstedt.bucket.utils.Preferences

class AccountFragment: BasePreference() {

    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference_account, rootKey)
    }

    override fun onStart() {
        super.onStart()

        val secureNav: Preference? = findPreference(getKey(R.string.secureNavKey))
        secureNav?.setOnPreferenceClickListener {
            startActivity(
                Intent(context, FrameActivity::class.java)
                    .putExtra(Params.viewType, FrameActivity.viewTypePassword))
            return@setOnPreferenceClickListener true
        }

        val resetNav: Preference? = findPreference(getKey(R.string.resetNavKey))
        resetNav?.setOnPreferenceClickListener {
            startActivity(
                Intent(context, FrameActivity::class.java)
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
    }

    private fun getKey(@StringRes id: Int) = getString(id)

    override fun onDetach() {
        super.onDetach()
        activity?.finish()
    }
}