package com.isaiahvonrundstedt.bucket.fragments.preference

import android.content.Intent
import android.os.Bundle
import androidx.preference.Preference
import com.afollestad.materialdialogs.MaterialDialog
import com.isaiahvonrundstedt.bucket.BuildConfig
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.activities.generic.LibrariesActivity
import com.isaiahvonrundstedt.bucket.activities.wrapper.WebViewActivity
import com.isaiahvonrundstedt.bucket.components.abstracts.BasePreference

class AboutFragment: BasePreference() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_about, rootKey)

        val policyPref: Preference? = findPreference("policyPref")
        policyPref?.setOnPreferenceClickListener {
            val intent = Intent(context!!, WebViewActivity::class.java)
            intent.putExtra("viewType", WebViewActivity.viewTypePrivacy)
            startActivity(intent)
            return@setOnPreferenceClickListener true
        }

        val termsPref: Preference? = findPreference("termsPref")
        termsPref?.setOnPreferenceClickListener {
            val intent = Intent(context!!, WebViewActivity::class.java)
            intent.putExtra("viewType", WebViewActivity.viewTypeTerms)
            startActivity(intent)
            return@setOnPreferenceClickListener true
        }

        val librariesPref: Preference? = findPreference("openSourcePref")
        librariesPref?.setOnPreferenceClickListener {
            startActivity(Intent(context!!, LibrariesActivity::class.java))
            return@setOnPreferenceClickListener true
        }

        var count = 0

        val versionPref: Preference? = findPreference("versionPreference")
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

    override fun onDetach() {
        super.onDetach()
        activity?.finish()
    }

}