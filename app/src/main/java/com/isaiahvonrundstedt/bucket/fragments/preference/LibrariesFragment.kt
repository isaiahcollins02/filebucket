package com.isaiahvonrundstedt.bucket.fragments.preference

import android.os.Bundle
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.components.abstracts.BasePreference

class LibrariesFragment: BasePreference() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_libraries, rootKey)
    }

    override fun onDetach() {
        super.onDetach()
        activity?.finish()
    }
}