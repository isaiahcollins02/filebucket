package com.isaiahvonrundstedt.bucket.service

import android.content.Intent
import android.os.IBinder
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseService
import com.isaiahvonrundstedt.bucket.constants.Firebase
import com.isaiahvonrundstedt.bucket.objects.Usage
import timber.log.Timber

class UsageService: BaseService() {

    private var firestore: FirebaseFirestore? = null

    companion object {
        const val extraObjectID = "objectID"
        const val extraAuthID = "authID"

        const val sendFileUsage = "sendFileUsage"
        const val sendBoxUsage = "sendBoxUsage"
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (firestore == null)
            firestore = FirebaseFirestore.getInstance()

        val authID: String? = intent?.getStringExtra(extraAuthID)
        val objectID: String? = intent?.getStringExtra(extraObjectID)

        if (intent?.action == sendFileUsage)
            sendFileUsage(objectID, authID)
        else if (intent?.action == sendBoxUsage)
            sendBoxUsage(objectID, authID)

        return START_REDELIVER_INTENT
    }
    private fun sendFileUsage(objectID: String?, authID: String?){
        val reference = firestore?.collection(Firebase.USERS.string)
            ?.document(authID!!)?.collection(Firebase.USAGE.string)

        val usageData = Usage(Timestamp.now(), objectID, Usage.fileType)
        reference?.add(usageData)
            ?.addOnSuccessListener {
                Timber.i("Usage Data Sent")
            }
            ?.addOnFailureListener {
                Timber.e(it.toString())
            }
    }
    private fun sendBoxUsage(objectID: String?, authID: String?){
        val reference = firestore?.collection(Firebase.USERS.string)
            ?.document(authID!!)?.collection(Firebase.USAGE.string)

        val usageData = Usage(Timestamp.now(), objectID, Usage.boxType)
        reference?.add(usageData)
            ?.addOnSuccessListener {
                Timber.i("Usage Data Sent")
            }
            ?.addOnFailureListener {
                Timber.e(it.toString())
            }
    }
}