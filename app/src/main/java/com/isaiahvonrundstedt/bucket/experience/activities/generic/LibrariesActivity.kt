package com.isaiahvonrundstedt.bucket.experience.activities.generic

import android.os.Bundle
import android.view.MenuItem
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.core.components.abstracts.BaseActivity
import com.isaiahvonrundstedt.bucket.experience.fragments.preference.LibrariesFragment

class LibrariesActivity: BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_frame)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setToolbarTitle(getString(R.string.pref_acknowledgements_title))

        supportFragmentManager.beginTransaction().run {
            replace(R.id.childLayout, LibrariesFragment())
            addToBackStack("viewLibraries")
            commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId){
            android.R.id.home -> finish()
        }
        return true
    }

}