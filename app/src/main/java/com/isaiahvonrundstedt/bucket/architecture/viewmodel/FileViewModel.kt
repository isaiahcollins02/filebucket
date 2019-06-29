package com.isaiahvonrundstedt.bucket.architecture.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.isaiahvonrundstedt.bucket.architecture.store.FileRepository
import com.isaiahvonrundstedt.bucket.objects.File
import com.isaiahvonrundstedt.bucket.utils.managers.ItemManager

class FileViewModel(app: Application): AndroidViewModel(app) {

    private val repository = FileRepository()
    private var initialList = mutableListOf<File>()
    private var _items: MutableLiveData<List<File>> = MutableLiveData()
    internal var itemList: LiveData<List<File>> = _items

    init {
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

    fun filterByCategory(item: Int){
        val finalList = mutableListOf<File>()
        val filterList = mutableListOf<File>()
        repository.fetch { fileList ->
            if (item != ItemManager.CATEGORY_ALL){
                filterList.addAll(fileList)
                filterList.forEachIndexed { _, file ->
                    if (item == ItemManager.getFileCategory(file.fileType))
                        finalList.add(file)
                }
            } else
                finalList.addAll(fileList)
        }
    }
}