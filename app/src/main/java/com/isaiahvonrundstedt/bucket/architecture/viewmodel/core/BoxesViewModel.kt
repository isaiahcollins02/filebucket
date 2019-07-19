package com.isaiahvonrundstedt.bucket.architecture.viewmodel.core

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.isaiahvonrundstedt.bucket.architecture.store.BoxStore
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

    fun size(): Int = repository.size()

    fun refresh(){
        repository.refresh()
        initialList.clear()
        fetch()
    }

}