package com.isaiahvonrundstedt.bucket.fragments.preference

import android.os.Bundle
import androidx.preference.Preference
import com.isaiahvonrundstedt.bucket.BuildConfig
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.components.abstracts.BasePreference
import com.isaiahvonrundstedt.bucket.constants.Params
import com.isaiahvonrundstedt.bucket.fragments.screendialog.SupportFragment
import com.isaiahvonrundstedt.bucket.fragments.screendialog.WebViewFragment

class AboutFragment: BasePreference() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_about, rootKey)

        val webViewFragment = WebViewFragment()
        val args = Bundle()

        val policyPref: Preference? = findPreference("policyPref")
        policyPref?.setOnPreferenceClickListener {
            args.putInt(Params.viewType, WebViewFragment.viewTypePrivacy)

            webViewFragment.arguments = args
            webViewFragment.invoke(childFragmentManager)
            return@setOnPreferenceClickListener true
        }

        val termsPref: Preference? = findPreference("termsPref")
        termsPref?.setOnPreferenceClickListener {
            args.putInt(Params.viewType, WebViewFragment.viewTypeTerms)

            webViewFragment.arguments = args
            webViewFragment.invoke(childFragmentManager)
            return@setOnPreferenceClickListener true
        }

        val librariesPref: Preference? = findPreference("licensesPref")
        librariesPref?.setOnPreferenceClickListener {
            args.putInt(Params.viewType, WebViewFragment.viewTypeLicense)

            webViewFragment.arguments = args
            webViewFragment.invoke(childFragmentManager)
            return@setOnPreferenceClickListener true
        }

        val supportPref: Preference? = findPreference("supportPref")
        supportPref?.setOnPreferenceClickListener {
            SupportFragment().invoke(childFragmentManager)
            return@setOnPreferenceClickListener true
        }

        val versionPref: Preference? = findPreference("versionPreference")
        versionPref?.summary = BuildConfig.VERSION_NAME
    }

    override fun onDetach() {
        super.onDetach()
        activity?.finish()
    }

}