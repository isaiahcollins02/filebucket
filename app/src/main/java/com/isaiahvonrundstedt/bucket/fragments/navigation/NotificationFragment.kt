package com.isaiahvonrundstedt.bucket.fragments.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.adapters.support.NotificationAdapter
import com.isaiahvonrundstedt.bucket.architecture.viewmodel.NotificationViewModel
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseFragment
import com.isaiahvonrundstedt.bucket.objects.core.Notification

class NotificationFragment: BaseFragment() {

    private var viewModel: NotificationViewModel? = null

    private lateinit var rootView: View
    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshContainer: SwipeRefreshLayout
    private lateinit var adapter: NotificationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(NotificationViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_notifications, container, false)

        adapter = NotificationAdapter()
        recyclerView = rootView.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(rootView.context)
        recyclerView.adapter = adapter

        swipeRefreshContainer = rootView.findViewById(R.id.swipeRefreshContainer)
        swipeRefreshContainer.setColorSchemeResources(
            R.color.colorIndicatorBlue,
            R.color.colorIndicatorRed,
            R.color.colorIndicatorGreen,
            R.color.colorIndicatorYellow
        )

        return rootView
    }
    override fun onResume() {
        super.onResume()
        onLoadAssets()
    }
    private fun onLoadAssets() {
        viewModel?.items?.observe(this, Observer<List<Notification>> { t: List<Notification> ->
            adapter.setItems(t)
        })

        if (swipeRefreshContainer.isRefreshing)
            swipeRefreshContainer.isRefreshing = false
    }
}