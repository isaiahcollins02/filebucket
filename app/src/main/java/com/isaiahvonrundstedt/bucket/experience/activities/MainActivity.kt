package com.isaiahvonrundstedt.bucket.experience.activities

import android.Manifest
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.google.android.material.navigation.NavigationView
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.core.components.abstracts.BaseActivity
import com.isaiahvonrundstedt.bucket.core.components.abstracts.BaseBottomSheet
import com.isaiahvonrundstedt.bucket.core.interfaces.ActionBarInvoker
import com.isaiahvonrundstedt.bucket.core.interfaces.MenuCallback
import com.isaiahvonrundstedt.bucket.core.service.StreamlineService
import com.isaiahvonrundstedt.bucket.core.utils.Client
import com.isaiahvonrundstedt.bucket.core.utils.Permissions
import com.isaiahvonrundstedt.bucket.core.utils.Preferences
import com.isaiahvonrundstedt.bucket.experience.activities.generic.AboutActivity
import com.isaiahvonrundstedt.bucket.experience.activities.generic.SettingsActivity
import com.isaiahvonrundstedt.bucket.experience.fragments.navigation.FilesFragment
import com.isaiahvonrundstedt.bucket.experience.fragments.navigation.RepoFragment
import com.isaiahvonrundstedt.bucket.experience.fragments.navigation.SavedFragment

class
MainActivity : BaseActivity(), SearchView.OnQueryTextListener, MenuCallback, LifecycleOwner {

    private var activeFragment: Fragment? = null
    private var downloadID: Long? = 0L

    private lateinit var searchView: SearchView
    private lateinit var transferReceiver: BroadcastReceiver
    private lateinit var listener: ActionBarInvoker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val homeIndicator: Drawable? = ResourcesCompat.getDrawable(resources, R.drawable.ic_vector_menu, null)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(homeIndicator)

        transferReceiver = object: BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                val receivedID: Long = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)!!
                if (receivedID == downloadID){
                    sendNotification(NOTIFICATION_TYPE_FINISHED, getString(R.string.notification_download_finished))
                } else
                    Log.e("DataFetchError", "Error Fetching File")
            }
        }

        if (Preferences(this).updateNotification){
            this.startService(Intent(this, StreamlineService::class.java)
                .setAction(StreamlineService.ACTION_CHECK))
        }
    }

    fun setSearchListener(listener: ActionBarInvoker){
        this.listener = listener
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        searchView = menu?.findItem(R.id.action_search)!!.actionView as SearchView
        searchView.setOnQueryTextListener(this)

        searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            toolbarTitleView?.isVisible = !hasFocus
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId){
            android.R.id.home -> {
                val drawer = NavigationDrawer()
                drawer.setMenuListener(this)
                drawer.show(supportFragmentManager, "drawerTag")
                true
            } else -> false
        }
    }

    companion object {
        internal const val VIEW_FILES = 0
        internal const val VIEW_REPO = 1
        internal const val VIEW_SAVED = 2
        internal const val VIEW_SETTINGS = 3
        internal const val VIEW_ABOUT = 4
    }

    private fun replaceFragment(fragment: Fragment){
        if (activeFragment != fragment) {
            supportFragmentManager.beginTransaction().run {
                replace(R.id.childLayout, fragment)
                commit()
            }

            when (fragment){
                is FilesFragment -> setToolbarTitle(getString(R.string.navigation_files))
                is SavedFragment -> setToolbarTitle(getString(R.string.navigation_collections))
                is RepoFragment -> setToolbarTitle(getString(R.string.navigation_repositories))
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

    internal class NavigationDrawer: BaseBottomSheet(), NavigationView.OnNavigationItemSelectedListener {

        private var listener: MenuCallback? = null

        private lateinit var rootView: View
        private lateinit var titleView: TextView
        private lateinit var subtitleView: TextView
        private lateinit var navigationView: NavigationView

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            rootView = inflater.inflate(R.layout.layout_sheet_main, container, false)

            titleView = rootView.findViewById(R.id.titleView)
            subtitleView = rootView.findViewById(R.id.subtitleView)
            navigationView = rootView.findViewById(R.id.navigationView)
            return rootView
        }

        fun setMenuListener(listener: MenuCallback){
            this.listener = listener
        }

        override fun onResume() {
            super.onResume()

            Client(rootView.context).let {
                titleView.text = it.fullName
                subtitleView.text = it.email
            }

            navigationView.setCheckedItem(0)
            navigationView.setNavigationItemSelectedListener(this)
        }

        override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
            when (menuItem.itemId){
                R.id.navigation_files -> listener?.onMenuItemSelected(0)
                R.id.navigation_repositories -> listener?.onMenuItemSelected(1)
                R.id.navigation_collections -> listener?.onMenuItemSelected(2)
                R.id.navigation_settings -> listener?.onMenuItemSelected(3)
                R.id.navigation_about -> listener?.onMenuItemSelected(4)
            }
            dismiss()
            return true
        }
    }

    override fun onMenuItemSelected(item: Int?) {
        when (item){
            VIEW_FILES -> replaceFragment(FilesFragment())
            VIEW_REPO -> replaceFragment(RepoFragment())
            VIEW_SAVED -> replaceFragment(SavedFragment())
            VIEW_SETTINGS -> startActivity(Intent(this, SettingsActivity::class.java))
            VIEW_ABOUT -> startActivity(Intent(this, AboutActivity::class.java))
        }
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(transferReceiver)
    }
}
