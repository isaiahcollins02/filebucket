package com.isaiahvonrundstedt.bucket.fragments.preference

import android.os.Bundle
import androidx.preference.Preference
import com.isaiahvonrundstedt.bucket.BuildConfig
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.components.abstracts.BasePreference
import com.isaiahvonrundstedt.bucket.constants.Params
import com.isaiahvonrundstedt.bucket.fragments.screendialog.FeedbackFragment
import com.isaiahvonrundstedt.bucket.fragments.screendialog.WebViewFragment

class AboutFragment: BasePreference() {

    private val termsKey by lazy { getString(R.string.settings_key_terms) }
    private val privacyPolicyKey by lazy { getString(R.string.settings_key_privacy_policy) }
    private val licensesKey by lazy { getString(R.string.settings_key_licenses) }
    private val feedbackKey by lazy { getString(R.string.settings_key_feedback) }
    private val versionKey by lazy { getString(R.string.settings_key_version) }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_about, rootKey)
    }

    override fun onStart() {
        super.onStart()

        val webViewFragment = WebViewFragment()
        val args = Bundle()

        val termsPreference: Preference? = findPreference(termsKey)
        termsPreference?.setOnPreferenceClickListener {
            args.putInt(Params.viewType, WebViewFragment.viewTypeTerms)

            webViewFragment.arguments = args
            webViewFragment.invoke(childFragmentManager)
            return@setOnPreferenceClickListener true
        }

        val privacyPreference: Preference? = findPreference(privacyPolicyKey)
        privacyPreference?.setOnPreferenceClickListener {
            args.putInt(Params.viewType, WebViewFragment.viewTypePrivacy)

            webViewFragment.arguments = args
            webViewFragment.invoke(childFragmentManager)

            return@setOnPreferenceClickListener true
        }

        val licensesPreference: Preference? = findPreference(licensesKey)
        licensesPreference?.setOnPreferenceClickListener {
            args.putInt(Params.viewType, WebViewFragment.viewTypeLicense)

            webViewFragment.arguments = args
            webViewFragment.invoke(childFragmentManager)

            return@setOnPreferenceClickListener true
        }

        val feedbackPreference: Preference? = findPreference(feedbackKey)
        feedbackPreference?.setOnPreferenceClickListener {
            val feedbackFragment = FeedbackFragment()
            feedbackFragment.invoke(childFragmentManager)
            return@setOnPreferenceClickListener true
        }

        val versionPref: Preference? = findPreference(versionKey)
        versionPref?.summary = BuildConfig.VERSION_NAME
    }

    override fun onDetach() {
        super.onDetach()
        activity?.finish()
    }

}