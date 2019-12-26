package com.isaiahvonrundstedt.bucket.features.core

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.isaiahvonrundstedt.bucket.features.shared.abstracts.BaseViewModel
import com.isaiahvonrundstedt.bucket.features.shared.StorageItem

class CoreViewModel: BaseViewModel() {

    private val repository = CoreStore()

    private var initialList = mutableListOf<StorageItem>()
    private var _itemList: MutableLiveData<List<StorageItem>> = MutableLiveData()
    internal var itemList: LiveData<List<StorageItem>> = _itemList

    init {
        _dataState.postValue(stateDataPreparing)
        fetch()
    }

    override fun fetch(){
        repository.fetch { items ->

            initialList.addAll(items)
            initialList.distinctBy { it.id }.toMutableList()
            _itemList.postValue(initialList)

            _itemSize.postValue(initialList.size)

            _dataState.postValue(stateDataReady)
        }
    }

    override fun refresh(){
        repository.refresh()
        initialList.clear()
        fetch()
    }

}