package com.isaiahvonrundstedt.bucket.experience.activities.generic

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import androidx.core.app.ActivityCompat
import androidx.preference.PreferenceManager
import com.afollestad.materialdialogs.MaterialDialog
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.core.components.abstracts.BaseActivity
import com.isaiahvonrundstedt.bucket.core.utils.Permissions
import com.isaiahvonrundstedt.bucket.experience.activities.MainActivity
import com.isaiahvonrundstedt.bucket.experience.fragments.preference.SettingsFragment

class SettingsActivity: BaseActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_frame)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setToolbarTitle(resources.getString(R.string.navigation_settings))

        supportFragmentManager.beginTransaction().run {
            replace(R.id.childLayout, SettingsFragment())
            addToBackStack("viewSettings")
            commit()
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == "appThemePref"){
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }

    private lateinit var sharedPreferences: SharedPreferences

    override fun onStart() {
        super.onStart()
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onStop() {
        super.onStop()
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId){
            android.R.id.home -> finish()
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode){
            Permissions.READ_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    val settingsFragment: SettingsFragment = supportFragmentManager?.findFragmentById(R.id.childLayout) as SettingsFragment
                    settingsFragment.invokeChooser()
                } else {
                    MaterialDialog(this).show {
                        title(R.string.dialog_permission_revoked_title)
                        message(R.string.dialog_permission_revoked_summary)
                        positiveButton(R.string.button_continue){
                            ActivityCompat.requestPermissions(this@SettingsActivity,
                                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), Permissions.READ_REQUEST)
                        }
                    }
                }
            } else -> onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

}