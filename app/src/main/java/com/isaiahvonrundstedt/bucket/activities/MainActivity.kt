package com.isaiahvonrundstedt.bucket.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.google.android.material.navigation.NavigationView
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.activities.generic.AboutActivity
import com.isaiahvonrundstedt.bucket.activities.generic.SettingsActivity
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseActivity
import com.isaiahvonrundstedt.bucket.components.modules.GlideApp
import com.isaiahvonrundstedt.bucket.fragments.navigation.*
import com.isaiahvonrundstedt.bucket.fragments.screendialog.SearchFragment
import com.isaiahvonrundstedt.bucket.utils.Permissions
import com.isaiahvonrundstedt.bucket.utils.Preferences
import com.isaiahvonrundstedt.bucket.utils.User
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_appbar_drawer.*

class MainActivity : BaseActivity(), LifecycleOwner, NavigationView.OnNavigationItemSelectedListener {

    private var selectedItem: Int? = null
    private var toolbarTitleView: AppCompatTextView? = null

    companion object {
        const val navigationItemCloud = 0
        const val navigationItemLocal = 1
        const val navigationItemBoxes = 2
        const val navigationItemSaved = 3
        const val navigationItemNotification = 4
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        selectedItem = savedInstanceState?.getInt("savedTab")

        setSupportActionBar(toolbar)
        supportActionBar?.title = null
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { drawerLayout.openDrawer(GravityCompat.START) }
        toolbarTitleView = toolbar.findViewById(R.id.titleView)
        setupNavigationHeader()

        val actionBarToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.status_drawer_open,
            R.string.status_drawer_closed)
        drawerLayout.addDrawerListener(actionBarToggle)
        actionBarToggle.drawerArrowDrawable.color = ContextCompat.getColor(this, R.color.colorIcons)
        actionBarToggle.syncState()

        if (!Preferences(this).isDarkEnabled){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                drawerLayout.setStatusBarBackground(R.color.colorDrawerStatusBar)
            }
        } else {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                drawerLayout.setStatusBarBackground(R.color.colorDrawerStatusBar)
            else drawerLayout.setStatusBarBackground(android.R.color.black)
        }

        navigationView.setNavigationItemSelectedListener(this)
        navigationView.setCheckedItem(obtainMenuItemID(selectedItem))
    }

    private fun obtainMenuItemID(int: Int?): Int {
        return when (int){
            navigationItemCloud -> R.id.navigation_cloud
            navigationItemLocal -> R.id.navigation_local
            navigationItemBoxes -> R.id.navigation_boxes
            navigationItemSaved -> R.id.navigation_collections
            navigationItemNotification -> R.id.navigation_notifications
            else -> R.id.navigation_cloud
        }
    }

    private fun setupNavigationHeader(){
        val headerLayout = navigationView.getHeaderView(0)
        val imageView: AppCompatImageView = headerLayout.findViewById(R.id.imageView)
        val titleView: AppCompatTextView = headerLayout.findViewById(R.id.titleView)
        val subtitleView: AppCompatTextView = headerLayout.findViewById(R.id.subtitleView)

        with(User(this)){
            titleView.text = fullName
            subtitleView.text = email

            GlideApp.with(this@MainActivity)
                .load(imageURL)
                .centerCrop()
                .error(R.drawable.ic_object_user)
                .into(imageView)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId){
            R.id.action_search -> SearchFragment().show(supportFragmentManager, SearchFragment.tag)
        }
        return true
    }

    private fun replaceFragment(item: Int?){
        selectedItem = item
        supportFragmentManager.beginTransaction().run {
            replace(R.id.childLayout, getFragment(item)!!)
            commit()
        }

        when (item){
            navigationItemCloud -> setToolbarTitle(R.string.navigation_cloud)
            navigationItemLocal -> setToolbarTitle(R.string.navigation_local)
            navigationItemBoxes -> setToolbarTitle(R.string.navigation_boxes)
            navigationItemSaved -> setToolbarTitle(R.string.navigation_saved)
            navigationItemNotification -> setToolbarTitle(R.string.navigation_notifications)
        }
    }

    private fun setToolbarTitle(int: Int) {
        toolbarTitleView?.text = getString(int)
    }

    private fun getFragment(item: Int?): Fragment? {
        return when (item){
            navigationItemCloud -> CloudFragment()
            navigationItemLocal -> LocalFragment()
            navigationItemBoxes -> BoxesFragment()
            navigationItemSaved -> SavedFragment()
            navigationItemNotification -> NotificationFragment()
            else -> null
        }
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START)
        else
            super.onBackPressed()
    }

    override fun onResume() {
        super.onResume()
        replaceFragment(selectedItem ?: navigationItemCloud)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode){
            Permissions.writeRequestCode -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    replaceFragment(selectedItem ?: navigationItemCloud)
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onNavigationItemSelected(navigationItem: MenuItem): Boolean {
        when (navigationItem.itemId){
            R.id.navigation_cloud -> replaceFragment(navigationItemCloud)
            R.id.navigation_local -> replaceFragment(navigationItemLocal)
            R.id.navigation_boxes -> replaceFragment(navigationItemBoxes)
            R.id.navigation_collections -> replaceFragment(navigationItemSaved)
            R.id.navigation_notifications -> replaceFragment(navigationItemNotification)
            R.id.navigation_settings -> startActivity(Intent(this, SettingsActivity::class.java))
            R.id.navigation_about -> startActivity(Intent(this, AboutActivity::class.java))
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("savedTab", selectedItem!!)
    }

}
