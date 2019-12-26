package com.isaiahvonrundstedt.bucket.features.core.file

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.isaiahvonrundstedt.bucket.features.shared.abstracts.BaseViewModel
import com.isaiahvonrundstedt.bucket.features.shared.StorageItem

class FileViewModel(authorParams: String?): BaseViewModel() {

    private val repository = FileStore(authorParams)

    private var initialList = mutableListOf<StorageItem>()
    private var _itemList: MutableLiveData<List<StorageItem>> = MutableLiveData()
    internal var itemList: LiveData<List<StorageItem>> = _itemList

    init {
        fetch()
        _dataState.postValue(stateDataPreparing)
    }

    override fun fetch() {
        repository.fetch { items ->
            initialList.addAll(items)
            initialList.distinctBy { it.id }.toMutableList()
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