package com.isaiahvonrundstedt.bucket.service

import android.content.Intent
import android.os.IBinder
import com.google.firebase.firestore.FirebaseFirestore
import com.isaiahvonrundstedt.bucket.architecture.store.room.NotificationStore
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseService

class SupportService: BaseService() {

    private val notificationStore by lazy { NotificationStore(application) }
    private val firestore by lazy { FirebaseFirestore.getInstance() }
    private val repository by lazy { NotificationStore(application) }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    companion object {
        const val actionFetchPayload = "actionFetch"
        const val actionCheckLife = "actionCheck"
        const val actionSendFeedback = "actionSend"

        const val extraSupportItem = "extra_support_item"


        private const val newPackageNotificationID = 1
        private const val unsupportedNotificationID = 2
        private const val feedbackSentNotificationID = 3
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_REDELIVER_INTENT
    }

}