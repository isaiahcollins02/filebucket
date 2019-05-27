package com.isaiahvonrundstedt.bucket.core.components.experience.decorations

import android.content.Context
import android.graphics.Canvas
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.isaiahvonrundstedt.bucket.core.utils.managers.Metrics

class RepoItemDecoration(val context: Context): BaseItemDecoration(context) {

    override fun onDraw(canvas: Canvas, container: RecyclerView, state: RecyclerView.State) {
        val dividerLeft = Metrics.convertDPtoPixel(container.context, 72.0F).toInt()
        val dividerRight = container.width

        for (i in 0 until container.childCount){
            val childView: View = container.getChildAt(i)
            val layoutParams: RecyclerView.LayoutParams = childView.layoutParams as RecyclerView.LayoutParams

            val dividerTop = childView.bottom + layoutParams.bottomMargin
            val dividerBottom = dividerTop + divider!!.intrinsicHeight

            divider!!.setBounds(dividerLeft, dividerTop, dividerRight, dividerBottom)
            divider!!.draw(canvas)
        }
    }

}