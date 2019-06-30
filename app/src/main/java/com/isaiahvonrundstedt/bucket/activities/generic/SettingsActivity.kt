package com.isaiahvonrundstedt.bucket.activities.generic

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.MenuItem
import androidx.appcompat.app.AppCompatDelegate
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseAppBarActivity
import com.isaiahvonrundstedt.bucket.fragments.preference.SettingsFragment
import com.isaiahvonrundstedt.bucket.utils.Preferences

class SettingsActivity: BaseAppBarActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_frame)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setToolbarTitle(R.string.navigation_settings)

        supportFragmentManager.beginTransaction().run {
            replace(R.id.childLayout, SettingsFragment())
            commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId){
            android.R.id.home -> finish()
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

}