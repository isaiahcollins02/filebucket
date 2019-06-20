package com.isaiahvonrundstedt.bucket.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.os.Build

abstract class BaseService: Service(){

    private var numTasks = 0

    protected val manager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    fun taskStarted(){
        changeNumberOfTasks(1)
    }

    fun taskCompleted(){
        changeNumberOfTasks(-1)
    }

    companion object {
        const val CHANNEL_ID_DEFAULT = "default"

        internal const val PROGRESS_NOTIFICATION_ID = 0
        internal const val FINISHED_NOTIFICATION_ID = 1
        internal const val AVAILABLE_NOTIFICATION_ID = 2

        const val ACTION_DOWNLOAD = "download"
        const val ACTION_SAVE = "save"

        const val BUNDLE_ARGS = "objectArgs"
    }

    @Synchronized
    private fun changeNumberOfTasks(delta: Int){
        numTasks += delta

        if (numTasks <= 0){
            stopSelf()
        }
    }

    protected fun createDefaultChannel(){
        // Since Android 0reo (API Level 26) is needed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(CHANNEL_ID_DEFAULT, "Default", NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(channel)
        }
    }

}