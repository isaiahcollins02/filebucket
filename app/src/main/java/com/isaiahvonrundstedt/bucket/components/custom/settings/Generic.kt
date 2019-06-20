package com.isaiahvonrundstedt.bucket.components.custom.settings

import android.content.Context
import android.util.AttributeSet
import androidx.preference.Preference
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.utils.Preferences

class Generic @JvmOverloads constructor(
                context: Context? = null,
                attrs: AttributeSet? = null,
                defStyle: Int = 0 ): Preference(context, attrs, defStyle) {

    private var currentTheme: String? = null

    init {
        layoutResource = R.layout.layout_settings_generic
        currentTheme = Preferences(context).theme
    }
}