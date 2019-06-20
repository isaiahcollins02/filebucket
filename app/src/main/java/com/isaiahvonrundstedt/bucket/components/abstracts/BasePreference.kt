package com.isaiahvonrundstedt.bucket.components.abstracts

import android.os.Bundle
import android.view.View
import androidx.preference.*
import androidx.recyclerview.widget.RecyclerView
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.utils.Preferences

abstract class BasePreference: PreferenceFragmentCompat() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        onHandleThemeChanges(view)
        super.onViewCreated(view, savedInstanceState)
    }

    private fun onHandleThemeChanges(view: View){
        val colorID = when (Preferences(view.context).theme){
            Preferences.THEME_LIGHT -> R.color.colorCardLight
            Preferences.THEME_DARK -> R.color.colorCardDark
            Preferences.THEME_AMOLED -> R.color.colorCardAMOLED
            else -> null
        }
        view.setBackgroundResource(colorID!!)
    }

}