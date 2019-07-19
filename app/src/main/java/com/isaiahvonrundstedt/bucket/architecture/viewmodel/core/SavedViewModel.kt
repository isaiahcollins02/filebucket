package com.isaiahvonrundstedt.bucket.architecture.viewmodel.core

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.isaiahvonrundstedt.bucket.architecture.store.SavedStore
import com.isaiahvonrundstedt.bucket.objects.core.File

class SavedViewModel(application: Application): AndroidViewModel(application) {

    private val repository = SavedStore(application)
    internal var items: LiveData<List<File>>

    init {
        items = repository.getCollections()
    }
}