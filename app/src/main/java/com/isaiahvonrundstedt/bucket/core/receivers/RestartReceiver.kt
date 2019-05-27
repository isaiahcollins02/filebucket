package com.isaiahvonrundstedt.bucket.core.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.isaiahvonrundstedt.bucket.core.service.ListenerService

class RestartReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        context?.startService(Intent(context, ListenerService::class.java))
    }

}