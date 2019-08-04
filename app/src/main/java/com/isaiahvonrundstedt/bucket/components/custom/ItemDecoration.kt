package com.isaiahvonrundstedt.bucket.components.custom

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import androidx.recyclerview.widget.RecyclerView

class ItemDecoration (private var context: Context?): RecyclerView.ItemDecoration() {

    private var divider: Drawable? = null

    init {
        val styledAttributes = context?.obtainStyledAttributes(attrs)
        divider = styledAttributes?.getDrawable(0)
        styledAttributes?.recycle()
    }

    companion object { private val attrs = intArrayOf(android.R.attr.listDivider) }

    fun convertToPixel(size: Int): Int = (size * (context?.resources?.displayMetrics!!.densityDpi / DisplayMetrics.DENSITY_DEFAULT))

}