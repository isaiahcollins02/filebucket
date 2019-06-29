package com.isaiahvonrundstedt.bucket.activities

import android.Manifest
import android.app.DownloadManager
import android.content.*
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.preference.PreferenceManager
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.google.android.material.navigation.NavigationView
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.activities.support.SearchActivity
import com.isaiahvonrundstedt.bucket.architecture.work.SupportWorker
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseActivity
import com.isaiahvonrundstedt.bucket.components.modules.GlideApp
import com.isaiahvonrundstedt.bucket.fragments.navigation.*
import com.isaiahvonrundstedt.bucket.utils.Account
import com.isaiahvonrundstedt.bucket.utils.Permissions
import com.isaiahvonrundstedt.bucket.utils.managers.DataManager
import com.isaiahvonrundstedt.bucket.utils.managers.ItemManager
import com.mancj.materialsearchbar.MaterialSearchBar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_appbar_drawer.*
import kotlinx.android.synthetic.main.layout_header.*

class MainActivity : BaseActivity(), LifecycleOwner, NavigationView.OnNavigationItemSelectedListener,
    SharedPreferences.OnSharedPreferenceChangeListener {

    private var downloadID: Long? = 0L
    private var selectedItem: Int? = 0
    private var searchMenuItem: MenuItem? = null
    private var toolbarTitleView: TextView? = null

    private lateinit var transferReceiver: BroadcastReceiver
    private lateinit var sharedPreferences: SharedPreferences

    companion object {
        const val navigationItemFiles = 0
        const val navigationItemBoxes = 1
        const val navigationSaved = 2
        const val navigationItemNotification = 3
        const val navigationItemAccount = 4
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        selectedItem = savedInstanceState?.getInt("savedTab")

        val homeIndicator: Drawable? = ResourcesCompat.getDrawable(resources, R.drawable.ic_vector_menu, null)
        homeIndicator?.setColorFilter(ContextCompat.getColor(this, R.color.colorAppBarItem), PorterDuff.Mode.SRC_ATOP)

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
        navigationView.setNavigationItemSelectedListener(this)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

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
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId){
            R.id.action_search -> startActivity(Intent(this, SearchActivity::class.java))
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
            navigationSaved -> setToolbarTitle(R.string.navigation_saved)
            navigationItemNotification -> setToolbarTitle(R.string.navigation_notifications)
            navigationItemAccount -> setToolbarTitle(R.string.navigation_account)
        }

        searchMenuItem?.isVisible = item != navigationItemAccount
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
            navigationItemAccount -> AccountFragment()
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
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
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
            R.id.navigation_repositories -> replaceFragment(navigationItemBoxes)
            R.id.navigation_collections -> replaceFragment(navigationSaved)
            R.id.navigation_notifications -> replaceFragment(navigationItemNotification)
            R.id.navigation_account -> replaceFragment(navigationItemAccount)
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
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == "appThemePreference")
            startActivity(Intent(intent))
    }
}
