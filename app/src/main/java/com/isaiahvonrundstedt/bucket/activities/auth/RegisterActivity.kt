package com.isaiahvonrundstedt.bucket.activities.auth

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.fragments.register.BasicFragment

class RegisterActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
    }

    override fun onStart() {
        super.onStart()

        supportFragmentManager.beginTransaction().run {
            replace(R.id.childLayout, BasicFragment())
            commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return false
    }

}