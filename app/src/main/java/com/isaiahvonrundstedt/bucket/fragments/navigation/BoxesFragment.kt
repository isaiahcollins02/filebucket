package com.isaiahvonrundstedt.bucket.fragments.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseFragment
import com.isaiahvonrundstedt.bucket.fragments.navigation.box.PublicFragment
import com.isaiahvonrundstedt.bucket.fragments.navigation.box.UserFragment
import kotlinx.android.synthetic.main.fragment_box_main.*

class BoxesFragment: BaseFragment() {

    private var selectedItem: Int? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_box_main, container, false)
    }

    override fun onResume() {
        super.onResume()

        val adapter = ViewPagerAdapter(childFragmentManager)
        adapter.add(TabItem(getString(R.string.tab_box_public), PublicFragment()))
        adapter.add(TabItem(getString(R.string.tab_box_user), UserFragment()))

        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)
    }

    override fun onPause() {
        super.onPause()

        selectedItem = tabLayout.selectedTabPosition
    }

    private data class TabItem (var title: String, var fragment: Fragment)

    private class ViewPagerAdapter(manager: FragmentManager): FragmentPagerAdapter(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        private val tabItems = ArrayList<TabItem>()

        fun add(tabItem: TabItem){
            tabItems.add(tabItem)
        }

        override fun getItem(position: Int): Fragment {
            return tabItems[position].fragment
        }

        override fun getCount(): Int {
            return tabItems.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return tabItems[position].title
        }
    }

}