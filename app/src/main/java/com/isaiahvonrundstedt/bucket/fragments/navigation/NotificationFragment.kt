package com.isaiahvonrundstedt.bucket.fragments.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.adapters.support.NotificationAdapter
import com.isaiahvonrundstedt.bucket.architecture.viewmodel.room.NotificationViewModel
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseFragment
import com.isaiahvonrundstedt.bucket.components.custom.ItemDecoration
import com.isaiahvonrundstedt.bucket.objects.core.Notification
import kotlinx.android.synthetic.main.fragment_notifications.*
import kotlinx.android.synthetic.main.layout_empty_no_notification.*

class NotificationFragment: BaseFragment() {

    private var viewModel: NotificationViewModel? = null
    private var adapter: NotificationAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(NotificationViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_notifications, container, false)
    }

    override fun onStart() {
        super.onStart()

        adapter = NotificationAdapter()

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.addItemDecoration(ItemDecoration(context))
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