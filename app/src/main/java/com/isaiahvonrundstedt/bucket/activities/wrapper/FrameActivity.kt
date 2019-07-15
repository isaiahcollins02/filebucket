package com.isaiahvonrundstedt.bucket.activities.wrapper

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseAppBarActivity
import com.isaiahvonrundstedt.bucket.constants.Params
import com.isaiahvonrundstedt.bucket.fragments.DetailFragment
import com.isaiahvonrundstedt.bucket.fragments.profile.ResetFragment
import com.isaiahvonrundstedt.bucket.fragments.profile.SecureFragment

class FrameActivity: BaseAppBarActivity() {

    companion object {
        const val viewTypePassword: Int = 0
        const val viewTypeReset: Int = 1
        const val viewTypeDetails: Int = 2
    }

    private var viewArgs: Bundle? = null
    private var activeFragment: Fragment? = null
    private var userViewType: Int? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_frame)

        val intent: Intent? = intent
        userViewType = intent?.getIntExtra(Params.viewType, 0)
        viewArgs = intent?.getBundleExtra(Params.viewArgs)

        when (userViewType){
            viewTypeDetails -> setToolbarTitle(R.string.file_details)
            viewTypePassword -> setToolbarTitle(R.string.profile_secure_account)
            viewTypeReset -> setToolbarTitle(R.string.profile_reset_password)
        }

        when (userViewType){
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
        activeFragment = null
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            android.R.id.home -> onBackPressed()
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }
}