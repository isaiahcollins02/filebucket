package com.isaiahvonrundstedt.bucket.features.boxes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.isaiahvonrundstedt.bucket.features.shared.abstracts.BaseViewModel
import com.isaiahvonrundstedt.bucket.features.auth.Account

class BoxViewModel: BaseViewModel() {

    private val repository = BoxStore()

    private var initialList = mutableListOf<Account>()
    private var _itemList: MutableLiveData<List<Account>> = MutableLiveData()
    internal var itemList: LiveData<List<Account>> = _itemList

    init {
        fetch()
        _dataState.postValue(stateDataPreparing)
    }

    override fun fetch(){
        repository.fetch { items ->
            initialList.addAll(items)
            initialList.distinctBy { it.accountID }.toMutableList()
            _itemList.postValue(initialList)

            _itemSize.postValue(items.size)
            _dataState.postValue(stateDataReady)
        }
    }

    override fun refresh(){
        repository.refresh()
        initialList.clear()
        fetch()
    }

}