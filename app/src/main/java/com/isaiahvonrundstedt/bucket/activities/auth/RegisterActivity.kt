package com.isaiahvonrundstedt.bucket.activities.auth

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.NavHostFragment
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseActivity
import com.isaiahvonrundstedt.bucket.utils.Preferences
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity: BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        setSupportActionBar(toolbar)
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_auth, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.findItem(R.id.action_theme)?.isChecked = Preferences(this).theme == Preferences.themeDark
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId){
            R.id.action_theme -> {
                if (!item.isChecked){
                    item.isChecked = true
                    Preferences(this).theme = Preferences.themeDark
                    delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
                } else {
                    item.isChecked = false
                    Preferences(this).theme = Preferences.themeLight
                    delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
                }
                true
            }
            android.R.id.home -> {
                onBackPressed()
                true
            } else -> super.onOptionsItemSelected(item)
        }
    }

}