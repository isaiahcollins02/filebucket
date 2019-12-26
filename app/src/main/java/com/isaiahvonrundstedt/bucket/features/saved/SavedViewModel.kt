package com.isaiahvonrundstedt.bucket.features.saved

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.isaiahvonrundstedt.bucket.features.shared.StorageItem

class SavedViewModel(application: Application): AndroidViewModel(application) {

    private val repository = SavedStore(application)

    private var initialList: ArrayList<StorageItem> = ArrayList()
    private var _itemList: MutableLiveData<List<StorageItem>> = MutableLiveData()
    internal var itemList: LiveData<List<StorageItem>> = _itemList

    private var _itemSize: MutableLiveData<Int> = MutableLiveData()
    internal var itemSize: LiveData<Int> = _itemSize

    init { fetch() }

    private fun fetch(){
        repository.fetch { items ->
            initialList.addAll(items)
            initialList.distinctBy { it.id }.toMutableList()
            _itemList.postValue(initialList)
            _itemSize.postValue(initialList.size)
        }
    }

    fun refresh(){
        initialList.clear()
        fetch()
    }

}