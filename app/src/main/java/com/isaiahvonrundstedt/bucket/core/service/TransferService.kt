package com.isaiahvonrundstedt.bucket.core.service

import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.IBinder
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.core.constants.Firebase
import com.isaiahvonrundstedt.bucket.core.objects.File
import com.isaiahvonrundstedt.bucket.core.utils.Client
import com.isaiahvonrundstedt.bucket.core.utils.managers.ItemManager
import com.isaiahvonrundstedt.bucket.experience.activities.MainActivity

class TransferService: BaseService() {

    private var actionType: String? = null

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
        const val ACTION_UPLOAD = "action_upload"
        const val UPLOAD_COMPLETED = "upload_completed"
        const val UPLOAD_ERROR = "upload_error"
        const val UPLOAD_TYPE = "upload_type"

        const val EXTRA_FILE_URI = "extra_file_uri"
        const val EXTRA_DOWNLOAD_URL = "extra_download_url"

        const val TYPE_FILE = "file"
        const val TYPE_PROFILE = "profile"

        val intentFilter: IntentFilter
            get() {
                val filter = IntentFilter()
                filter.addAction(UPLOAD_COMPLETED)
                filter.addAction(UPLOAD_ERROR)
                return filter
            }

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (ACTION_UPLOAD == intent?.action){
            val fileUri = intent.getParcelableExtra<Uri>(EXTRA_FILE_URI)

            actionType = intent.getStringExtra(UPLOAD_TYPE)
            if (actionType == TYPE_FILE)
                uploadFile(fileUri)
            else if (actionType == TYPE_PROFILE)
                uploadProfile(fileUri)
        }

        return START_REDELIVER_INTENT
    }

    private fun uploadProfile(fileUri: Uri){
        taskStarted()

        showProgressNotification(getString(R.string.notification_updating_profile), 0, 0)

        val profileReference = storageReference.child(Firebase.PROFILES.string).child(fileUri.lastPathSegment!!)

        profileReference.putFile(fileUri)
            .addOnProgressListener {
                showProgressNotification(getString(R.string.notification_updating_profile)
                    , it.bytesTransferred, it.totalByteCount)
            }
            .continueWithTask {
                if (!it.isSuccessful)
                    throw it.exception!!

                // Request downloadURL of file
                profileReference.downloadUrl
            }.addOnSuccessListener { downloadUri ->

                // Broadcast whether the task is successful or not
                broadcastUploadFinished(downloadUri, fileUri)

                // Send a notification to the user
                showUploadFinishedNotification(downloadUri, fileUri)

                // Package the localCache for the location of the file in the server
                finalizeProfileUpload(downloadUri)
            }.addOnFailureListener {

                // Broadcast whether the task is successful or not
                broadcastUploadFinished(null, fileUri)

                // Send a notification to the user
                showUploadFinishedNotification(null, fileUri)

                // Task is Completed
                taskCompleted()
            }
    }

    private fun uploadFile(fileUri: Uri){
        taskStarted()

        showProgressNotification(getString(R.string.status_file_uploading), 0, 0)

        // Get a reference to store a file at files reference
        val fileReference = storageReference.child(Firebase.FILES.string).child(fileUri.lastPathSegment!!)

        fileReference.putFile(fileUri)
            .addOnProgressListener { taskSnapshot ->
                showProgressNotification(getString(R.string.status_file_uploading),
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
                finalizeFileUpload(downloadUri, fileUri)
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

        val action = if (success) UPLOAD_COMPLETED else UPLOAD_ERROR
        val broadcast = Intent(action)
            .putExtra(EXTRA_DOWNLOAD_URL, downloadURL)
            .putExtra(EXTRA_FILE_URI, fileURI)
        return LocalBroadcastManager.getInstance(applicationContext)
            .sendBroadcast(broadcast)
    }

    // Show a notification for a finished upload
    private fun showUploadFinishedNotification(downloadUri: Uri?, fileUri: Uri?){
        // Hide the progress notification
        dismissProgressNotification()

        // Make intent to MainActivity
        val intent = Intent(this, MainActivity::class.java)
            .putExtra(EXTRA_DOWNLOAD_URL, downloadUri)
            .putExtra(EXTRA_FILE_URI, fileUri)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)

        val success = downloadUri != null
        val caption = if (success) getString(R.string.file_upload_success) else getString(R.string.file_upload_error)
        showFinishedNotification(caption, intent, success)
    }
    private fun finalizeProfileUpload(downloadUri: Uri?) {
        val profileReference = firestore.collection(Firebase.USERS.string)

        val userID: String? = firebaseAuth.currentUser?.uid

        if (downloadUri != null && userID != null) {
            Thread().run {

                val map: HashMap<String, Any?> = HashMap()
                map["imageURL"] = downloadUri.toString()

                profileReference.document(userID).update(map)
                    .addOnCompleteListener {
                        if (it.isSuccessful)
                            taskCompleted()
                    }
                // Operates on new thread
            }
        } else
            Log.i("DataError", "Download URI is null")
    }
    private fun finalizeFileUpload(downloadUri: Uri?, selectedFileUri: Uri?) {
        val fileReference = firestore.collection(Firebase.FILES.string)

        if (downloadUri != null) {
            Thread().run {
                // Create a buffered file to retrieve data about the uri
                val bufferedFile: java.io.File = java.io.File(selectedFileUri?.path)

                val file = File()
                file.name = bufferedFile.name
                file.fileSize = bufferedFile.length().toDouble()
                file.downloadURL = downloadUri.toString()
                file.fileType = ItemManager.obtainFileExtension(selectedFileUri!!)
                file.author = Client(this@TransferService).fullName
                file.timestamp = Timestamp.now()

                fileReference.add(file)
                    .addOnCompleteListener {
                        if (it.isSuccessful)
                            taskCompleted()
                    }
                // Operates on new thread
            }
        } else
            Log.i("DataError", "Download URI is null")
    }
}