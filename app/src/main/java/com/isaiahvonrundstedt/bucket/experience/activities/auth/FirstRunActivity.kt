package com.isaiahvonrundstedt.bucket.experience.activities.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.button.MaterialButton
import com.isaiahvonrundstedt.bucket.R

class FirstRunActivity: AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var loginButton: MaterialButton
    private lateinit var registerButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firstrun)

        toolbar = findViewById(R.id.toolbar)
        loginButton = findViewById(R.id.loginButton)
        registerButton = findViewById(R.id.registerButton)

        setSupportActionBar(toolbar)
        supportActionBar?.title = null
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
}