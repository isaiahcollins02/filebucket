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
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseActivity
import com.isaiahvonrundstedt.bucket.constants.Params
import com.isaiahvonrundstedt.bucket.fragments.screendialog.WebViewFragment
import com.isaiahvonrundstedt.bucket.utils.Preferences
import kotlinx.android.synthetic.main.activity_firstrun.*

class FirstRunActivity: BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firstrun)

        setSupportActionBar(toolbar)
        supportActionBar?.title = null

        val webViewFragment = WebViewFragment()
        val args = Bundle()

        val spannableString = SpannableString(getString(R.string.first_run_terms))
        spannableString.setSpan(object: ClickableSpan() {
            override fun onClick(widget: View) {
                args.putInt(Params.viewType, WebViewFragment.viewTypePrivacy)
                webViewFragment.arguments = args
                webViewFragment.invoke(supportFragmentManager)
            }
        }, 33, 47, 0)
        spannableString.setSpan(object: ClickableSpan(){
            override fun onClick(widget: View) {
                args.putInt(Params.viewType, WebViewFragment.viewTypeTerms)
                webViewFragment.arguments = args
                webViewFragment.invoke(supportFragmentManager)
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

}