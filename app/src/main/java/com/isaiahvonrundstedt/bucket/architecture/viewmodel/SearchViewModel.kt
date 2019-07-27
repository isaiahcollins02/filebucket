package com.isaiahvonrundstedt.bucket.architecture.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.isaiahvonrundstedt.bucket.architecture.store.network.CoreStore
import com.isaiahvonrundstedt.bucket.objects.core.StorageItem

class SearchViewModel: ViewModel(){

    private val repository = CoreStore()
    private var initialList = mutableListOf<StorageItem>()
    private var _items: MutableLiveData<List<StorageItem>> = MutableLiveData()
    internal var itemList: LiveData<List<StorageItem>> = _items

    init {
        fetch()
    }

    fun filter(search: String?): LiveData<List<StorageItem>> {
        return Transformations.map(itemList) { items ->
            items.filter { it.name?.contains(search!!) ?: false }
        }
    }

    fun fetch(){
        repository.fetch { fileList ->
            initialList.addAll(fileList)
            initialList.distinctBy { it.id }.toMutableList()
            _items.postValue(initialList)
        }
    }

    val size: Int
        get() = initialList.size

    fun refresh(){
        repository.refresh()
        initialList.clear()
        fetch()
    }
}