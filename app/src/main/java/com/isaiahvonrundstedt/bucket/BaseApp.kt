package com.isaiahvonrundstedt.bucket

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import androidx.preference.PreferenceManager
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

class BaseApp: MultiDexApplication() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        // Analytics
        val firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        val settings = PreferenceManager.getDefaultSharedPreferences(this)
        val analyticsEnabled = settings.getBoolean("diagnosticsPreference", true)
        firebaseAnalytics.setAnalyticsCollectionEnabled(analyticsEnabled)

        //Firestore
        val firestore = FirebaseFirestore.getInstance()
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
    }
}