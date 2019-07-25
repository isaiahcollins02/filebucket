package com.isaiahvonrundstedt.bucket.architecture.viewmodel.network

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.isaiahvonrundstedt.bucket.architecture.store.network.FileStore
import com.isaiahvonrundstedt.bucket.objects.core.File

class FileViewModel(authorParams: String?): ViewModel() {

    private val repository = FileStore(authorParams)
    private var initialList = mutableListOf<File>()
    private var _items: MutableLiveData<List<File>> = MutableLiveData()
    internal var itemList: LiveData<List<File>> = _items

    init {
        fetch()
    }

    fun fetch(){
        repository.fetch { fileList ->
            initialList.addAll(fileList)
            initialList.distinctBy { it.fileID }.toMutableList()
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