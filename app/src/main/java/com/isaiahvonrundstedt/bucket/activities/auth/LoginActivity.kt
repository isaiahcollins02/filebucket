package com.isaiahvonrundstedt.bucket.activities.auth

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatDelegate
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseActivity
import com.isaiahvonrundstedt.bucket.fragments.auth.AuthFragment
import com.isaiahvonrundstedt.bucket.utils.Preferences
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity: BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { onBackPressed() }
        supportActionBar?.title = null
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onStart() {
        super.onStart()

        supportFragmentManager.beginTransaction().run {
            replace(R.id.childLayout, AuthFragment())
            commit()
        }
    }

}