package com.isaiahvonrundstedt.bucket.components.custom.settings

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.preference.CheckBoxPreference
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.utils.Preferences

@SuppressLint("PrivateResource")
class Check @JvmOverloads constructor(
                context: Context? = null,
                attributeSet: AttributeSet? = null,
                defStyle: Int = 0): CheckBoxPreference(context, attributeSet, defStyle) {

    private var currentTheme: String? = null

    init {
        currentTheme = Preferences(context).theme
        layoutResource = R.layout.layout_settings_base
        widgetLayoutResource = androidx.preference.R.layout.preference_widget_checkbox
    }

}