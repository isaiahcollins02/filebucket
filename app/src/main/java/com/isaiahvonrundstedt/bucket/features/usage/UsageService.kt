package com.isaiahvonrundstedt.bucket.features.usage

import android.content.Intent
import android.os.IBinder
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.isaiahvonrundstedt.bucket.features.shared.abstracts.BaseService
import com.isaiahvonrundstedt.bucket.constants.Firestore
import timber.log.Timber

class UsageService: BaseService() {

    private val firestore by lazy { FirebaseFirestore.getInstance() }

    companion object {
        const val extraObjectID = "objectID"

        const val sendFileUsage = "sendFileUsage"
        const val sendBoxUsage = "sendBoxUsage"
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val authID: String? = FirebaseAuth.getInstance().currentUser?.uid
        val objectID: String? = intent?.getStringExtra(extraObjectID)

        if (intent?.action == sendFileUsage)
            sendFileUsage(objectID, authID)
        else if (intent?.action == sendBoxUsage)
            sendBoxUsage(objectID, authID)

        return START_REDELIVER_INTENT
    }
    private fun sendFileUsage(objectID: String?, authID: String?){
        taskStarted()
        val reference = firestore.collection(Firestore.users).document(authID!!).collection(Firestore.usage)

        val usageData = Usage(Timestamp.now(), objectID, Usage.fileType)
        reference.add(usageData)
            .addOnCompleteListener {
                taskCompleted()
            }
            .addOnSuccessListener {
                Timber.i("Usage Data Sent")
            }
            .addOnFailureListener {
                Timber.e(it.toString())
            }
    }
    private fun sendBoxUsage(objectID: String?, authID: String?){
        taskStarted()
        val reference = firestore.collection(Firestore.users).document(authID!!).collection(Firestore.usage)

        val usageData = Usage(Timestamp.now(), objectID, Usage.boxType)
        reference.add(usageData)
            .addOnCompleteListener {
                taskCompleted()
            }
            .addOnSuccessListener {
                Timber.i("Usage Data Sent")
            }
            .addOnFailureListener {
                Timber.e(it.toString())
            }
    }
}