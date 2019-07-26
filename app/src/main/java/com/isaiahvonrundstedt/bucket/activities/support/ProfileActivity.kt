package com.isaiahvonrundstedt.bucket.activities.support

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.activities.wrapper.FrameActivity
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseAppBarActivity
import com.isaiahvonrundstedt.bucket.components.custom.ItemDecoration
import com.isaiahvonrundstedt.bucket.components.modules.GlideApp
import com.isaiahvonrundstedt.bucket.constants.Params
import com.isaiahvonrundstedt.bucket.interfaces.RecyclerNavigation
import com.isaiahvonrundstedt.bucket.service.TransferService
import com.isaiahvonrundstedt.bucket.utils.Permissions
import com.isaiahvonrundstedt.bucket.utils.User
import gun0912.tedbottompicker.TedBottomPicker
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity: BaseAppBarActivity(), RecyclerNavigation {

    private var userID: String? = null
    private var adapter: NavigationAdapter? = null
    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        setToolbarTitle(R.string.activity_account_settings)

        userID = firebaseAuth.currentUser?.uid

        val itemList = listOf(R.string.profile_secure_account, R.string.profile_reset_password)
        adapter = NavigationAdapter(itemList, this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(ItemDecoration(this))
        recyclerView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()

        User(this).run {
            nameView.text = fullName
            emailView.text = email
        }

        val accountDrawable = ResourcesCompat.getDrawable(resources, R.drawable.ic_user, null)
        accountDrawable?.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP)

        GlideApp.with(this)
            .load(User(this).imageURL)
            .placeholder(accountDrawable)
            .error(accountDrawable)
            .centerCrop()
            .apply(RequestOptions().circleCrop())
            .into(profileView)

        profileView.setOnClickListener {
            if (Permissions(this).readAccessGranted)
                invokePhotoPicker()
            else
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), Permissions.readRequestCode)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == Permissions.readRequestCode && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            invokePhotoPicker()
        else super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun invokePhotoPicker(){
        TedBottomPicker.with(this)
            .setImageProvider { imageView, imageUri ->

                val requestOptions = RequestOptions()
                    .centerCrop()
                    .priority(Priority.NORMAL)

                GlideApp.with(this)
                    .load(imageUri.path)
                    .apply(requestOptions)
                    .into(imageView)
            }.show { uri -> startService(Intent(this, TransferService::class.java)
                .putExtra(TransferService.extraFileURI, uri)
                .putExtra(TransferService.extraAccountID, userID)
                .setAction(TransferService.actionProfile))}
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            android.R.id.home -> super.onBackPressed()
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onItemSelected(index: Int) {
        when (index){
            0 -> startActivity(Intent(this, FrameActivity::class.java)
                .putExtra(Params.viewType, FrameActivity.viewTypePassword))
            1 -> startActivity(Intent(this, FrameActivity::class.java)
                .putExtra(Params.viewType, FrameActivity.viewTypeReset))
        }
    }

    private class NavigationAdapter(private var itemList: List<Int>,
                                private var itemSelection: RecyclerNavigation): RecyclerView.Adapter<NavigationAdapter.ViewHolder>() {

        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
            val rowView: View = LayoutInflater.from(viewGroup.context).inflate(R.layout.layout_item_navigation, viewGroup, false)
            return ViewHolder(rowView)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            viewHolder.titleView.text = viewHolder.itemView.context.getString(itemList[position])
            viewHolder.rootView.setOnClickListener { itemSelection.onItemSelected(position) }
        }

        override fun getItemCount(): Int {
            return itemList.size
        }

        class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
            val rootView: View = itemView.findViewById(R.id.rootView)
            val titleView: AppCompatTextView = itemView.findViewById(R.id.titleView)
        }
    }

}