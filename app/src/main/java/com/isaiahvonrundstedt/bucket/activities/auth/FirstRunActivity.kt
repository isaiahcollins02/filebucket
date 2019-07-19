package com.isaiahvonrundstedt.bucket.activities.auth

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.activities.wrapper.WebViewActivity
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseActivity
import com.isaiahvonrundstedt.bucket.utils.Preferences
import kotlinx.android.synthetic.main.activity_firstrun.*

class FirstRunActivity: BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firstrun)

        setSupportActionBar(toolbar)
        supportActionBar?.title = null

        val spannableString = SpannableString(getString(R.string.license_agreement_helper))
        spannableString.setSpan(object: ClickableSpan(){
            override fun onClick(widget: View) {
                startActivity(Intent(this@FirstRunActivity, WebViewActivity::class.java)
                    .putExtra("viewType", WebViewActivity.viewTypePrivacy))
            }
        }, 33, 47, 0)
        spannableString.setSpan(object: ClickableSpan(){
            override fun onClick(widget: View) {
                startActivity(Intent(this@FirstRunActivity, WebViewActivity::class.java)
                    .putExtra("viewType", WebViewActivity.viewTypeTerms))
            }
        }, 52, 68, 0)
        hyperlinkView.movementMethod = LinkMovementMethod.getInstance()
        hyperlinkView.setText(spannableString, TextView.BufferType.SPANNABLE)
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
            } else -> super.onOptionsItemSelected(item)
        }
    }

}