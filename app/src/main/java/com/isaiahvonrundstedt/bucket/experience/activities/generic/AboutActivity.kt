package com.isaiahvonrundstedt.bucket.experience.activities.generic

import android.os.Bundle
import android.view.MenuItem
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.core.components.abstracts.BaseActivity
import com.isaiahvonrundstedt.bucket.experience.fragments.preference.AboutFragment

class AboutActivity: BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_frame)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setToolbarTitle(getString(R.string.navigation_about))

        supportFragmentManager.beginTransaction().run {
            replace(R.id.childLayout, AboutFragment())
            addToBackStack("viewAbout")
            commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId){
            android.R.id.home -> {
                super.onBackPressed()
            }
        }
        return true
    }

}