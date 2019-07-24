package com.isaiahvonrundstedt.bucket.activities.wrapper

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.paolorotolo.appintro.AppIntro
import com.github.paolorotolo.appintro.AppIntroFragment
import com.github.paolorotolo.appintro.model.SliderPage
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.activities.MainActivity

class IntroActivity: AppIntro() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val storagePage = SliderPage()
        storagePage.title = getString(R.string.intro_cloud_storage_title)
        storagePage.description = getString(R.string.intro_cloud_storage_desc)
        storagePage.imageDrawable = R.drawable.ic_brand_cloud
        storagePage.bgColor = ContextCompat.getColor(this, R.color.colorSlideBlue)
        addSlide(AppIntroFragment.newInstance(storagePage))

        val sharePage = SliderPage()
        sharePage.title = getString(R.string.intro_share_people_title)
        sharePage.description = getString(R.string.intro_share_people_desc)
        sharePage.imageDrawable = R.drawable.ic_brand_share
        sharePage.bgColor = ContextCompat.getColor(this, R.color.colorSlideGreen)
        addSlide(AppIntroFragment.newInstance(sharePage))

        val collectionsPage = SliderPage()
        collectionsPage.title = getString(R.string.intro_collections_title)
        collectionsPage.description = getString(R.string.intro_collections_desc)
        collectionsPage.imageDrawable = R.drawable.ic_brand_saved
        collectionsPage.bgColor = ContextCompat.getColor(this, R.color.colorSlideYellow)
        addSlide(AppIntroFragment.newInstance(collectionsPage))

        val permissionPage = SliderPage()
        permissionPage.title= getString(R.string.intro_permissions_title)
        permissionPage.description = getString(R.string.intro_permissions_desc)
        permissionPage.imageDrawable = R.drawable.ic_brand_permissions
        permissionPage.bgColor = ContextCompat.getColor(this, R.color.colorSlideRed)
        addSlide(AppIntroFragment.newInstance(permissionPage))

        showSkipButton(false)
        askForPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), 4)
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        startActivity(Intent(this, MainActivity::class.java))
    }

}