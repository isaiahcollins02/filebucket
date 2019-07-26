package com.isaiahvonrundstedt.bucket.architecture.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.isaiahvonrundstedt.bucket.architecture.store.SavedStore
import com.isaiahvonrundstedt.bucket.objects.core.StorageItem

class SavedViewModel(application: Application): AndroidViewModel(application) {

    private val repository = SavedStore(application)
    internal var items: LiveData<List<StorageItem>>

    init {
        items = repository.getCollections()
    }

    val size: Int
        get() = items.value?.size ?: 0

}