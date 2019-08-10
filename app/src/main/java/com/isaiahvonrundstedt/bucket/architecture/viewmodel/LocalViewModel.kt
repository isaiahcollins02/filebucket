package com.isaiahvonrundstedt.bucket.architecture.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.isaiahvonrundstedt.bucket.architecture.store.LocalStore
import com.isaiahvonrundstedt.bucket.objects.core.StorageItem
import com.isaiahvonrundstedt.bucket.utils.Permissions
import timber.log.Timber

class LocalViewModel(private var app: Application): AndroidViewModel(app){

    private var store: LocalStore? = null

    private val initialList: ArrayList<StorageItem> = ArrayList()
    private val _itemList: MutableLiveData<List<StorageItem>> = MutableLiveData()
    internal var itemList: LiveData<List<StorageItem>> = _itemList

    private val _itemSize: MutableLiveData<Int> = MutableLiveData()
    internal var itemSize: LiveData<Int> = _itemSize

    private var isPermissionGranted: Boolean = false

    init {
        setStorageAccessChanged()
    }

    fun setStorageAccessChanged(){
        isPermissionGranted = Permissions(app).readAccessGranted
        if (isPermissionGranted){
            store = LocalStore(app)
            fetch()
        } else
            Timber.i("Storage Permission still denied")
    }

    private fun fetch() {
        store?.fetch { items ->
            initialList.addAll(items)
            initialList.distinctBy { it.id }.toMutableList()
            _itemList.postValue(initialList)
            _itemSize.postValue(initialList.size)
        }
    }

}