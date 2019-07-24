package com.isaiahvonrundstedt.bucket.fragments.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseFragment
import com.isaiahvonrundstedt.bucket.fragments.navigation.box.SentFragment
import com.isaiahvonrundstedt.bucket.fragments.navigation.box.SharedFragment
import kotlinx.android.synthetic.main.fragment_box_main.*

class BoxesFragment: BaseFragment() {

    private var selectedItem: Int? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_box_main, container, false)
    }

    override fun onResume() {
        super.onResume()

        onLoadFragment(selectedItem ?: R.id.navigation_shared)

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            onLoadFragment(item.itemId)
            true
        }
    }

    override fun onPause() {
        super.onPause()

        selectedItem = bottomNavigationView.selectedItemId
    }

    private fun onLoadFragment(itemId: Int){
        val fragment: Fragment? =
            when (itemId){
                R.id.navigation_shared -> SharedFragment()
                R.id.navigation_sent -> SentFragment()
                else -> null
            }

        if (fragment != null) {
            childFragmentManager.beginTransaction().run {
                replace(R.id.containerLayout, fragment)
                addToBackStack(null)
                commit()
            }
        }
    }

}