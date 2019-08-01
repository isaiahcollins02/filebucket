package com.isaiahvonrundstedt.bucket.utils.converters

import android.content.Context
import android.util.DisplayMetrics

@Deprecated("")
object MetricConverter {

    fun convertDPtoPixel(context: Context, value: Float): Float {
        return (value * (context.resources.displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT))
    }
    fun convertPixeltoDP(context: Context, value: Float): Float {
        return (value / (context.resources.displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT))
    }

}