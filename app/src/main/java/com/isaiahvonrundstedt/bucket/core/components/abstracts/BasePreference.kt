package com.isaiahvonrundstedt.bucket.core.components.abstracts

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.preference.*
import androidx.recyclerview.widget.RecyclerView
import com.isaiahvonrundstedt.bucket.R

abstract class BasePreference: PreferenceFragmentCompat() {

    @SuppressLint("RestrictedApi")
    override fun onCreateAdapter(preferenceScreen: PreferenceScreen?): RecyclerView.Adapter<*> {
        return object : PreferenceGroupAdapter(preferenceScreen) {
            override fun onBindViewHolder(holder: PreferenceViewHolder, position: Int) {
                super.onBindViewHolder(holder, position)

                val preference: Preference = getItem(position)
                if (preference is PreferenceCategory)
                    setZeroPaddingToLayoutChildren(holder.itemView)
                else
                    holder.itemView.findViewById<View?>(R.id.icon_frame)?.visibility = if (preference.icon == null) View.GONE else View.VISIBLE
            }
        }
    }

    private fun setZeroPaddingToLayoutChildren(view: View){
        if (view !is ViewGroup)
            return
        val childCount: Int = view.childCount
        for (i in 0 until childCount){
            setZeroPaddingToLayoutChildren(view.getChildAt(i))
            view.setPaddingRelative(0, view.paddingTop, view.paddingEnd, view.paddingBottom)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDivider(ColorDrawable(Color.TRANSPARENT))
        setDividerHeight(0)
    }

}