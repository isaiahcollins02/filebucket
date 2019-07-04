package com.isaiahvonrundstedt.bucket.activities.wrapper

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseAppBarActivity
import com.isaiahvonrundstedt.bucket.fragments.DetailFragment
import com.isaiahvonrundstedt.bucket.fragments.profile.BoxFragment
import com.isaiahvonrundstedt.bucket.fragments.profile.ResetFragment
import com.isaiahvonrundstedt.bucket.fragments.profile.SecureFragment

class FrameActivity: BaseAppBarActivity() {

    companion object {
        const val viewTypeSent: Int = 0
        const val viewTypePassword: Int = 1
        const val viewTypeReset: Int = 2
        const val viewTypeDetails: Int = 3
    }

    private var viewArgs: Bundle? = null
    private var activeFragment: Fragment? = null
    private var userViewType: Int? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_frame)

        val intent: Intent? = intent
        userViewType = intent?.getIntExtra("viewType", 0)
        viewArgs = intent?.getBundleExtra("viewArgs")

        when(userViewType){
            viewTypeSent -> setToolbarTitle(getString(R.string.profile_view_repository))
            viewTypePassword -> setToolbarTitle(getString(R.string.profile_secure_account))
            viewTypeReset -> setToolbarTitle(getString(R.string.profile_reset_password))
            viewTypeDetails -> setToolbarTitle(getString(R.string.file_details))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            android.R.id.home -> onBackPressed()
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onStart() {
        super.onStart()
        
        when (userViewType){
            viewTypeSent -> activeFragment = BoxFragment()
            viewTypePassword -> activeFragment = SecureFragment()
            viewTypeReset -> activeFragment = ResetFragment()
            viewTypeDetails -> activeFragment = DetailFragment()
        }
        activeFragment?.arguments = viewArgs

        supportFragmentManager.beginTransaction().run {
            replace(R.id.childLayout, activeFragment!!)
            addToBackStack(null)
            commit()
        }
    }

}