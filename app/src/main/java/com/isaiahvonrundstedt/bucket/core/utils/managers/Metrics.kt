package com.isaiahvonrundstedt.bucket.core.utils.managers

import android.content.Context
import android.util.DisplayMetrics

object Metrics {

    fun convertDPtoPixel(context: Context, value: Float): Float {
        return (value * (context.resources.displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT))
    }
    fun convertPixeltoDP(context: Context, value: Float): Float {
        return (value / (context.resources.displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT))
    }
}