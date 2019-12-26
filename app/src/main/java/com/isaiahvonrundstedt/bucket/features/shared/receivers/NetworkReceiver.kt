package com.isaiahvonrundstedt.bucket.features.shared.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.ConnectivityManager.TYPE_MOBILE
import android.net.ConnectivityManager.TYPE_WIFI
import android.net.NetworkCapabilities
import android.os.Build
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley


class NetworkReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val status = getConnectivityStatus(context)
        if (status != typeNotConnected){
            val request = StringRequest(Request.Method.GET, "http://captive.apple.com",
                Response.Listener<String> {
                    connectivityListener?.onNetworkChanged(status)
                },
                Response.ErrorListener {
                    connectivityListener?.onNetworkChanged(typeNotConnected)
                })
            Volley.newRequestQueue(context).add(request)
        } else
            connectivityListener?.onNetworkChanged(typeNotConnected)
    }

    interface ConnectivityListener {
        fun onNetworkChanged(status: Int)
    }

    companion object {
        var connectivityListener: ConnectivityListener? = null

        const val typeNotConnected = 0
        const val typeWiFi = 1
        const val typeCellular = 2

        @Suppress("DEPRECATION")
        fun getConnectivityStatus(context: Context?): Int {
            var status: Int = typeNotConnected

            val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)?.run {
                    status = when {
                        hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> typeCellular
                        hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> typeWiFi
                        else -> typeNotConnected
                    }
                }
            } else {
                connectivityManager.activeNetworkInfo?.run {
                    status = when (type) {
                        TYPE_WIFI -> typeWiFi
                        TYPE_MOBILE -> typeCellular
                        else -> typeNotConnected
                    }
                }
            }
            return status
        }
    }

}