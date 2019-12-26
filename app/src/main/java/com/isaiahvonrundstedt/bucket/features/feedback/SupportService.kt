package com.isaiahvonrundstedt.bucket.features.feedback

import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.firestore.FirebaseFirestore
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.features.notifications.NotificationStore
import com.isaiahvonrundstedt.bucket.features.shared.abstracts.BaseService
import com.isaiahvonrundstedt.bucket.constants.Firestore
import com.isaiahvonrundstedt.bucket.features.notifications.Notification
import com.isaiahvonrundstedt.bucket.features.shared.StorageItem
import timber.log.Timber

class SupportService: BaseService() {

    private val notificationStore by lazy { NotificationStore(application) }
    private val firestore by lazy { FirebaseFirestore.getInstance() }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    companion object {
        const val actionFetchPayload = "actionFetch"
        const val actionSendFeedback = "actionSend"

        const val extraSupportItem = "extra_support_item"
        const val extraActionTaken = "extra_action_taken"

        const val statusCompleted = "status_completed"
        const val statusFailure = "status_failure"

        private const val newPackageNotificationID = 1
        private const val feedbackSentNotificationID = 2

        val intentFilter: IntentFilter
            get(){
                val filter = IntentFilter()
                filter.addAction(actionFetchPayload)
                filter.addAction(actionSendFeedback)
                return filter
            }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == actionSendFeedback){
            val support = intent.getParcelableExtra<Feedback>(extraSupportItem)
            sendFeedback(support)
        } else if (intent?.action == actionFetchPayload)
            checkPackages()
        return START_REDELIVER_INTENT
    }

    private fun sendFeedback(feedback: Feedback){
        firestore.collection(Firestore.feedback)
            .add(feedback)
            .addOnSuccessListener {

                sentFeedbackNotification()
                broadcastTaskFinished(true, actionSendFeedback)
            }
            .addOnFailureListener {
                broadcastTaskFinished(false, actionSendFeedback)
            }
    }

    private fun checkPackages(){
        firestore.collection(Firestore.Support.core).document(Firestore.Support.packages).get()
            .addOnSuccessListener { snapshot ->
                if (snapshot != null && snapshot.exists()){
                    val item: StorageItem? = snapshot.toObject(StorageItem::class.java)

                    sendNewPackageNotification(item)
                    broadcastTaskFinished(true, actionFetchPayload)
                } else
                    Timber.i("No Updates")
            }
            .addOnFailureListener {
                broadcastTaskFinished(false, actionFetchPayload)
            }
    }

    private fun sentFeedbackNotification(){
        val notification = Notification().apply {
            title = getString(R.string.notification_support_feedback_sent_title)
            content = getString(R.string.notification_support_feedback_sent_content)
            type = Notification.typeGeneric
        }

        notificationStore.insert(notification)
        createDefaultChannel()

        val builder = NotificationCompat.Builder(this, getString(R.string.notification_channel_default))
            .setContentTitle(notification.title)
            .setContentText(notification.content)
            .setSmallIcon(R.drawable.ic_bell)
            .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
            .setAutoCancel(false)

        manager.notify(feedbackSentNotificationID, builder.build())
    }

    private fun sendNewPackageNotification(item: StorageItem?){
        val notification = Notification().apply {
            title = getString(R.string.notification_update_available_title)
            content = getString(R.string.notification_update_available_content)
            objectID = item?.id
            objectArgs = item?.args
            type = Notification.typePackage
        }

        createDefaultChannel()

        val builder = NotificationCompat.Builder(this, getString(R.string.notification_channel_default))
            .setContentTitle(notification.title)
            .setContentText(notification.content)
            .setSmallIcon(R.drawable.ic_checked)
            .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
            .setAutoCancel(true)

        manager.notify(newPackageNotificationID, builder.build())
    }

    private fun broadcastTaskFinished(status: Boolean, task: String): Boolean {
        val action = if (status) statusCompleted else statusFailure
        val broadcast = Intent(action)
            .putExtra(extraActionTaken, task)
        return LocalBroadcastManager.getInstance(applicationContext)
            .sendBroadcast(broadcast)
    }

}