package com.isaiahvonrundstedt.bucket.architecture.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.isaiahvonrundstedt.bucket.architecture.store.BoxesRepository
import com.isaiahvonrundstedt.bucket.objects.core.Account

class BoxesViewModel(app: Application): AndroidViewModel(app) {

    private val repository = BoxesRepository()
    private var initialList = mutableListOf<Account>()
    private var _items: MutableLiveData<List<Account>> = MutableLiveData()
    internal var itemList: LiveData<List<Account>> = _items

    init {
        repository.fetch { accountList ->
            initialList.addAll(accountList)
            initialList.distinctBy { it.accountID }.toMutableList()
            _items.postValue(initialList)
        }
    }

    fun refresh(){
        repository.refresh()
        initialList.clear()
    }

}