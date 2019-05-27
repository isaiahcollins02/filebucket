package com.isaiahvonrundstedt.bucket.experience.activities.wrapper

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.core.components.abstracts.BaseActivity
import com.isaiahvonrundstedt.bucket.experience.fragments.profile.RepoFragment
import com.isaiahvonrundstedt.bucket.experience.fragments.profile.ResetFragment
import com.isaiahvonrundstedt.bucket.experience.fragments.profile.SecureFragment

class FrameActivity: BaseActivity() {

    companion object {
        const val VIEW_TYPE_SENT: Int = 0
        const val VIEW_TYPE_PASSWORD: Int = 1
        const val VIEW_TYPE_RESET: Int = 2
    }

    private var activeFragment: Fragment? = null
    private var userViewType: Int? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_frame)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val intent: Intent = intent
        userViewType = intent.getIntExtra("VIEW_TYPE", 0)

        when(userViewType){
            VIEW_TYPE_SENT -> setToolbarTitle(getString(R.string.profile_view_repository))
            VIEW_TYPE_PASSWORD -> setToolbarTitle(getString(R.string.profile_secure_account))
            VIEW_TYPE_RESET -> setToolbarTitle(getString(R.string.profile_reset_password))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            android.R.id.home -> finish()
        }
        return true
    }

    override fun onStart() {
        super.onStart()
        
        when (userViewType){
            VIEW_TYPE_SENT -> activeFragment = RepoFragment()
            VIEW_TYPE_PASSWORD -> activeFragment = SecureFragment()
            VIEW_TYPE_RESET -> activeFragment = ResetFragment()
        }

        supportFragmentManager.beginTransaction().run {
            replace(R.id.childLayout, activeFragment!!)
            addToBackStack(null)
            commit()
        }
    }

}