package com.isaiahvonrundstedt.bucket.activities

import android.Manifest
import android.app.DownloadManager
import android.content.*
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.preference.PreferenceManager
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.architecture.store.NotificationRepository
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseActivityCore
import com.isaiahvonrundstedt.bucket.fragments.navigation.*
import com.isaiahvonrundstedt.bucket.fragments.navigation.AccountFragment
import com.isaiahvonrundstedt.bucket.interfaces.ScreenAction
import com.isaiahvonrundstedt.bucket.service.SupportService
import com.isaiahvonrundstedt.bucket.utils.Permissions
import com.isaiahvonrundstedt.bucket.utils.Preferences
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivityCore(), SearchView.OnQueryTextListener, LifecycleOwner,
    BottomNavigationView.OnNavigationItemSelectedListener, SharedPreferences.OnSharedPreferenceChangeListener  {

    private var actionBarMenu: Menu? = null
    private var activeFragment: Fragment? = null
    private var downloadID: Long? = 0L

    private lateinit var searchView: SearchView
    private lateinit var transferReceiver: BroadcastReceiver
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var listener: ScreenAction.Search

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        transferReceiver = object: BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                val receivedID: Long = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)!!
                if (receivedID == downloadID){
                    sendNotification(NOTIFICATION_TYPE_FINISHED, getString(R.string.notification_download_finished))
                } else
                    Log.e("DataFetchError", "Error Fetching File")
            }
        }

        if (Preferences(this).updateNotification)
            startService(Intent(this, SupportService::class.java)
                .setAction(SupportService.ACTION_CHECK))

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        navigationView.setOnNavigationItemSelectedListener(this)
    }

    fun setSearchListener(listener: ScreenAction.Search){
        this.listener = listener
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        this.actionBarMenu = menu

        tintActionBarItem(menu, R.id.action_search, R.color.colorDefault)
        tintActionBarItem(menu, R.id.action_clear, R.color.colorDefault)
        searchView = menu?.findItem(R.id.action_search)!!.actionView as SearchView
        searchView.setOnQueryTextListener(this)

        searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            toolbarTitleView?.isVisible = !hasFocus
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId){
            R.id.action_clear -> NotificationRepository(application).removeAll()
        }

        return true
    }

    private fun replaceFragment(fragment: Fragment){
        if (activeFragment != fragment) {
            supportFragmentManager.beginTransaction().run {
                replace(R.id.childLayout, fragment)
                commit()
            }

            when (fragment){
                is FilesFragment -> setToolbarTitle(getString(R.string.navigation_files))
                is CollectionFragment -> setToolbarTitle(getString(R.string.navigation_collections))
                is RepositoriesFragment -> setToolbarTitle(getString(R.string.navigation_repositories))
                is NotificationFragment -> setToolbarTitle(getString(R.string.navigation_notifications))
                is AccountFragment -> setToolbarTitle(getString(R.string.navigation_account))
            }

            when (fragment) {
                is NotificationFragment -> {
                    actionBarMenu?.findItem(R.id.action_search)?.isVisible = false
                    actionBarMenu?.findItem(R.id.action_clear)?.isVisible = true
                }
                is AccountFragment -> {
                    actionBarMenu?.findItem(R.id.action_search)?.isVisible = false
                    actionBarMenu?.findItem(R.id.action_clear)?.isVisible = false
                }
                else -> {
                    actionBarMenu?.findItem(R.id.action_search)?.isVisible = true
                    actionBarMenu?.findItem(R.id.action_clear)?.isVisible = false
                }
            }

            activeFragment = fragment
        }
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        listener.onSearch(newText)
        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onBackPressed() {
        when {
            searchView.isIconified -> searchView.isIconified = true
            else -> super.onBackPressed()
        }
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
            replaceFragment(FilesFragment())
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode){
            Permissions.WRITE_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    replaceFragment(FilesFragment())
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onNavigationItemSelected(navigationItem: MenuItem): Boolean {
        when (navigationItem.itemId){
            R.id.navigation_files -> replaceFragment(FilesFragment())
            R.id.navigation_repositories -> replaceFragment(RepositoriesFragment())
            R.id.navigation_collections -> replaceFragment(CollectionFragment())
            R.id.navigation_notifications -> replaceFragment(NotificationFragment())
            R.id.navigation_account -> replaceFragment(AccountFragment())
        }
        return true
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
