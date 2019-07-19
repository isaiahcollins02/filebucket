package com.isaiahvonrundstedt.bucket.activities.wrapper

import android.os.Bundle
import android.view.MenuItem
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseAppBarActivity
import kotlinx.android.synthetic.main.activity_web_view.*

class WebViewActivity: BaseAppBarActivity() {

    companion object {
        const val viewTypeGeneric = 0
        const val viewTypeTerms = 1
        const val viewTypePrivacy = 2
    }

    private var userViewType: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        val intent = intent
        userViewType = intent.getIntExtra("viewType", viewTypeGeneric)
        when (userViewType){
            viewTypeTerms -> setToolbarTitle(resources.getString(R.string.about_terms_of_service))
            viewTypePrivacy -> setToolbarTitle(resources.getString(R.string.about_privacy_policy))
        }
    }

    override fun onResume() {
        super.onResume()
        when (userViewType){
            viewTypeTerms -> webView.loadUrl("file:///android_asset/terms_and_conditions.html")
            viewTypePrivacy -> webView.loadUrl("file:///android_asset/privacy_policy.html")
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            android.R.id.home -> onBackPressed()
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

}