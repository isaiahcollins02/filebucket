package com.isaiahvonrundstedt.bucket.components.custom.settings

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.preference.PreferenceViewHolder
import androidx.preference.SwitchPreference
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.utils.Preferences

@SuppressLint("PrivateResource")
class Switch @JvmOverloads constructor(
                    context: Context? = null,
                    attrs: AttributeSet? = null,
                    defStyle: Int = 0): SwitchPreference(context, attrs, defStyle) {

    private var currentTheme: String? = null

    init {
        currentTheme = Preferences(context).theme
        layoutResource = R.layout.layout_settings_base
        widgetLayoutResource = androidx.preference.R.layout.preference_widget_switch
    }
    override fun onBindViewHolder(container: PreferenceViewHolder?) {
        super.onBindViewHolder(container)
        container?.itemView?.setBackgroundResource(R.color.colorCardBackground)
    }
}