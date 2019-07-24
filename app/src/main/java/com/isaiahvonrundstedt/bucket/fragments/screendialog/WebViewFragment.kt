package com.isaiahvonrundstedt.bucket.fragments.screendialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseScreenDialog
import com.isaiahvonrundstedt.bucket.constants.Params
import kotlinx.android.synthetic.main.layout_dialog_webview.*

class WebViewFragment: BaseScreenDialog() {

    private var userViewType: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userViewType = arguments!!.getInt(Params.viewType, 0)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_dialog_webview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when (userViewType){
            viewTypeTerms -> setToolbarTitle(R.string.about_terms_of_service)
            viewTypePrivacy -> setToolbarTitle(R.string.about_privacy_policy)
            viewTypeLicense -> setToolbarTitle(R.string.about_licenses)
        }
    }

    override fun onResume() {
        super.onResume()
        val url: String? = when (userViewType){
            viewTypeTerms -> "file:///android_asset/terms_and_conditions.html"
            viewTypePrivacy -> "file:///android_asset/privacy_policy.html"
            viewTypeLicense -> "file:///android_asset/software_licenses.html"
            else -> null
        }
        webView.loadUrl(url)
    }

    companion object {
        const val viewTypeGeneric = 0
        const val viewTypeTerms = 1
        const val viewTypePrivacy = 2
        const val viewTypeLicense = 3

        const val tag = "webViewTag"
    }

}