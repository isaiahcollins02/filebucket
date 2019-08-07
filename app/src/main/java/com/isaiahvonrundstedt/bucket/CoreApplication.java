package com.isaiahvonrundstedt.bucket;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;
import androidx.preference.PreferenceManager;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class CoreApplication extends MultiDexApplication {

    public static String appPackage = "com.isaiahvonrundstedt.bucket";

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseApp.initializeApp(this);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        boolean analytics = settings.getBoolean(getString(R.string.settings_key_diagnostics), true);
        firebaseAnalytics.setAnalyticsCollectionEnabled(analytics);

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.setFirestoreSettings(new FirebaseFirestoreSettings.Builder()
                    .setPersistenceEnabled(true)
                    .build());
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
