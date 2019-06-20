package com.isaiahvonrundstedt.bucket.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.isaiahvonrundstedt.bucket.service.NotificationService

class RestartReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        context?.startService(Intent(context, NotificationService::class.java))
    }

}