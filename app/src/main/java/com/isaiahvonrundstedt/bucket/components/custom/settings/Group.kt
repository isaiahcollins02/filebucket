package com.isaiahvonrundstedt.bucket.components.custom.settings

import android.content.Context
import android.util.AttributeSet
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceViewHolder
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.utils.Preferences

class Group @JvmOverloads constructor (
            context: Context? = null,
            attrs: AttributeSet? = null,
            defStyle: Int = 0): PreferenceCategory(context, attrs, defStyle) {

    init {
        layoutResource = R.layout.layout_settings_header
    }

    override fun onBindViewHolder(container: PreferenceViewHolder?) {
        super.onBindViewHolder(container)
        val colorID = when (Preferences(context).theme){
            Preferences.THEME_LIGHT -> R.color.colorPreferenceLight
            Preferences.THEME_DARK -> R.color.colorPreferenceDark
            Preferences.THEME_AMOLED -> R.color.colorPreferenceAMOLED
            else -> null
        }
        container?.itemView?.setBackgroundResource(colorID!!)
    }
}