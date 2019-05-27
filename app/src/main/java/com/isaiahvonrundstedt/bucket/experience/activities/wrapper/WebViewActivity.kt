package com.isaiahvonrundstedt.bucket.experience.activities.wrapper

import android.os.Bundle
import android.view.MenuItem
import android.webkit.WebView
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.core.components.abstracts.BaseActivity

class WebViewActivity: BaseActivity() {

    companion object {
        const val VIEW_TYPE_TERMS = 0
        const val VIEW_TYPE_PRIVACY = 1
    }

    private var userViewType: Int = 0

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        webView = findViewById(R.id.webView)
        webView.settings.javaScriptEnabled = false

        val intent = intent
        userViewType = intent.getIntExtra("VIEW_TYPE", 0)

        when (userViewType){
            0 -> setToolbarTitle(resources.getString(R.string.pref_terms_of_service))
            1 -> setToolbarTitle(resources.getString(R.string.pref_privacy_policy))
        }
    }

    override fun onStart() {
        super.onStart()

        when (userViewType){
            0 -> webView.loadUrl("file:///android_asset/terms_and_conditions.html")
            1 -> webView.loadUrl("file:///android_asset/privacy_policy.html")
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            android.R.id.home -> finish()
        }
        return true
    }

}