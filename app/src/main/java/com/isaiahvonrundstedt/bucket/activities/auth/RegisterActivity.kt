package com.isaiahvonrundstedt.bucket.activities.auth

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseActivity
import com.isaiahvonrundstedt.bucket.constants.Params
import com.isaiahvonrundstedt.bucket.fragments.registration.AuthFragment
import com.isaiahvonrundstedt.bucket.fragments.registration.BasicFragment
import com.isaiahvonrundstedt.bucket.fragments.registration.EmailFragment
import com.isaiahvonrundstedt.bucket.utils.Preferences
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity: BaseActivity() {

    private var currentView: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        setSupportActionBar(toolbar)
        supportActionBar?.title = null
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    companion object {
        const val viewTypeEmail = 1
        const val viewTypePassword = 2
        const val viewTypeBasic = 3
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(Params.viewType, currentView)
    }

    override fun onStart() {
        super.onStart()

        supportFragmentManager.beginTransaction().run {
            replace(R.id.childLayout, getViewByID(currentView) ?: EmailFragment())
            addToBackStack("viewTag")
            commit()
        }
    }

    private fun getViewByID(int: Int?): Fragment? {
        return when (int){
            viewTypeEmail -> EmailFragment()
            viewTypeBasic -> BasicFragment()
            viewTypePassword -> AuthFragment()
            else -> null
        }
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