package com.isaiahvonrundstedt.bucket.features.notifications

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class NotificationViewModel(application: Application): AndroidViewModel(application){

    private val store: NotificationStore = NotificationStore(application)

    private var initialList: ArrayList<Notification> = ArrayList()
    private var _itemList: MutableLiveData<List<Notification>> = MutableLiveData()
    internal var itemList: LiveData<List<Notification>> = _itemList

    private var _itemSize: MutableLiveData<Int> = MutableLiveData()
    internal var itemSize: LiveData<Int> = _itemSize

    init { fetch() }

    fun fetch() {
        store.fetch { items ->
            initialList.addAll(items)
            initialList.distinctBy { it.id }.toMutableList()
            _itemList.postValue(initialList)

            _itemSize.postValue(items.size)
        }
    }

    fun refresh(){
        initialList.clear()
        fetch()
    }

}