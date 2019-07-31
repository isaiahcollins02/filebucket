package com.isaiahvonrundstedt.bucket.architecture.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.isaiahvonrundstedt.bucket.architecture.store.LocalStore
import com.isaiahvonrundstedt.bucket.architecture.store.network.CoreStore
import com.isaiahvonrundstedt.bucket.objects.core.StorageItem
import java.util.*
import kotlin.collections.ArrayList

class SearchViewModel: ViewModel(){

    private val repository = CoreStore()
    private var initialList = mutableListOf<StorageItem>()
    private var _items: MutableLiveData<List<StorageItem>> = MutableLiveData()
    internal var itemList: LiveData<List<StorageItem>> = _items

    init {
        fetch()
    }

    fun filter(searchTerm: String?) {
        val filterList = ArrayList<StorageItem>()
        initialList.forEachIndexed { _, storageItem ->
            if (storageItem.name?.toLowerCase(Locale.getDefault())?.contains(searchTerm.toString()) == true)
                filterList.add(storageItem)
        }
        _items.postValue(filterList)
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