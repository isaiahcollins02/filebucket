package com.isaiahvonrundstedt.bucket.experience.fragments.preference

import android.content.Intent
import android.os.Bundle
import androidx.preference.Preference
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.core.components.abstracts.BasePreference
import com.isaiahvonrundstedt.bucket.experience.activities.generic.LibrariesActivity
import com.isaiahvonrundstedt.bucket.experience.activities.wrapper.WebViewActivity

class AboutFragment: BasePreference() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_about, rootKey)

        val policyPref: Preference? = findPreference("policyPref")
        policyPref?.setOnPreferenceClickListener {
            val intent = Intent(context!!, WebViewActivity::class.java)
            intent.putExtra("VIEW_TYPE", WebViewActivity.VIEW_TYPE_PRIVACY)
            startActivity(intent)
            return@setOnPreferenceClickListener true
        }

        val termsPref: Preference? = findPreference("termsPref")
        termsPref?.setOnPreferenceClickListener {
            val intent = Intent(context!!, WebViewActivity::class.java)
            intent.putExtra("VIEW_TYPE", WebViewActivity.VIEW_TYPE_TERMS)
            startActivity(intent)
            return@setOnPreferenceClickListener true
        }

        val librariesPref: Preference? = findPreference("openSourcePref")
        librariesPref?.setOnPreferenceClickListener {
            startActivity(Intent(context!!, LibrariesActivity::class.java))
            return@setOnPreferenceClickListener true
        }

    }

    override fun onDetach() {
        super.onDetach()
        activity?.finish()
    }

}