package com.isaiahvonrundstedt.bucket.components.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.isaiahvonrundstedt.bucket.utils.converters.MetricConverter

class ItemDecoration (context: Context?): RecyclerView.ItemDecoration() {

    private var divider: Drawable? = null

    init {
        val styledAttributes = context?.obtainStyledAttributes(attrs)
        divider = styledAttributes?.getDrawable(0)
        styledAttributes?.recycle()
    }

    companion object { private val attrs = intArrayOf(android.R.attr.listDivider) }

}