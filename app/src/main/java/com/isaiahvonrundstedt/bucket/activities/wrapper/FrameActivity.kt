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
        const val VIEW_TYPE_SENT: Int = 0
        const val VIEW_TYPE_PASSWORD: Int = 1
        const val VIEW_TYPE_RESET: Int = 2
        const val VIEW_TYPE_DETAILS: Int = 3
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
            VIEW_TYPE_SENT -> setToolbarTitle(getString(R.string.profile_view_repository))
            VIEW_TYPE_PASSWORD -> setToolbarTitle(getString(R.string.profile_secure_account))
            VIEW_TYPE_RESET -> setToolbarTitle(getString(R.string.profile_reset_password))
            VIEW_TYPE_DETAILS -> setToolbarTitle(getString(R.string.file_details))
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
            VIEW_TYPE_SENT -> activeFragment = BoxFragment()
            VIEW_TYPE_PASSWORD -> activeFragment = SecureFragment()
            VIEW_TYPE_RESET -> activeFragment = ResetFragment()
            VIEW_TYPE_DETAILS -> activeFragment = DetailFragment()
        }
        activeFragment?.arguments = viewArgs

        supportFragmentManager.beginTransaction().run {
            replace(R.id.childLayout, activeFragment!!)
            addToBackStack(null)
            commit()
        }
    }

}