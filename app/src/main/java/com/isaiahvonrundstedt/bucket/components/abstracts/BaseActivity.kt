package com.isaiahvonrundstedt.bucket.components.abstracts

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.isaiahvonrundstedt.bucket.utils.Preferences

abstract class BaseActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        onThemeApplied()
        super.onCreate(savedInstanceState)
        // Check if the running device is a tablet or a smartphone. Then if its a smartphone
        // lock the orientation to portrait
        val screenLayoutSize =
            this.resources?.configuration?.screenLayout!! and Configuration.SCREENLAYOUT_SIZE_MASK
        if (screenLayoutSize == Configuration.SCREENLAYOUT_SIZE_SMALL || screenLayoutSize == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
            this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }
    private fun onThemeApplied() {
        when (Preferences(this).theme){
            Preferences.themeLight -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            Preferences.themeDark -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }
}