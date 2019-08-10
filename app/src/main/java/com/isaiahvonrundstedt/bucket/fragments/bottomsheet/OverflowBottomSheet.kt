package com.isaiahvonrundstedt.bucket.fragments.bottomsheet

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.google.android.material.navigation.NavigationView
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseBottomSheet
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.activities.generic.AboutActivity
import com.isaiahvonrundstedt.bucket.activities.generic.SettingsActivity
import com.isaiahvonrundstedt.bucket.activities.support.AccountActivity
import com.isaiahvonrundstedt.bucket.activities.support.account.NotificationActivity
import com.isaiahvonrundstedt.bucket.activities.support.account.SharedActivity
import com.isaiahvonrundstedt.bucket.activities.support.account.StorageActivity
import com.isaiahvonrundstedt.bucket.utils.User
import kotlinx.android.synthetic.main.layout_sheet_overflow.*

class OverflowBottomSheet: BaseBottomSheet(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_sheet_overflow, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navigationView.setNavigationItemSelectedListener(this)
        titleView.text = User(context!!).fullName
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.action_account -> startActivity(Intent(context, AccountActivity::class.java))
            R.id.action_shared -> startActivity(Intent(context, SharedActivity::class.java))
            R.id.action_notifications -> startActivity(Intent(context, NotificationActivity::class.java))
            R.id.action_downloads -> startActivity(Intent(context, StorageActivity::class.java))
            R.id.action_settings -> startActivity(Intent(context, SettingsActivity::class.java))
            R.id.action_about -> startActivity(Intent(context, AboutActivity::class.java))
        }
        return true
    }


}