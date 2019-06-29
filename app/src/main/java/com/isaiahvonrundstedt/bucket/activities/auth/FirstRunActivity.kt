package com.isaiahvonrundstedt.bucket.activities.auth

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.isaiahvonrundstedt.bucket.R

class FirstRunActivity: AppCompatActivity() {

    private lateinit var loginButton: MaterialButton
    private lateinit var registerButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firstrun)

        loginButton = findViewById(R.id.loginButton)
        registerButton = findViewById(R.id.registerButton)

    }

    override fun onStart() {
        super.onStart()

        loginButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        registerButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return false
    }

}