package com.isaiahvonrundstedt.bucket.features.about

import android.os.Bundle
import android.view.MenuItem
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.features.shared.abstracts.BaseAppBarActivity

class AboutActivity: BaseAppBarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_frame)
        setToolbarTitle(R.string.activity_about)

        supportFragmentManager.beginTransaction().run {
            replace(R.id.childLayout, AboutFragment())
            addToBackStack("viewAbout")
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