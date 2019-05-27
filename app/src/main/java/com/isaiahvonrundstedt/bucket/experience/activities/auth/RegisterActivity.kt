package com.isaiahvonrundstedt.bucket.experience.activities.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.experience.fragments.register.BasicFragment

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

}