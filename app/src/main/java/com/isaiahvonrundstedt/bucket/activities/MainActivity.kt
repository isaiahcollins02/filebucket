package com.isaiahvonrundstedt.bucket.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.AppCompatImageView
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
import com.isaiahvonrundstedt.bucket.fragments.screendialog.SearchFragment
import com.isaiahvonrundstedt.bucket.fragments.navigation.BoxesFragment
import com.isaiahvonrundstedt.bucket.fragments.navigation.FilesFragment
import com.isaiahvonrundstedt.bucket.fragments.navigation.NotificationFragment
import com.isaiahvonrundstedt.bucket.fragments.navigation.SavedFragment
import com.isaiahvonrundstedt.bucket.utils.Permissions
import com.isaiahvonrundstedt.bucket.utils.Preferences
import com.isaiahvonrundstedt.bucket.utils.User
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_appbar_drawer.*

class MainActivity : BaseActivity(), LifecycleOwner, NavigationView.OnNavigationItemSelectedListener {

    private var selectedItem: Int? = null
    private var toolbarTitleView: TextView? = null

    companion object {
        const val navigationItemFiles = 0
        const val navigationItemBoxes = 1
        const val navigationItemSaved = 2
        const val navigationItemNotification = 3
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

        if (Preferences(this).theme == Preferences.themeLight){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                drawerLayout.setStatusBarBackground(android.R.color.transparent)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                drawerLayout.setStatusBarBackground(R.color.colorDrawerStatusBar)
            }
        } else {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                drawerLayout.setStatusBarBackground(R.color.colorDrawerStatusBar)
        }

        navigationView.setNavigationItemSelectedListener(this)
        navigationView.setCheckedItem(obtainMenuItemID(selectedItem))
    }

    private fun obtainMenuItemID(int: Int?): Int {
        return when (int){
            navigationItemFiles -> R.id.navigation_files
            navigationItemBoxes -> R.id.navigation_boxes
            navigationItemSaved -> R.id.navigation_collections
            navigationItemNotification -> R.id.navigation_notifications
            else -> R.id.navigation_files
        }
    }

    private fun setupNavigationHeader(){
        val headerLayout = navigationView.getHeaderView(0)
        val imageView: AppCompatImageView = headerLayout.findViewById(R.id.imageView)
        val titleView: TextView = headerLayout.findViewById(R.id.titleView)
        val subtitleView: TextView = headerLayout.findViewById(R.id.subtitleView)

        with(User(this)){
            titleView.text = fullName
            subtitleView.text = email

            GlideApp.with(this@MainActivity)
                .load(imageURL)
                .centerCrop()
                .error(R.drawable.ic_brand_user)
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
            navigationItemFiles -> setToolbarTitle(R.string.navigation_files)
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
            navigationItemFiles -> FilesFragment()
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

        if (!Permissions(this).writeAccessGranted){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                MaterialDialog(this).show {
                    lifecycleOwner(this@MainActivity)
                    title(R.string.dialog_permission_revoked_title)
                    message(R.string.dialog_permission_revoked_summary)
                    positiveButton(R.string.button_continue){
                        ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            Permissions.writeRequestCode)
                    }
                }
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    Permissions.writeRequestCode)
            }
        } else
            replaceFragment(selectedItem ?: navigationItemFiles)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode){
            Permissions.writeRequestCode -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    replaceFragment(selectedItem ?: navigationItemFiles)
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onNavigationItemSelected(navigationItem: MenuItem): Boolean {
        when (navigationItem.itemId){
            R.id.navigation_files -> replaceFragment(navigationItemFiles)
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
