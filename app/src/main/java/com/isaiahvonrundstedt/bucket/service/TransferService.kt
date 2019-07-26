package com.isaiahvonrundstedt.bucket.service

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.activities.MainActivity
import com.isaiahvonrundstedt.bucket.architecture.store.NotificationStore
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseService
import com.isaiahvonrundstedt.bucket.constants.Firestore
import com.isaiahvonrundstedt.bucket.objects.core.File
import com.isaiahvonrundstedt.bucket.utils.User
import com.isaiahvonrundstedt.bucket.utils.managers.ItemManager
import timber.log.Timber

class TransferService: BaseService() {

    private val notificationStore by lazy { NotificationStore(application)}
    private val firestore by lazy { FirebaseFirestore.getInstance() }
    private val storageReference by lazy { FirebaseStorage.getInstance().reference }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    companion object {
        const val actionFile = "action_file"
        const val actionProfile = "action_profile"
        const val statusCompleted = "status_completed"
        const val statusError = "status_error"

        const val extraFileURI = "extra_file_uri"
        const val extraAccountID = "extra_account_id"
        const val extraDownloadURL = "extra_download_url"

        val intentFilter: IntentFilter
            get() {
                val filter = IntentFilter()
                filter.addAction(statusCompleted)
                filter.addAction(statusError)
                return filter
            }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (actionFile == intent?.action){
            val fileUri: Uri = intent.getParcelableExtra(extraFileURI)
            initiateTransfer(fileUri)
        } else if (actionProfile == intent?.action){
            val fileUri: Uri = intent.getParcelableExtra(extraFileURI)
            val accountID: String? = intent.getStringExtra(extraAccountID)
            setProfile(accountID, fileUri)
        }
        return START_REDELIVER_INTENT
    }

    private fun initiateTransfer(fileUri: Uri){
        taskStarted()

        showProgressNotification(fileUri.lastPathSegment!!, 0, 0)

        // Get a reference to store a file at files reference
        val fileReference = storageReference.child(Firestore.files).child(fileUri.lastPathSegment!!)

        fileReference.putFile(fileUri)
            .addOnProgressListener { taskSnapshot ->
                showProgressNotification(fileUri.lastPathSegment!!,
                    taskSnapshot.bytesTransferred, taskSnapshot.totalByteCount)
            }.continueWithTask {  task ->
                // Forward any exceptions
                if (!task.isSuccessful)
                    Timber.e(task.exception)

                // Request the downloadURL
                fileReference.downloadUrl
            }.addOnSuccessListener { downloadUri ->
                // Broadcast whether the task is successful or not
                broadcastTransferFinished(downloadUri, fileUri)

                // Send a notification to the user
                showTransferFinishedNotification(downloadUri, fileUri)
                // Package the localCache for the location of the file in the server
                finalizeTransfer(downloadUri, fileUri)
            }.addOnFailureListener {
                // Broadcast whether the task is successful or not
                broadcastTransferFinished(null, fileUri)

                // Send a notification to the user
                showTransferFinishedNotification(null, fileUri)
                // Task is Completed
                taskCompleted()
            }
    }

    // Broadcast finished uploading (success or failure)
    // return true if a running receiver received the broadcast
    private fun broadcastTransferFinished(downloadURL: Uri?, fileURI: Uri?): Boolean {
        val success = downloadURL != null

        val action = if (success) statusCompleted else statusError
        val broadcast = Intent(action)
            .putExtra(extraDownloadURL, downloadURL)
            .putExtra(extraFileURI, fileURI)
        return LocalBroadcastManager.getInstance(applicationContext)
            .sendBroadcast(broadcast)
    }

    // Show a notification for a finished upload
    private fun showTransferFinishedNotification(downloadUri: Uri?, fileUri: Uri?){
        // Hide the progress notification
        dismissProgressNotification()

        // Make intent to MainActivity
        val intent = Intent(this, MainActivity::class.java)
            .putExtra(extraDownloadURL, downloadUri)
            .putExtra(extraFileURI, fileUri)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)

        val success = downloadUri != null
        val caption = if (success) getString(R.string.notification_file_upload_success) else getString(R.string.notification_file_upload_error)
        showFinishedNotification(caption, intent, success)
    }
    private fun finalizeTransfer(downloadUri: Uri?, selectedFileUri: Uri?) {
        val fileReference = firestore.collection(Firestore.files)

        if (downloadUri != null) {
            // Create a buffered file to retrieve data about the uri
            val bufferedFile: java.io.File = java.io.File(selectedFileUri?.path)

            val file = File()
            file.name = bufferedFile.nameWithoutExtension
            file.fileSize = bufferedFile.length().toDouble()
            file.downloadURL = downloadUri.toString()
            file.fileType = ItemManager.obtainFileExtension(bufferedFile.toUri())
            file.author = User(this).fullName
            file.timestamp = Timestamp.now()

            fileReference.add(file)
                .addOnCompleteListener {
                    if (it.isSuccessful)
                        taskCompleted()
                }
        } else
            Timber.i("Download URI is null")
    }
    // Show notification with a progress bar
    private fun showProgressNotification(fileName: String, completedUnits: Long, totalUnits: Long){
        val percentComplete: Int
        if (totalUnits > 0){
            percentComplete = (100 * completedUnits / totalUnits).toInt()

            createTransferChannel()
            val builder = NotificationCompat.Builder(this, getString(R.string.notification_channel_default))
                .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setSmallIcon(R.drawable.ic_upload)
                .setContentTitle(String.format(getString(R.string.notification_transferring), fileName))
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

        val icon = if (success) R.drawable.ic_checked else R.drawable.ic_error

        createTransferChannel()
        val builder = NotificationCompat.Builder(this, getString(R.string.notification_channel_transfer))
            .setSmallIcon(icon)
            .setContentTitle(caption)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        manager.notify(finishedNotificationID, builder.build())
    }

    private fun dismissProgressNotification(){
        manager.cancel(inProgressNotificationID)
    }

    private fun setProfile(accountID: String?, uri: Uri?){
        taskStarted()

        val reference = storageReference.child(Firestore.users).child(accountID!!)
        reference.putFile(uri!!)
            .continueWithTask { task ->
                if (!task.isSuccessful)
                    Timber.e(task.exception)

                reference.downloadUrl
            }.addOnSuccessListener { downloadUri ->
                firestore.collection(Firestore.files).document(accountID)
                    .update("imageURL", downloadUri.toString())
                    .addOnSuccessListener {
                        showTransferFinishedNotification(downloadUri, uri)
                    }
            }.addOnFailureListener {
                showTransferFinishedNotification(null, uri)
            }
    }

}