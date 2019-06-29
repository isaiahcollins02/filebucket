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
        const val defaultChannel = "default"
        const val transferChannel = "transfer"

        internal const val inProgressNotificationID = 0
        internal const val finishedNotificationID = 1
        internal const val availableNotificationID = 2

        const val actionDownload = "download"
        const val actionSave = "save"

        const val objectArgs = "objectArgs"
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
            val channel = NotificationChannel(defaultChannel, "Default", NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(channel)
        }
    }

    protected fun createTransferChannel(){
        // Since Android 0reo (API Level 26) is needed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(transferChannel, "Transfer", NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(channel)
        }
    }

}