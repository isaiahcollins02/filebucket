package com.isaiahvonrundstedt.bucket.activities.support

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.activities.wrapper.FrameActivity
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseActivity
import com.isaiahvonrundstedt.bucket.components.modules.GlideApp
import com.isaiahvonrundstedt.bucket.service.TransferService
import com.isaiahvonrundstedt.bucket.utils.Client
import gun0912.tedbottompicker.TedBottomPicker

class ProfileActivity: BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var userID: String? = null

    private val itemList: ArrayList<String> = ArrayList()
    private val firebaseAuth = FirebaseAuth.getInstance()

    private lateinit var imageView: AppCompatImageView
    private lateinit var nameView: TextView
    private lateinit var emailView: TextView
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        userID = firebaseAuth.currentUser?.uid!!

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setToolbarTitle(resources.getString(R.string.activity_account_settings))

        nameView = findViewById(R.id.nameView)
        emailView = findViewById(R.id.emailView)
        imageView = findViewById(R.id.profileView)
        navigationView = findViewById(R.id.navigationView)
    }

    override fun onStart() {
        super.onStart()
        navigationView.setNavigationItemSelectedListener(this)
    }

    override fun onResume() {
        super.onResume()

        Client(this).run {
            nameView.text = fullName
            emailView.text = email
        }

        GlideApp.with(this)
            .load(Client(this).imageURL)
            .placeholder(R.drawable.ic_brand_user)
            .error(R.drawable.ic_brand_user)
            .centerCrop()
            .apply(RequestOptions().circleCrop())
            .into(imageView)

        imageView.setOnClickListener {
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
                .show { uri ->
                    startService(Intent(this, TransferService::class.java)
                        .putExtra(TransferService.EXTRA_FILE_URI, uri)
                        .putExtra(TransferService.UPLOAD_TYPE, TransferService.TYPE_PROFILE)
                        .setAction(TransferService.ACTION_UPLOAD))
                }
        }
    }

    override fun onPause() {
        super.onPause()
        itemList.clear()
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