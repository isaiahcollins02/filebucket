package com.isaiahvonrundstedt.bucket.fragments.preference

import android.content.Intent
import android.os.Bundle
import android.webkit.WebView
import androidx.preference.Preference
import com.isaiahvonrundstedt.bucket.BuildConfig
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.activities.support.SupportActivity
import com.isaiahvonrundstedt.bucket.components.abstracts.BasePreference
import com.isaiahvonrundstedt.bucket.constants.Params
import com.isaiahvonrundstedt.bucket.fragments.screendialog.WebViewFragment

class AboutFragment: BasePreference() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_about, rootKey)

        val webViewFragment = WebViewFragment()
        val args = Bundle()

        val policyPref: Preference? = findPreference("policyPref")
        policyPref?.setOnPreferenceClickListener {
            if (childFragmentManager.findFragmentByTag(WebViewFragment.tag)?.isAdded != true){
                args.putInt(Params.viewType, WebViewFragment.viewTypePrivacy)

                webViewFragment.arguments = args
                webViewFragment.show(childFragmentManager, WebViewFragment.tag)
            }
            return@setOnPreferenceClickListener true
        }

        val termsPref: Preference? = findPreference("termsPref")
        termsPref?.setOnPreferenceClickListener {
            if (childFragmentManager.findFragmentByTag(WebViewFragment.tag)?.isAdded != true) {
                args.putInt(Params.viewType, WebViewFragment.viewTypeTerms)

                webViewFragment.arguments = args
                webViewFragment.show(childFragmentManager, WebViewFragment.tag)
            }
            return@setOnPreferenceClickListener true
        }

        val librariesPref: Preference? = findPreference("licensesPref")
        librariesPref?.setOnPreferenceClickListener {
            val bundle = Bundle()
            bundle.putInt(Params.viewType, WebViewFragment.viewTypeLicense)
            if (childFragmentManager.findFragmentByTag(WebViewFragment.tag)?.isAdded != true){
                args.putInt(Params.viewType, WebViewFragment.viewTypeLicense)

                webViewFragment.arguments = args
                webViewFragment.show(childFragmentManager, WebViewFragment.tag)
            }
            return@setOnPreferenceClickListener true
        }

        val supportPref: Preference? = findPreference("supportPref")
        supportPref?.setOnPreferenceClickListener {
            startActivity(Intent(context, SupportActivity::class.java))
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