package com.isaiahvonrundstedt.bucket.core.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.core.constants.Firebase
import com.isaiahvonrundstedt.bucket.core.objects.File
import com.isaiahvonrundstedt.bucket.core.utils.Preferences
import com.isaiahvonrundstedt.bucket.core.utils.managers.DataManager

class ListenerService: Service() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth

    private val manager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    companion object {
        private const val ACTION_PATH = "com.isaiahvonrundstedt.bucket.core.receivers.RestartReceiver"
        private const val CHANNEL_ID_DEFAULT = "default"

        internal const val NEW_FILE_NOTIFICATION_ID = 0
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val filesReference = firestore.collection(Firebase.FILES.string)
        filesReference.addSnapshotListener { querySnapshot, exception ->
            if (exception == null){
                for (documentSnapshot in querySnapshot!!.documents){
                    val file = documentSnapshot.toObject(File::class.java)
                    if (Preferences(this@ListenerService).fileNotification)
                        showNewFileNotification(file?.author, file?.name)
                }
            } else
                Log.e("FirebaseException", exception.toString())
        }
        return START_STICKY_COMPATIBILITY
    }

    override fun onCreate() {
        super.onCreate()

        firestore = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
    }

    override fun onDestroy() {
        super.onDestroy()

        // Send a message to the broadcast receiver if the service is being destroyed
        val broadcastIntent = Intent(ACTION_PATH)
        sendBroadcast(broadcastIntent)
    }

    private fun createDefaultChannel(){
        // Since Android 0reo (API Level 26) is needed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(BaseService.CHANNEL_ID_DEFAULT, "Default", NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(channel)
        }
    }

    private fun showNewFileNotification(fileAuthor: String?, name: String?){
        createDefaultChannel()

        val author: String = DataManager.sliceFullName(fileAuthor!!)
        val contentText: String = String.format(getString(R.string.notification_new_file_content), author, name)

        val builder = NotificationCompat.Builder(this, CHANNEL_ID_DEFAULT)
            .setSmallIcon(R.drawable.ic_vector_new)
            .setContentTitle(getString(R.string.notification_new_file_title))
            .setContentText(contentText)

        manager.notify(NEW_FILE_NOTIFICATION_ID, builder.build())
    }

}