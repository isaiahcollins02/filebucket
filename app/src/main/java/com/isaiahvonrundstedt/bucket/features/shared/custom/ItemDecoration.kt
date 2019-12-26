package com.isaiahvonrundstedt.bucket.features.shared.custom

import android.content.Context
import android.graphics.Rect
import android.util.TypedValue
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.isaiahvonrundstedt.bucket.features.shared.abstracts.BaseAdapter

class ItemDecoration (private var context: Context?): DividerItemDecoration(context, VERTICAL) {

    private fun convert(value: Float): Int
        = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, context?.resources?.displayMetrics).toInt()

    override fun getItemOffsets(outRect: Rect, view: View, recyclerView: RecyclerView, state: RecyclerView.State) {
        val offset = convert(16F)

        val itemPosition: Int = recyclerView.getChildAdapterPosition(view)
        val viewType: Int? = recyclerView.adapter?.getItemViewType(itemPosition)

        if (viewType == BaseAdapter.viewTypeImage)
            outRect.set(0, offset, 0, offset)
        else
            outRect.setEmpty()
    }

}