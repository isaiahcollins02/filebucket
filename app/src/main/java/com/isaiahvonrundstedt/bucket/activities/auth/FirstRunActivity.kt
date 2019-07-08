package com.isaiahvonrundstedt.bucket.activities.auth

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.activities.wrapper.WebViewActivity
import kotlinx.android.synthetic.main.activity_firstrun.*

class FirstRunActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firstrun)

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
        return false
    }

}