package com.isaiahvonrundstedt.bucket.architecture.viewmodel.network

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.isaiahvonrundstedt.bucket.architecture.store.network.BoxStore
import com.isaiahvonrundstedt.bucket.objects.core.Account

class BoxesViewModel: ViewModel() {

    private val repository = BoxStore()

    private var initialList = mutableListOf<Account>()
    private var _itemList: MutableLiveData<List<Account>> = MutableLiveData()
    internal var itemList: LiveData<List<Account>> = _itemList

    private var _itemSize: MutableLiveData<Int> = MutableLiveData()
    internal var itemSize: LiveData<Int> = _itemSize

    init {
        fetch()
    }

    fun fetch(){
        repository.fetch { items ->
            initialList.addAll(items)
            initialList.distinctBy { it.accountID }.toMutableList()
            _itemList.postValue(initialList)

            _itemSize.postValue(items.size)
        }
    }

    fun refresh(){
        repository.refresh()
        initialList.clear()
        fetch()
    }

}