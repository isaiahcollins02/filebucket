package com.isaiahvonrundstedt.bucket.components.abstracts

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.activities.support.SupportActivity
import com.isaiahvonrundstedt.bucket.utils.Preferences

abstract class BaseActivity: AppCompatActivity() {

    internal var toolbarTitleView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        // Set Activity Theme before creating
        onThemeChanged()
        super.onCreate(savedInstanceState)

        setInterface()
    }

    private fun onThemeChanged(){
        // Get current user theme preference from SharedPreferences API
        // (when null, it defaults to THEME_DEFAULT)
        when (Preferences(this).theme){
            Preferences.THEME_LIGHT -> setTheme(R.style.AppTheme_Core_Light)
            Preferences.THEME_DARK -> setTheme(R.style.AppTheme_Core_Dark)
            Preferences.THEME_AMOLED -> setTheme(R.style.AppTheme_Core_AMOLED)
        }
    }

    private fun setInterface() {
        // Check if the running device is a tablet or a smartphone. Then if its a smartphone
        // lock the orientation to portrait
        val screenLayoutSize =
            this.resources?.configuration?.screenLayout!! and Configuration.SCREENLAYOUT_SIZE_MASK
        if (screenLayoutSize == Configuration.SCREENLAYOUT_SIZE_SMALL || screenLayoutSize == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
            this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        val rootView: ViewGroup? = findViewById(R.id.action_bar_root)

        if (rootView != null){
            val layoutID: Int? = when (Preferences(this).theme){
                Preferences.THEME_LIGHT -> R.layout.layout_appbar_light
                Preferences.THEME_DARK -> R.layout.layout_appbar_dark
                Preferences.THEME_AMOLED -> R.layout.layout_appbar_amoled
                else -> null
            }

            val view: View = LayoutInflater.from(this).inflate(layoutID!!, rootView, false)
            rootView.addView(view, 0)

            val toolbar: Toolbar = findViewById(R.id.toolbar)
            setSupportActionBar(toolbar)

            supportActionBar?.title = null
            toolbarTitleView = toolbar.findViewById(R.id.toolbarTitle)
        }
    }

    internal fun setToolbarTitle(title: String?){
        toolbarTitleView?.text = title
    }

    companion object {
        internal const val CHANNEL_ID_DEFAULT = "default"

        internal const val AVAILABLE_NOTIFICATION_ID = 2
        internal const val NOTIFICATION_TYPE_FINISHED = 1
    }

    val manager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    internal fun sendNotification(type: Int, title: String?){
        createDefaultChannel()

        val icon = if (type == NOTIFICATION_TYPE_FINISHED) R.drawable.ic_vector_check else R.drawable.ic_vector_warning
        val builder = NotificationCompat.Builder(this, CHANNEL_ID_DEFAULT)
            .setSmallIcon(icon)
            .setContentTitle(title)
            .setAutoCancel(true)

        manager.notify(NOTIFICATION_TYPE_FINISHED, builder.build())
    }

    private fun createDefaultChannel(){
        // Since Android O (API Level 26), a notification channel
        // is needed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                CHANNEL_ID_DEFAULT, "Default",
                NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(channel)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_generic, menu)

        if (Preferences(this).theme == Preferences.THEME_LIGHT)
            tintActionBarItem(menu, R.id.action_help, R.color.colorDefault)
        return true
    }

    internal fun tintActionBarItem(menu: Menu?, id: Int, colorID: Int){
        val drawable = menu?.findItem(id)?.icon
        drawable?.setColorFilter(ContextCompat.getColor(this, colorID), PorterDuff.Mode.SRC_ATOP)
        menu?.findItem(id)?.icon = drawable
    }

    internal fun tintItem(){

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId){
            R.id.action_help -> startActivity(Intent(this, SupportActivity::class.java))
        }
        return true
    }

}