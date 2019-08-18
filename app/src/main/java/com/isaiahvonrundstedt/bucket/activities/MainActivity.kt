package com.isaiahvonrundstedt.bucket.activities

import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseActivity
import com.isaiahvonrundstedt.bucket.fragments.bottomsheet.OverflowBottomSheet
import com.isaiahvonrundstedt.bucket.fragments.navigation.*
import com.isaiahvonrundstedt.bucket.fragments.screendialog.SearchFragment
import com.isaiahvonrundstedt.bucket.receivers.NetworkReceiver
import com.isaiahvonrundstedt.bucket.service.SupportService
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity(), LifecycleOwner, BottomNavigationView.OnNavigationItemSelectedListener,
    NetworkReceiver.ConnectivityListener {

    override fun onNetworkChanged(status: Int) {
        if (status == NetworkReceiver.typeNotConnected){
            val snackbar = Snackbar.make(window.decorView.rootView, R.string.status_network_no_internet, Snackbar.LENGTH_SHORT)
            snackbar.setAction(R.string.button_go_to_settings) {
                // After the Android Q APIs is finalized implement in-app
                // settings panel
                startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
            }
            snackbar.show()
        }
    }

    private var selectedItem: Int? = null
    private var toolbarTitleView: AppCompatTextView? = null

    companion object {
        const val navigationItemCloud = 0
        const val navigationItemBoxes = 1
        const val navigationItemSaved = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setPersistentActionBar()

        selectedItem = savedInstanceState?.getInt("savedTab")
        startService(Intent(this, SupportService::class.java)
            .setAction(SupportService.actionFetchPayload))
    }

    private fun setPersistentActionBar() {
        val rootView: ViewGroup? = findViewById(R.id.action_bar_root)
        if (rootView != null){
            val view: View = LayoutInflater.from(this).inflate(R.layout.layout_appbar_main, rootView, false)
            rootView.addView(view, 0)

            val toolbar: Toolbar = rootView.findViewById(R.id.toolbar)
            toolbar.setNavigationIcon(R.drawable.ic_android_menu)
            setSupportActionBar(toolbar)

            toolbar.setNavigationOnClickListener {
                val overflowSheet = OverflowBottomSheet()
                overflowSheet.invoke(supportFragmentManager)
            }
            supportActionBar?.title = null
            toolbarTitleView = findViewById(R.id.titleView)
        }
    }

    override fun onStart() {
        super.onStart()

        registerReceiver(NetworkReceiver(), IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        navigationView.setOnNavigationItemSelectedListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId){
            R.id.action_search -> SearchFragment().invoke(supportFragmentManager)
        }
        return true
    }

    private var startingPosition = 0
    private fun replaceFragment(item: Int?, newPosition: Int): Boolean {
        if (item != null){
            selectedItem = item
            when {
                newPosition == 0 -> supportFragmentManager.beginTransaction().run {
                    replace(R.id.containerLayout, getFragment(item)!!)
                    commit()
                }
                startingPosition > newPosition -> supportFragmentManager.beginTransaction().run {
                    setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                    replace(R.id.containerLayout, getFragment(item)!!)
                    commit()
                }
                startingPosition < newPosition -> supportFragmentManager.beginTransaction().run {
                    setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                    replace(R.id.containerLayout, getFragment(item)!!)
                    commit()
                }
            }
            startingPosition = newPosition

            when (item){
                navigationItemCloud -> setToolbarTitle(R.string.navigation_cloud)
                navigationItemBoxes -> setToolbarTitle(R.string.navigation_boxes)
                navigationItemSaved -> setToolbarTitle(R.string.navigation_saved)
            }
            return true
        }
        return false
    }

    private fun setToolbarTitle(int: Int) {
        toolbarTitleView?.text = getString(int)
    }

    private fun getFragment(item: Int?): Fragment? {
        return when (item){
            navigationItemCloud -> CloudFragment()
            navigationItemBoxes -> BoxesFragment()
            navigationItemSaved -> SavedFragment()
            else -> null
        }
    }

    override fun onResume() {
        super.onResume()

        NetworkReceiver.connectivityListener = this
        replaceFragment(selectedItem ?: navigationItemCloud, 0)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("savedTab", selectedItem!!)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var selectedFragment: Int? = null
        var newPosition = 0
        when (item.itemId){
            R.id.navigation_cloud -> {
                selectedFragment = navigationItemCloud
                newPosition = 1
            }
            R.id.navigation_boxes -> {
                selectedFragment = navigationItemBoxes
                newPosition = 2
            }
            R.id.navigation_saved -> {
                selectedFragment = navigationItemSaved
                newPosition = 3
            }
        }
        return replaceFragment(selectedFragment, newPosition)
    }

}
