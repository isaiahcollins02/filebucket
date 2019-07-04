package com.isaiahvonrundstedt.bucket.activities

import android.Manifest
import android.app.DownloadManager
import android.content.*
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.google.android.material.navigation.NavigationView
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.activities.generic.AboutActivity
import com.isaiahvonrundstedt.bucket.architecture.work.SupportWorker
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseActivity
import com.isaiahvonrundstedt.bucket.components.modules.GlideApp
import com.isaiahvonrundstedt.bucket.fragments.navigation.BoxesFragment
import com.isaiahvonrundstedt.bucket.fragments.navigation.FilesFragment
import com.isaiahvonrundstedt.bucket.fragments.navigation.NotificationFragment
import com.isaiahvonrundstedt.bucket.fragments.navigation.SavedFragment
import com.isaiahvonrundstedt.bucket.fragments.preference.SettingsFragment
import com.isaiahvonrundstedt.bucket.interfaces.ScreenAction
import com.isaiahvonrundstedt.bucket.utils.Account
import com.isaiahvonrundstedt.bucket.utils.Permissions
import com.isaiahvonrundstedt.bucket.utils.Preferences
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_appbar_drawer.*

class MainActivity : BaseActivity(), LifecycleOwner, NavigationView.OnNavigationItemSelectedListener,
     SearchView.OnQueryTextListener {

    private var downloadID: Long? = 0L
    private var selectedItem: Int? = 0
    private var searchMenuItem: MenuItem? = null
    private var toolbarTitleView: TextView? = null
    private var searchView: SearchView? = null
    private var searchListener: ScreenAction.Search? = null

    private lateinit var transferReceiver: BroadcastReceiver

    companion object {
        const val navigationItemFiles = 0
        const val navigationItemBoxes = 1
        const val navigationSaved = 2
        const val navigationItemNotification = 3
        const val navigationItemSettings = 4
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        selectedItem = savedInstanceState?.getInt("savedTab")

        val homeIndicator: Drawable? = ResourcesCompat.getDrawable(resources, R.drawable.ic_vector_menu, null)
        homeIndicator?.setColorFilter(ContextCompat.getColor(this, R.color.colorIcons), PorterDuff.Mode.SRC_ATOP)

        setSupportActionBar(toolbar)
        supportActionBar?.title = null
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(homeIndicator)
        toolbar.setNavigationOnClickListener { drawerLayout.openDrawer(GravityCompat.START) }
        toolbarTitleView = toolbar.findViewById(R.id.titleView)

        setupHeader()

        val actionBarToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.status_drawer_open,
            R.string.status_drawer_closed)
        drawerLayout.addDrawerListener(actionBarToggle)
        actionBarToggle.syncState()

        transferReceiver = object: BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                val receivedID: Long = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)!!
                if (receivedID == downloadID){
                    sendNotification(NOTIFICATION_TYPE_FINISHED, getString(R.string.notification_download_finished))
                } else
                    Log.e("DataFetchError", "Error Fetching File")
            }
        }

        if (Preferences(this).theme == Preferences.THEME_LIGHT){
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
        executeWorker()
    }

    private fun setupHeader(){
        val headerLayout = navigationView.getHeaderView(0)
        val imageView: AppCompatImageView = headerLayout.findViewById(R.id.imageView)
        val titleView: TextView = headerLayout.findViewById(R.id.titleView)
        val subtitleView: TextView = headerLayout.findViewById(R.id.subtitleView)

        with(Account(this)){
            titleView.text = fullName
            subtitleView.text = email

            GlideApp.with(this@MainActivity)
                .load(imageURL)
                .centerCrop()
                .into(imageView)
        }
    }

    private fun executeWorker(){
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = OneTimeWorkRequestBuilder<SupportWorker>()
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance().enqueue(request)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        searchMenuItem = menu?.findItem(R.id.action_search)

        searchView = searchMenuItem?.actionView as SearchView
        searchView?.setOnQueryTextListener(this)

        return true
    }

    fun initializeSearch(listener: ScreenAction.Search){
        searchListener = listener
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        searchListener?.onSearch(newText)
        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
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
            navigationSaved -> setToolbarTitle(R.string.navigation_saved)
            navigationItemNotification -> setToolbarTitle(R.string.navigation_notifications)
            navigationItemSettings -> setToolbarTitle(R.string.navigation_settings)
        }

        searchMenuItem?.isVisible = item != navigationItemSettings
    }

    private fun setToolbarTitle(int: Int) {
        toolbarTitleView?.text = getString(int)
    }

    private fun getFragment(item: Int?): Fragment? {
        return when (item){
            navigationItemFiles -> FilesFragment()
            navigationItemBoxes -> BoxesFragment()
            navigationSaved -> SavedFragment()
            navigationItemNotification -> NotificationFragment()
            navigationItemSettings -> SettingsFragment()
            else -> null
        }
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START)
        else
            super.onBackPressed()
    }

    override fun onStart() {
        super.onStart()

        registerReceiver(transferReceiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
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
                            Permissions.WRITE_REQUEST)
                    }
                }
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    Permissions.WRITE_REQUEST)
            }
        } else
            replaceFragment(selectedItem ?: navigationItemFiles)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode){
            Permissions.WRITE_REQUEST -> {
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
            R.id.navigation_collections -> replaceFragment(navigationSaved)
            R.id.navigation_notifications -> replaceFragment(navigationItemNotification)
            R.id.navigation_settings -> replaceFragment(navigationItemSettings)
            R.id.navigation_about -> startActivity(Intent(this, AboutActivity::class.java))
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("savedTab", selectedItem!!)
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(transferReceiver)
    }
}
