package com.isaiahvonrundstedt.bucket.activities.wrapper

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.paolorotolo.appintro.AppIntro
import com.github.paolorotolo.appintro.AppIntroFragment
import com.github.paolorotolo.appintro.model.SliderPage
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.activities.MainActivity

class IntroActivity: AppIntro() {

    private var storageFragment: AppIntroFragment? = null
    private var shareFragment: AppIntroFragment? = null
    private var savedFragment: AppIntroFragment? = null
    private var permissionFragment: AppIntroFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            window?.statusBarColor = introBlue

        storageFragment = AppIntroFragment.newInstance(SliderPage(titleStorage, descriptionStorage, iconStorage, introBlue))
        shareFragment = AppIntroFragment.newInstance(SliderPage(titleShare, descriptionShare, iconShared, introGreen))
        savedFragment = AppIntroFragment.newInstance(SliderPage(titleSaved, descriptionSaved, iconSaved, introYellow))
        permissionFragment = AppIntroFragment.newInstance(SliderPage(titlePermission, descriptionPermission, iconPermission, introRed))

        addSlide(storageFragment!!)
        addSlide(shareFragment!!)
        addSlide(savedFragment!!)
        addSlide(permissionFragment!!)

        showSkipButton(false)
        askForPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), 4)
    }

    override fun onSlideChanged(oldFragment: Fragment?, newFragment: Fragment?) {
        super.onSlideChanged(oldFragment, newFragment)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            when (newFragment){
                storageFragment -> window?.statusBarColor = introBlue
                shareFragment -> window?.statusBarColor = introGreen
                savedFragment -> window?.statusBarColor = introYellow
                permissionFragment -> window?.statusBarColor = introRed
            }
        }
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        startActivity(Intent(this, MainActivity::class.java))
    }

    private val titleStorage: String by lazy { getString(R.string.intro_cloud_storage_title) }
    private val titleShare: String by lazy { getString(R.string.intro_share_people_title) }
    private val titleSaved: String by lazy { getString(R.string.intro_collections_title) }
    private val titlePermission: String by lazy { getString(R.string.intro_permissions_title) }

    private val descriptionStorage: String by lazy { getString(R.string.intro_cloud_storage_desc) }
    private val descriptionShare: String by lazy { getString(R.string.intro_share_people_desc) }
    private val descriptionSaved: String by lazy { getString(R.string.intro_collections_desc) }
    private val descriptionPermission: String by lazy { getString(R.string.intro_permissions_desc) }

    private val iconStorage = R.drawable.ic_brand_cloud
    private val iconShared = R.drawable.ic_brand_share
    private val iconSaved = R.drawable.ic_brand_saved
    private val iconPermission = R.drawable.ic_brand_permissions

    private val introBlue by lazy { ContextCompat.getColor(this, R.color.colorIntroBlue) }
    private val introGreen by lazy { ContextCompat.getColor(this, R.color.colorIntroGreen) }
    private val introYellow by lazy { ContextCompat.getColor(this, R.color.colorIntroYellow) }
    private val introRed by lazy { ContextCompat.getColor(this, R.color.colorIntroRed) }

}