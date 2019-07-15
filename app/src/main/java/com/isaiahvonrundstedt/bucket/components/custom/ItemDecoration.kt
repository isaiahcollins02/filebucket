package com.isaiahvonrundstedt.bucket.components.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.isaiahvonrundstedt.bucket.utils.converters.MetricConverter

class ItemDecoration (context: Context?, var topDrawable: Boolean = true): RecyclerView.ItemDecoration() {

    private var divider: Drawable? = null

    init {
        val styledAttributes = context?.obtainStyledAttributes(attrs)
        divider = styledAttributes?.getDrawable(0)
        styledAttributes?.recycle()
    }

    companion object { private val attrs = intArrayOf(android.R.attr.listDivider) }

    override fun onDrawOver(canvas: Canvas, recyclerView: RecyclerView, state: RecyclerView.State) {
        if (topDrawable) {
            val dividerLeft = 0
            val dividerRight = recyclerView.width

            for (i in 0 until recyclerView.childCount){
                if (i == 0 || i == recyclerView.childCount - 1){
                    val childView: View = recyclerView.getChildAt(0)
                    val layoutParameters: RecyclerView.LayoutParams = childView.layoutParams as RecyclerView.LayoutParams

                    val dividerTop: Int = childView.top + layoutParameters.topMargin
                    val dividerBottom: Int = dividerTop + MetricConverter.convertDPtoPixel(recyclerView.context, 1.0F).toInt()

                    divider?.setBounds(dividerLeft, dividerTop, dividerRight, dividerBottom)
                    divider?.draw(canvas)
                }
            }
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
}