package com.isaiahvonrundstedt.bucket.features.support

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.google.android.material.navigation.NavigationView
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.features.about.AboutActivity
import com.isaiahvonrundstedt.bucket.features.settings.SettingsActivity
import com.isaiahvonrundstedt.bucket.features.account.AccountActivity
import com.isaiahvonrundstedt.bucket.features.notifications.NotificationActivity
import com.isaiahvonrundstedt.bucket.features.account.SharedActivity
import com.isaiahvonrundstedt.bucket.features.storage.StorageActivity
import com.isaiahvonrundstedt.bucket.features.shared.abstracts.BaseBottomSheet
import com.isaiahvonrundstedt.bucket.utils.User
import kotlinx.android.synthetic.main.layout_sheet_overflow.*

class OverflowBottomSheet: BaseBottomSheet(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_sheet_overflow, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navigationView.setNavigationItemSelectedListener(this)

        with (User(context!!)){
            titleView.text = fullName
            subtitleView.text = email
        }
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