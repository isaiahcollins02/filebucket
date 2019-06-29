package com.isaiahvonrundstedt.bucket.components.abstracts

import android.os.Bundle
import android.view.View
import androidx.preference.PreferenceFragmentCompat
import com.isaiahvonrundstedt.bucket.R

abstract class BasePreference: PreferenceFragmentCompat() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        onHandleThemeChanges(view)
        super.onViewCreated(view, savedInstanceState)
    }

    private fun onHandleThemeChanges(view: View){
        view.setBackgroundResource(R.color.colorPreference)
    }

}