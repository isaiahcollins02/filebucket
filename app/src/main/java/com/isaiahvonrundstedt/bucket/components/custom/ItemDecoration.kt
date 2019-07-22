package com.isaiahvonrundstedt.bucket.components.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.isaiahvonrundstedt.bucket.adapters.BaseAdapter
import com.isaiahvonrundstedt.bucket.adapters.core.PublicAdapter
import com.isaiahvonrundstedt.bucket.utils.converters.MetricConverter

class ItemDecoration (private var context: Context?): RecyclerView.ItemDecoration() {

    private var divider: Drawable? = null

    init {
        val styledAttributes = context?.obtainStyledAttributes(attrs)
        divider = styledAttributes?.getDrawable(0)
        styledAttributes?.recycle()
    }

    companion object { private val attrs = intArrayOf(android.R.attr.listDivider) }

    override fun onDrawOver(canvas: Canvas, recyclerView: RecyclerView, state: RecyclerView.State) {
        val dividerLeft = 0
        val dividerRight = recyclerView.width

        for (i in 0 until recyclerView.childCount){
            val childView: View = recyclerView.getChildAt(i)
            val layoutParameters: RecyclerView.LayoutParams = childView.layoutParams as RecyclerView.LayoutParams

            val dividerTop: Int = childView.top + layoutParameters.topMargin
            val dividerBottom: Int = dividerTop + MetricConverter.convertDPtoPixel(recyclerView.context, 1.0F).toInt()

            divider?.setBounds(dividerLeft, dividerTop, dividerRight, dividerBottom)
            divider?.draw(canvas)
        }
    }

    override fun onDraw(canvas: Canvas, recyclerView: RecyclerView, state: RecyclerView.State) {
        val dividerLeft = 0
        val dividerRight = recyclerView.width

        for (i in 0 until recyclerView.childCount){
            val childView: View = recyclerView.getChildAt(i)
            val layoutParameters: RecyclerView.LayoutParams = childView.layoutParams as RecyclerView.LayoutParams

            val dividerTop: Int =  childView.bottom + layoutParameters.bottomMargin
            val dividerBottom: Int = dividerTop + MetricConverter.convertDPtoPixel(recyclerView.context, 1.0F).toInt()

            divider?.setBounds(dividerLeft, dividerTop, dividerRight, dividerBottom)
            divider?.draw(canvas)
        }
    }

    override fun getItemOffsets(outRect: Rect, view: View, recyclerView: RecyclerView, state: RecyclerView.State) {
        val itemPosition = recyclerView.getChildAdapterPosition(view)
        val viewType = recyclerView.adapter?.getItemViewType(itemPosition)
        val marginOffsets = MetricConverter.convertDPtoPixel(context!!, 16F).toInt()
        if (viewType == BaseAdapter.viewTypeImage)
            outRect.set(0, marginOffsets, 0, marginOffsets)
        else
            outRect.setEmpty()
    }
}