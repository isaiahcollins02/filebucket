package com.isaiahvonrundstedt.bucket.architecture.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.isaiahvonrundstedt.bucket.architecture.store.network.CoreStore
import com.isaiahvonrundstedt.bucket.objects.core.StorageItem
import java.util.*
import kotlin.collections.ArrayList

class SearchViewModel: ViewModel(){

    private val repository = CoreStore()

    private var initialList = mutableListOf<StorageItem>()
    private var _itemList: MutableLiveData<List<StorageItem>> = MutableLiveData()
    internal var itemList: LiveData<List<StorageItem>> = _itemList

    private var _itemSize: MutableLiveData<Int> = MutableLiveData()
    internal var itemSize: LiveData<Int> = _itemSize

    init {
        fetch()
    }

    fun filter(searchTerm: String?) {
        val filterList = ArrayList<StorageItem>()
        val filterString = searchTerm.toString().toLowerCase(Locale.getDefault())

        initialList.forEachIndexed { _, storageItem ->
            val itemName = storageItem.name?.toLowerCase(Locale.getDefault())
            val itemAuthor = storageItem.author?.toLowerCase(Locale.getDefault())
            if (itemName?.contains(filterString) == true)
                filterList.add(storageItem)
            else if (itemAuthor?.contains(filterString) == true)
                filterList.add(storageItem)
        }
        _itemList.postValue(filterList)
        _itemSize.postValue(filterList.size)
    }

    fun fetchFiltered(searchTerm: String?){
        fetch()
        if (searchTerm != null) filter(searchTerm)
    }

    fun fetch(){
        repository.fetch { fileList ->
            initialList.addAll(fileList)
            initialList.distinctBy { it.id }.toMutableList()
            _itemList.postValue(initialList)
            _itemSize.postValue(initialList.size)
        }
    }

    fun refresh(){
        repository.refresh()
        initialList.clear()
        fetch()
    }

}