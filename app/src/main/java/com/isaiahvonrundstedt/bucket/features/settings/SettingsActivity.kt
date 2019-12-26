package com.isaiahvonrundstedt.bucket.features.settings

import android.os.Bundle
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.features.shared.abstracts.BaseAppBarActivity

class SettingsActivity: BaseAppBarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_frame)
        setToolbarTitle(R.string.navigation_settings)

        supportFragmentManager.beginTransaction().run {
            replace(R.id.childLayout, SettingsFragment())
            commit()
        }
    }

}