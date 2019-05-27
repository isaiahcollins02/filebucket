package com.isaiahvonrundstedt.bucket.core.components.experience.decorations

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.isaiahvonrundstedt.bucket.core.utils.managers.Metrics

class CoreItemDecoration (context: Context, private var status: Boolean): BaseItemDecoration(context) {

    private var itemOffSet: Int = 0

    init {
        itemOffSet = Metrics.convertDPtoPixel(context, 12.0F).toInt()
    }

    override fun onDraw(canvas: Canvas, container: RecyclerView, state: RecyclerView.State) {
        if (status) {
            val dividerLeft: Int = 0
            val dividerRight: Int = container.width

            for (i in 0 until container.childCount){
                val childView: View = container.getChildAt(i)
                val layoutParameters: RecyclerView.LayoutParams = childView.layoutParams as RecyclerView.LayoutParams

                val dividerTop: Int = childView.bottom + layoutParameters.bottomMargin
                val dividerBottom: Int = dividerTop + divider!!.intrinsicHeight

                divider!!.setBounds(dividerLeft, dividerTop, dividerRight, dividerBottom)
                divider!!.draw(canvas)
            }
        }
    }

    override fun onDrawOver(canvas: Canvas, container: RecyclerView, state: RecyclerView.State) {
        if (status) {
            val dividerLeft: Int = 0
            val dividerRight: Int = container.width

            for (i in 0 until container.childCount){
                val childView: View = container.getChildAt(i)
                val layoutParameters: RecyclerView.LayoutParams = childView.layoutParams as RecyclerView.LayoutParams

                val dividerTop: Int = childView.top + layoutParameters.topMargin
                val dividerBottom: Int = dividerTop + divider!!.intrinsicHeight

                divider!!.setBounds(dividerLeft, dividerTop, dividerRight, dividerBottom)
                divider!!.draw(canvas)
            }
        }
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        with(outRect){
            if (parent.getChildAdapterPosition(view) == 0){
                top = itemOffSet / 2
            }
            bottom = itemOffSet
        }
    }

}