package com.isaiahvonrundstedt.bucket.components.abstracts

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.fragments.screendialog.FeedbackFragment

abstract class BaseAppBarActivity: BaseActivity() {

    private var toolbarTitleView: AppCompatTextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setPersistentActionBar()
    }

    private fun setPersistentActionBar() {
        val rootView: ViewGroup? = findViewById(R.id.action_bar_root)
        if (rootView != null){
            val view: View = LayoutInflater.from(this).inflate(R.layout.layout_appbar_core, rootView, false)
            rootView.addView(view, 0)

            val toolbar: Toolbar = rootView.findViewById(R.id.toolbar)
            setSupportActionBar(toolbar)

            toolbar.setNavigationOnClickListener { finish() }
            supportActionBar?.title = null
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            toolbarTitleView = findViewById(R.id.toolbarTitle)
        }
    }

    internal fun setToolbarTitle(title: String?){
        toolbarTitleView?.text = title
    }

    internal fun setToolbarTitle(titleResID: Int){
        toolbarTitleView?.text = getString(titleResID)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId){
            R.id.action_support -> {
                FeedbackFragment().invoke(supportFragmentManager)
                true
            } else -> super.onOptionsItemSelected(item)
        }
    }

}