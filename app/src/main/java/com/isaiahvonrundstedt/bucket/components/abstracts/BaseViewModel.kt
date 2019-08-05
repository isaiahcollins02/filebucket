package com.isaiahvonrundstedt.bucket.components.abstracts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class BaseViewModel: ViewModel() {

    protected var _itemSize: MutableLiveData<Int> = MutableLiveData()
    internal var itemSize: LiveData<Int> = _itemSize

    protected var _dataState: MutableLiveData<Int> = MutableLiveData()
    internal var dataState: LiveData<Int> = _dataState

    companion object {
        const val stateDataPreparing = 0
        const val stateDataReady = 1
    }

    abstract fun fetch()
    abstract fun refresh()

}