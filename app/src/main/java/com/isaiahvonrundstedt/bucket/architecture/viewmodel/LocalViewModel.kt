package com.isaiahvonrundstedt.bucket.architecture.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.isaiahvonrundstedt.bucket.architecture.store.LocalStore
import com.isaiahvonrundstedt.bucket.objects.core.LocalFile

class LocalViewModel(app: Application): AndroidViewModel(app){

    private val localStore: LocalStore = LocalStore(app)

    private val itemList: ArrayList<LocalFile> = ArrayList()
    private val _items: MutableLiveData<List<LocalFile>> = MutableLiveData()
    internal var items: LiveData<List<LocalFile>> = _items

    init {
        fetch()
    }

    private fun fetch() {
        localStore.fetch { localItems ->
            itemList.addAll(localItems)
            itemList.distinctBy { it.id }.toMutableList()
            _items.postValue(itemList)
        }
    }

    val size: Int
        get() = itemList.size

}