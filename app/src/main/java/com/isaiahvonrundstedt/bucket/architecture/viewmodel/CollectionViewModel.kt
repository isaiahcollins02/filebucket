package com.isaiahvonrundstedt.bucket.architecture.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.isaiahvonrundstedt.bucket.architecture.store.CollectionRepository
import com.isaiahvonrundstedt.bucket.objects.File

class CollectionViewModel(application: Application): AndroidViewModel(application) {

    private val repository = CollectionRepository(application)
    internal var items: LiveData<List<File>>

    init {
        items = repository.getCollections()
    }

}