package com.isaiahvonrundstedt.bucket.architecture.viewmodel.recycler.network

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.isaiahvonrundstedt.bucket.architecture.store.network.BoxStore
import com.isaiahvonrundstedt.bucket.objects.core.Account

class BoxesViewModel: ViewModel() {

    private val repository = BoxStore()
    private var initialList = mutableListOf<Account>()
    private var _items: MutableLiveData<List<Account>> = MutableLiveData()
    internal var itemList: LiveData<List<Account>> = _items

    init {
        fetch()
    }

    fun fetch(){
        repository.fetch { accountList ->
            initialList.addAll(accountList)
            initialList.distinctBy { it.accountID }.toMutableList()
            _items.postValue(initialList)
        }
    }

    val isEmpty: Boolean
        get() = initialList.isEmpty()
    val size: Int
        get() = initialList.size

    fun size(): Int = repository.size()

    fun refresh(){
        repository.refresh()
        initialList.clear()
        fetch()
    }

}