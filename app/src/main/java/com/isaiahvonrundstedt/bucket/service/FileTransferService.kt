package com.isaiahvonrundstedt.bucket.service

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.activities.MainActivity
import com.isaiahvonrundstedt.bucket.constants.Firebase
import com.isaiahvonrundstedt.bucket.objects.File
import com.isaiahvonrundstedt.bucket.utils.Account
import com.isaiahvonrundstedt.bucket.utils.managers.ItemManager

class FileTransferService: BaseService() {

    private lateinit var storageReference: StorageReference
    private lateinit var firestore: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate() {
        super.onCreate()

        storageReference = FirebaseStorage.getInstance().reference
        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    companion object {
        const val actionUpload = "action_upload"
        const val actionCompleted = "upload_completed"

        const val uploadError = "upload_error"
        const val uploadType = "upload_type"

        const val extraFileURI = "extra_file_uri"
        const val extraDownloadURL = "extra_download_url"

        val intentFilter: IntentFilter
            get() {
                val filter = IntentFilter()
                filter.addAction(actionCompleted)
                filter.addAction(uploadError)
                return filter
            }

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (actionUpload == intent?.action){
            val fileUri = intent.getParcelableExtra<Uri>(extraFileURI)

            uploadFile(fileUri)
        }

        return START_REDELIVER_INTENT
    }
    private fun uploadFile(fileUri: Uri){
        taskStarted()

        showProgressNotification(fileUri.lastPathSegment!!, 0, 0)

        // Get a reference to store a file at files reference
        val fileReference = storageReference.child(Firebase.FILES.string).child(fileUri.lastPathSegment!!)

        fileReference.putFile(fileUri)
            .addOnProgressListener { taskSnapshot ->
                showProgressNotification(getString(R.string.notification_transferring),
                    taskSnapshot.bytesTransferred, taskSnapshot.totalByteCount)
            }.continueWithTask {  task ->
                // Forward any exceptions
                if (!task.isSuccessful)
                    throw task.exception!!

                // Request the downloadURL
                fileReference.downloadUrl
            }.addOnSuccessListener { downloadUri ->
                // Broadcast whether the task is successful or not
                broadcastUploadFinished(downloadUri, fileUri)

                // Send a notification to the user
                showUploadFinishedNotification(downloadUri, fileUri)
                // Package the localCache for the location of the file in the server
                finalizeTransfer(downloadUri, fileUri)
            }.addOnFailureListener {
                // Broadcast whether the task is successful or not
                broadcastUploadFinished(null, fileUri)

                // Send a notification to the user
                showUploadFinishedNotification(null, fileUri)
                // Task is Completed
                taskCompleted()
            }
    }

    // Broadcast finished uploading (success or failure)
    // return true if a running receiver received the broadcast
    private fun broadcastUploadFinished(downloadURL: Uri?, fileURI: Uri?): Boolean {
        val success = downloadURL != null

        val action = if (success) actionCompleted else uploadError
        val broadcast = Intent(action)
            .putExtra(extraDownloadURL, downloadURL)
            .putExtra(extraFileURI, fileURI)
        return LocalBroadcastManager.getInstance(applicationContext)
            .sendBroadcast(broadcast)
    }

    // Show a notification for a finished upload
    private fun showUploadFinishedNotification(downloadUri: Uri?, fileUri: Uri?){
        // Hide the progress notification
        dismissProgressNotification()

        // Make intent to MainActivity
        val intent = Intent(this, MainActivity::class.java)
            .putExtra(extraDownloadURL, downloadUri)
            .putExtra(extraFileURI, fileUri)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)

        val success = downloadUri != null
        val caption = if (success) getString(R.string.file_upload_success) else getString(R.string.file_upload_error)
        showFinishedNotification(caption, intent, success)
    }
    private fun finalizeTransfer(downloadUri: Uri?, selectedFileUri: Uri?) {
        val fileReference = firestore.collection(Firebase.FILES.string)

        if (downloadUri != null) {
            // Create a buffered file to retrieve data about the uri
            val bufferedFile: java.io.File = java.io.File(selectedFileUri?.path)

            val file = File()
            file.name = bufferedFile.name
            file.fileSize = bufferedFile.length().toDouble()
            file.downloadURL = downloadUri.toString()
            file.fileType = ItemManager.obtainFileExtension(selectedFileUri!!)
            file.author = Account(this@FileTransferService).fullName
            file.timestamp = Timestamp.now()

            fileReference.add(file)
                .addOnCompleteListener {
                    if (it.isSuccessful)
                        taskCompleted()
                }
        } else
            Log.i("DataError", "Download URI is null")
    }
    // Show notification with a progress bar
    private fun showProgressNotification(fileName: String, completedUnits: Long, totalUnits: Long){
        val percentComplete: Int
        if (totalUnits > 0){
            percentComplete = (100 * completedUnits / totalUnits).toInt()

            createDefaultChannel()

            val builder = NotificationCompat.Builder(this, defaultChannel)
                .setColor(ContextCompat.getColor(this, R.color.colorDefault))
                .setSmallIcon(R.drawable.ic_vector_upload)
                .setContentTitle(String.format(getString(R.string.notification_percent_complete), fileName))
                .setContentText(String.format(getString(R.string.notification_percent_complete), percentComplete))
                .setProgress(100, percentComplete, false)
                .setOngoing(true)
                .setAutoCancel(false)

            manager.notify(inProgressNotificationID, builder.build())
        }
    }

    private fun showFinishedNotification(caption: String, intent: Intent, success: Boolean){
        // Make pending intent for notification
        val pendingIntent = PendingIntent.getActivity(this, 0
            /* requestCode */, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val icon = if (success) R.drawable.ic_vector_check else R.drawable.ic_vector_error

        createDefaultChannel()
        val builder = NotificationCompat.Builder(this, defaultChannel)
            .setSmallIcon(icon)
            .setContentTitle(caption)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        manager.notify(finishedNotificationID, builder.build())
    }

    private fun dismissProgressNotification(){
        manager.cancel(inProgressNotificationID)
    }
}