package com.isaiahvonrundstedt.bucket.core.components.custom

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceViewHolder
import com.isaiahvonrundstedt.bucket.R

class AppSettingsHeader @JvmOverloads constructor (
            context: Context? = null,
            attrs: AttributeSet? = null,
            defStyle: Int = 0): PreferenceCategory(context, attrs, defStyle) {

    init {
        layoutResource = R.layout.layout_settings_header
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder?) {
        super.onBindViewHolder(holder)
        holder?.itemView?.setBackgroundColor(ContextCompat.getColor(this.context, R.color.colorGenericWindow))
    }

}