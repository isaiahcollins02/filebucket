package com.isaiahvonrundstedt.bucket.activities.generic

import android.os.Bundle
import android.view.MenuItem
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseAppBarActivity
import com.isaiahvonrundstedt.bucket.fragments.preference.LibrariesFragment

class LibrariesActivity: BaseAppBarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_frame)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setToolbarTitle(getString(R.string.about_acknowledgements_title))

        supportFragmentManager.beginTransaction().run {
            replace(R.id.childLayout, LibrariesFragment())
            addToBackStack("viewLibraries")
            commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId){
            android.R.id.home -> super.onBackPressed()
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

}