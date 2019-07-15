package com.isaiahvonrundstedt.bucket.architecture.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.isaiahvonrundstedt.bucket.architecture.store.FileRepository
import com.isaiahvonrundstedt.bucket.objects.core.File

class FileViewModel(app: Application): AndroidViewModel(app) {

    private val repository = FileRepository()
    private var initialList = mutableListOf<File>()
    private var _items: MutableLiveData<List<File>> = MutableLiveData()
    internal var itemList: LiveData<List<File>> = _items

    init {
        onLoad()
    }

    fun onLoad(){
        repository.fetch { fileList ->
            initialList.addAll(fileList)
            initialList.distinctBy { it.fileID }.toMutableList()
            _items.postValue(initialList)
        }
    }

    fun refresh(){
        repository.refresh()
        initialList.clear()
    }
}