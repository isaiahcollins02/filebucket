package com.isaiahvonrundstedt.bucket.service

import android.app.PendingIntent
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.isaiahvonrundstedt.bucket.BuildConfig
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.activities.MainActivity
import com.isaiahvonrundstedt.bucket.architecture.store.NotificationStore
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseService
import com.isaiahvonrundstedt.bucket.constants.Firestore
import com.isaiahvonrundstedt.bucket.objects.core.Notification
import timber.log.Timber

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

        private const val newPackageNotificationID = 1
        private const val unsupportedNotificationID = 2
        private const val feedbackSentNotificationID = 3
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_REDELIVER_INTENT
    }

}