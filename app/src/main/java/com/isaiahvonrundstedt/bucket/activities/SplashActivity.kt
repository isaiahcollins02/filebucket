package com.isaiahvonrundstedt.bucket.activities

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.isaiahvonrundstedt.bucket.activities.auth.FirstRunActivity
import com.isaiahvonrundstedt.bucket.constants.Firebase
import com.isaiahvonrundstedt.bucket.objects.Account
import com.isaiahvonrundstedt.bucket.service.NotificationService
import com.isaiahvonrundstedt.bucket.utils.Client


class SplashActivity: AppCompatActivity(){

    private var firstName: String? = null
    private var lastName: String? = null
    private var email: String? = null
    private var profileURL: String? = null

    private var firestore: FirebaseFirestore? = null
    private var firebaseAuth: FirebaseAuth? = null
    private var firebaseUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firestore = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth?.currentUser

        val service = NotificationService()
        val serviceIntent = Intent(applicationContext, service.javaClass)
        if (isServiceRunning(service.javaClass))
            startService(serviceIntent)

    }

    override fun onStart() {
        super.onStart()

        // Check if the user is signed in
        if (firebaseUser != null){
            val userID: String? = firebaseUser?.uid

            // Get all user data from sharedPreferences, this fields will return null if
            // the no cached version of the user data is available
            Client(this).also {
                this.firstName = it.firstName
                this.lastName = it.lastName
                this.email = it.email
                this.profileURL = it.imageURL
            }

            val dataVerified: Boolean = firstName != null && lastName != null && email != null && profileURL != null

            // Verify the data if it is null or one of those fields is null. If a
            // certain field is null, then fetch all data from Firestore then
            // cache it on SharedPreferences
            if (!dataVerified){
                val userReference: DocumentReference? = firestore?.collection(Firebase.USERS.string)?.document(userID!!)
                userReference?.get()?.addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        val account: Account = task.result?.toObject(Account::class.java) as Account

                        Client(this).apply {
                            firstName = account.firstName
                            lastName = account.lastName
                            email = account.email
                            imageURL = imageURL
                        }

                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else
                        Log.e("DataFetchError", "An error occurred while fetching the required data")
                }
            } else
                startActivity(Intent(this, MainActivity::class.java))
        } else
            startActivity(Intent(this@SplashActivity, FirstRunActivity::class.java))
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

}