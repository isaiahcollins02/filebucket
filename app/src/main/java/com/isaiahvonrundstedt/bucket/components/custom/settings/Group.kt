package com.isaiahvonrundstedt.bucket.components.custom.settings

import android.content.Context
import android.util.AttributeSet
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceViewHolder
import com.isaiahvonrundstedt.bucket.R

class Group @JvmOverloads constructor (
            context: Context? = null,
            attrs: AttributeSet? = null,
            defStyle: Int = 0): PreferenceCategory(context, attrs, defStyle) {

    init {
        layoutResource = R.layout.layout_settings_header
    }

    override fun onBindViewHolder(container: PreferenceViewHolder?) {
        super.onBindViewHolder(container)
        container?.itemView?.setBackgroundResource(R.color.colorPreference)
    }
}