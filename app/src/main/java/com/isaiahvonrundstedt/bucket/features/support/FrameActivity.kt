package com.isaiahvonrundstedt.bucket.features.support

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.features.shared.abstracts.BaseAppBarActivity
import com.isaiahvonrundstedt.bucket.constants.Params
import com.isaiahvonrundstedt.bucket.features.account.profile.ResetFragment
import com.isaiahvonrundstedt.bucket.features.account.profile.SecureFragment

class FrameActivity: BaseAppBarActivity() {

    companion object {
        const val viewTypePassword: Int = 0
        const val viewTypeReset: Int = 1
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
            viewTypePassword -> setToolbarTitle(R.string.navigation_secure)
            viewTypeReset -> setToolbarTitle(R.string.navigation_reset)
        }

        when (userViewType){
            viewTypePassword -> activeFragment = SecureFragment()
            viewTypeReset -> activeFragment = ResetFragment()
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