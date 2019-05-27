package com.isaiahvonrundstedt.bucket.core.components.experience.decorations

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.RecyclerView

abstract class BaseItemDecoration(context: Context): RecyclerView.ItemDecoration() {

    internal var divider: Drawable? = null

    init {
        val attrib = context.obtainStyledAttributes(attrs)
        divider = attrib.getDrawable(0)
        attrib.recycle()

    }

    companion object {
        private var attrs = intArrayOf(android.R.attr.listDivider)
    }

}