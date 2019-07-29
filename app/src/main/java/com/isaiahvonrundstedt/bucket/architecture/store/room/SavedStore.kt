package com.isaiahvonrundstedt.bucket.architecture.store.room

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.isaiahvonrundstedt.bucket.architecture.database.AppDatabase
import com.isaiahvonrundstedt.bucket.architecture.database.SavedDAO
import com.isaiahvonrundstedt.bucket.objects.core.StorageItem
import kotlinx.coroutines.runBlocking

class SavedStore (application: Application){

    private var database = AppDatabase.getDatabase(application)
    private var savedDAO = database?.collectionAccessor()

    fun fetch( onFetch: (List<StorageItem>) -> Unit) = runBlocking {
        onFetch(savedDAO?.getFiles() ?: ArrayList())
    }

    fun insert(item: StorageItem) = runBlocking {
        savedDAO?.insert(item)
    }

    fun remove(item: StorageItem) = runBlocking {
        savedDAO?.remove(item)
    }

}