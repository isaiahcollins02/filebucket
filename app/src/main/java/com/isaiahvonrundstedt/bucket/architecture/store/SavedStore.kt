package com.isaiahvonrundstedt.bucket.architecture.store

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.isaiahvonrundstedt.bucket.architecture.database.AppDatabase
import com.isaiahvonrundstedt.bucket.architecture.database.SavedDAO
import com.isaiahvonrundstedt.bucket.objects.core.StorageItem
import kotlinx.coroutines.runBlocking

class SavedStore (application: Application){

    private var appDB: AppDatabase? = null
    private var savedDAO: SavedDAO? = null
    private lateinit var collectionLiveData: LiveData<List<StorageItem>>

    init {
        appDB = AppDatabase.getDatabase(application)
        savedDAO = appDB?.collectionAccessor()
        fetch { collectionLiveData = it }
    }

    private fun fetch( onFetch: (LiveData<List<StorageItem>>) -> Unit) = runBlocking {
        val itemList = savedDAO?.getFiles()
        val mutableLiveData: MutableLiveData<List<StorageItem>> = MutableLiveData()
        mutableLiveData.value = itemList
        onFetch(mutableLiveData)
    }

    fun insert(item: StorageItem) = runBlocking {
        savedDAO?.insert(item)
    }

    fun remove(item: StorageItem) = runBlocking {
        savedDAO?.remove(item)
    }

    fun getCollections(): LiveData<List<StorageItem>> = collectionLiveData
}