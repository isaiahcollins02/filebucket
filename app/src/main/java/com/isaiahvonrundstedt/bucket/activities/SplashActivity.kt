package com.isaiahvonrundstedt.bucket.activities

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.afollestad.materialdialogs.MaterialDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.activities.auth.FirstRunActivity
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseActivity
import com.isaiahvonrundstedt.bucket.constants.Firestore
import com.isaiahvonrundstedt.bucket.objects.core.Account
import com.isaiahvonrundstedt.bucket.service.NotificationService
import com.isaiahvonrundstedt.bucket.utils.User
import kotlinx.coroutines.runBlocking
import timber.log.Timber


class SplashActivity: BaseActivity() {

    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val userReference by lazy { FirebaseFirestore.getInstance().collection(Firestore.users) }

    private var firebaseUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseUser = firebaseAuth?.currentUser
        executeServices()
    }

    private fun executeServices() = runBlocking {
        val service = NotificationService()
        val serviceIntent = Intent(applicationContext, service.javaClass)
        if (isServiceRunning(service.javaClass))
            startService(serviceIntent)
    }
    @Suppress("DEPRECATION")
    private fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (runningService in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == runningService.service.className) {
                return true
            }
        }
        return false
    }

    override fun onStart() {
        super.onStart()

        var cachedAccount: Account? = null
        User(this).fetch { cachedAccount = it }
        if (firebaseAuth?.currentUser != null){
            if (cachedAccount == null){
                userReference.document(firebaseAuth?.uid!!).get()
                    .addOnSuccessListener {
                        val account: Account = it.toObject(Account::class.java) as Account
                        User(this).save(account)
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                    .addOnFailureListener {
                        MaterialDialog(this).show { title(R.string.dialog_token_error) }
                        Timber.i(it)
                        startActivity(Intent(this, FirstRunActivity::class.java))
                        finish()
                    }
            } else {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        } else {
            startActivity(Intent(this, FirstRunActivity::class.java))
            finish()
        }
    }
}