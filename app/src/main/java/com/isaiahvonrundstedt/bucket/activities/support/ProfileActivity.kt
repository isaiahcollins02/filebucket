package com.isaiahvonrundstedt.bucket.activities.support

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.activities.wrapper.FrameActivity
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseAppBarActivity
import com.isaiahvonrundstedt.bucket.components.modules.GlideApp
import com.isaiahvonrundstedt.bucket.utils.Account
import gun0912.tedbottompicker.TedBottomPicker
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity: BaseAppBarActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var userID: String? = null

    private val firebaseAuth = FirebaseAuth.getInstance()

    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        userID = firebaseAuth.currentUser?.uid!!

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setToolbarTitle(resources.getString(R.string.activity_account_settings))

        navigationView = findViewById(R.id.navigationView)
    }

    override fun onStart() {
        super.onStart()
        navigationView.setNavigationItemSelectedListener(this)
    }

    override fun onResume() {
        super.onResume()

        Account(this).run {
            nameView.text = fullName
            emailView.text = email
        }

        GlideApp.with(this)
            .load(Account(this).imageURL)
            .placeholder(R.drawable.ic_brand_user)
            .error(R.drawable.ic_brand_user)
            .centerCrop()
            .apply(RequestOptions().circleCrop())
            .into(profileView)

        profileView.setOnClickListener {
            TedBottomPicker.with(this)
                .setImageProvider { imageView, imageUri ->

                    val requestOptions = RequestOptions()
                        .centerCrop()
                        .priority(Priority.NORMAL)

                    GlideApp.with(this)
                        .load(imageUri.path)
                        .apply(requestOptions)
                        .into(imageView)
                }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            android.R.id.home -> super.onBackPressed()
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId){
            R.id.navigation_sent -> startActivity(Intent(this, FrameActivity::class.java)
                .putExtra(viewType, FrameActivity.VIEW_TYPE_SENT))
            R.id.navigation_secure -> startActivity(Intent(this, FrameActivity::class.java)
                .putExtra(viewType, FrameActivity.VIEW_TYPE_PASSWORD))
            R.id.navigation_reset -> startActivity(Intent(this, FrameActivity::class.java)
                .putExtra(viewType, FrameActivity.VIEW_TYPE_RESET))
        }
        return true
    }

    companion object {
        private const val viewType = "VIEW_TYPE"
    }

}