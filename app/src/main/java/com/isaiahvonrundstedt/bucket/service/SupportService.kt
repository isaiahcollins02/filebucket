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
import com.isaiahvonrundstedt.bucket.architecture.database.AppDatabase
import com.isaiahvonrundstedt.bucket.architecture.database.NotificationDAO
import com.isaiahvonrundstedt.bucket.architecture.store.NotificationRepository
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseService
import com.isaiahvonrundstedt.bucket.constants.Firestore
import com.isaiahvonrundstedt.bucket.objects.core.Notification
import timber.log.Timber

class SupportService: BaseService() {

    private var appDB: AppDatabase? = null
    private var notificationDAO: NotificationDAO? = null

    private val firestore by lazy { FirebaseFirestore.getInstance() }
    private val repository by lazy { NotificationRepository(application) }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    companion object {
        const val actionLife = "life"
        const val actionSupport = "check"

        private const val newPackageNotificationID = 1
        private const val unsupportedNotificationID = 2
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        appDB = AppDatabase.getDatabase(this)
        notificationDAO = appDB?.notificationAccessor()

        if (actionLife == intent?.action)
            checkIfSupported()
        else if (actionSupport == intent?.action)
            checkPackages()

        return START_REDELIVER_INTENT
    }

    private fun checkPackages(){
        val reference = firestore.collection(Firestore.Support.updates).document(Firestore.Support.updates)
        reference.get().addOnCompleteListener {
            if (it.isSuccessful){
                val newPackages: Package? = it.result?.toObject(Package::class.java)
                notifyNewPackage(newPackages?.version)
            } else
                Timber.e(it.exception.toString())
        }
    }

    private data class Package(var version: Double, var args: Array<String>){
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Package

            if (version != other.version) return false
            if (!args.contentEquals(other.args)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = version.hashCode()
            result = 31 * result + args.contentHashCode()
            return result
        }

    }

    private fun checkIfSupported(){
        val reference = firestore.collection(Firestore.Support.core).document(Firestore.Support.life)
        reference.get().addOnCompleteListener {
            if (it.isSuccessful){
                val currentVersion = BuildConfig.VERSION_CODE.toDouble()
                val maxVersion: Double? = it.result?.getDouble(Firestore.Support.maxVersion)
                val minVersion: Double? = it.result?.getDouble(Firestore.Support.minVersion)

                if (!(minVersion!! >= currentVersion && maxVersion!! <= currentVersion))
                    notifyUnsupportedVersion()
            } else
                Timber.e(it.exception.toString())
        }
    }

    private fun notifyNewPackage(version: Double?){
        val icon = R.drawable.ic_vector_update

        val resultIntent = Intent(applicationContext, MainActivity::class.java)
        val resultPendingIntent = PendingIntent.getBroadcast(applicationContext, 0, resultIntent, 0)

        val notification = Notification().apply {
            title = String.format(getString(R.string.notification_update_available_title), version)
            content = getString(R.string.notification_update_available_content)
            type = Notification.typePackage
        }
        repository.insert(notification)

        val builder = NotificationCompat.Builder(this, Notification.supportChannel)
            .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
            .setSmallIcon(icon)
            .setContentTitle(notification.title)
            .setContentText(notification.content)
            .setContentIntent(resultPendingIntent)

        manager.notify(newPackageNotificationID, builder.build())
    }

    private fun notifyUnsupportedVersion(){
        val icon = R.drawable.ic_vector_warning

        val resultIntent = Intent(this, MainActivity::class.java)
        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
            // Add the intent, which inflates the back stack
            addNextIntent(resultIntent)
            // Get the pending intent containing the entire back stack
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        createDefaultChannel()
        val builder = NotificationCompat.Builder(this, getString(R.string.notification_channel_default))
            .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
            .setSmallIcon(icon)
            .setContentTitle(getString(R.string.notification_unsupported_version_title))
            .setContentText(getString(R.string.notification_unsupported_version_content))
            .setAutoCancel(false)
            .setContentIntent(resultPendingIntent)

        manager.notify(unsupportedNotificationID, builder.build())
    }

}