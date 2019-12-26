package com.isaiahvonrundstedt.bucket.features.account

import android.os.Bundle
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.features.shared.abstracts.BaseAppBarActivity

class AccountActivity: BaseAppBarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_frame)
        setToolbarTitle(R.string.navigation_account)

        supportFragmentManager.beginTransaction().run {
            replace(R.id.childLayout, AccountFragment())
            commit()
        }
    }

}