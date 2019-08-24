package com.isaiahvonrundstedt.bucket.activities.support.account

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.adapters.support.NotificationAdapter
import com.isaiahvonrundstedt.bucket.architecture.viewmodel.room.NotificationViewModel
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseAppBarActivity
import kotlinx.android.synthetic.main.activity_notifications.*
import kotlinx.android.synthetic.main.layout_empty_no_notification.*

class NotificationActivity: BaseAppBarActivity() {

    private var viewModel: NotificationViewModel? = null
    private var adapter: NotificationAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)
        setToolbarTitle(R.string.navigation_notifications)

        viewModel = ViewModelProviders.of(this).get(NotificationViewModel::class.java)
    }

    override fun onStart() {
        super.onStart()

        adapter = NotificationAdapter(this)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        recyclerView.adapter = adapter
    }
    override fun onResume() {
        super.onResume()

        viewModel?.itemList?.observe(this, Observer { items ->
            adapter?.setObservableItems(items)
        })

        viewModel?.itemSize?.observe(this, Observer { size ->
            noNotificationView.isVisible = size == 0
        })

    }
}