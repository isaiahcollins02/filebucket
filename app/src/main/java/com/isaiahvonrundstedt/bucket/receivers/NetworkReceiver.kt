package com.isaiahvonrundstedt.bucket.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.ConnectivityManager.TYPE_MOBILE
import android.net.NetworkInfo
import android.net.ConnectivityManager.TYPE_WIFI
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build


class NetworkReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val status = getConnectivityStatus(context)
        if (connectivityListener != null)
            connectivityListener!!.onNetworkChanged(status)
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