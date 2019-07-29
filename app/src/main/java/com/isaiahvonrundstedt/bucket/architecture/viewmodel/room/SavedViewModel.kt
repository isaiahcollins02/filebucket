package com.isaiahvonrundstedt.bucket.architecture.viewmodel.room

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.isaiahvonrundstedt.bucket.architecture.store.room.SavedStore
import com.isaiahvonrundstedt.bucket.objects.core.StorageItem

class SavedViewModel(application: Application): AndroidViewModel(application) {

    private val repository = SavedStore(application)

    private var initialList: ArrayList<StorageItem> = ArrayList()
    private var _itemList: MutableLiveData<List<StorageItem>> = MutableLiveData()
    internal var itemList: LiveData<List<StorageItem>> = _itemList

    private var _itemSize: MutableLiveData<Int> = MutableLiveData()
    internal var itemSize: LiveData<Int> = _itemSize

    fun fetch(){
        repository.fetch { items ->
            initialList.addAll(items)
            initialList.distinctBy { it.id }.toMutableList()
            _itemList.postValue(initialList)

            _itemSize.postValue(items.size)
        }
    }

}