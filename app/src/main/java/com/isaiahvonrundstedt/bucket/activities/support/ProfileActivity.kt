package com.isaiahvonrundstedt.bucket.activities.support

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.activities.wrapper.FrameActivity
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseAppBarActivity
import com.isaiahvonrundstedt.bucket.components.modules.GlideApp
import com.isaiahvonrundstedt.bucket.constants.Params
import com.isaiahvonrundstedt.bucket.interfaces.RecyclerNavigation
import com.isaiahvonrundstedt.bucket.utils.User
import gun0912.tedbottompicker.TedBottomPicker
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity: BaseAppBarActivity(), RecyclerNavigation {

    private var userID: String? = null
    private var adapter: NavigationAdapter? = null
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        setToolbarTitle(R.string.activity_account_settings)

        userID = firebaseAuth.currentUser?.uid!!

        val itemList = listOf(
            NavigationItem(R.drawable.ic_vector_auth, R.string.profile_secure_account),
            NavigationItem(R.drawable.ic_vector_refresh, R.string.profile_reset_password)
        )
        adapter = NavigationAdapter(itemList, this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()

        User(this).run {
            nameView.text = fullName
            emailView.text = email
        }

        GlideApp.with(this)
            .load(User(this).imageURL)
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

    override fun onItemSelected(index: Int) {
        when (index){
            0 -> startActivity(Intent(this, FrameActivity::class.java)
                .putExtra(Params.viewType, FrameActivity.viewTypePassword))
            1 -> startActivity(Intent(this, FrameActivity::class.java)
                .putExtra(Params.viewType, FrameActivity.viewTypeReset))
        }
    }

    private data class NavigationItem(@DrawableRes var iconID: Int, @StringRes var titleID: Int)

    private class NavigationAdapter(private var itemList: List<NavigationItem>,
                                private var itemSelection: RecyclerNavigation): RecyclerView.Adapter<NavigationAdapter.ViewHolder>() {

        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
            val rowView: View = LayoutInflater.from(viewGroup.context).inflate(R.layout.layout_item_navigation, viewGroup, false)
            return ViewHolder(rowView)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            viewHolder.titleView.text = viewHolder.itemView.context.getString(itemList[position].titleID)
            viewHolder.iconView.setImageResource(itemList[position].iconID)
            viewHolder.rootView.setOnClickListener { itemSelection.onItemSelected(position) }
        }

        override fun getItemCount(): Int {
            return itemList.size
        }

        class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
            val rootView: View = itemView.findViewById(R.id.rootView)
            val titleView: TextView = itemView.findViewById(R.id.titleView)
            val iconView: AppCompatImageView = itemView.findViewById(R.id.iconView)
        }
    }

}