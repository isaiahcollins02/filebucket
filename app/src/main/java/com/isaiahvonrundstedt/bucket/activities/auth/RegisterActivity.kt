package com.isaiahvonrundstedt.bucket.activities.auth

import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseActivity
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity: BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { onBackPressed() }
        supportActionBar?.title = null
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onStart() {
        super.onStart()

        val navigationHost = NavHostFragment.create(R.navigation.graph_register)
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, navigationHost)
            .setPrimaryNavigationFragment(navigationHost)
            .commit()
    }
}