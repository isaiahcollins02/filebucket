package com.isaiahvonrundstedt.bucket.architecture.store

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.isaiahvonrundstedt.bucket.architecture.database.AppDatabase
import com.isaiahvonrundstedt.bucket.architecture.database.SavedDAO
import com.isaiahvonrundstedt.bucket.objects.core.File
import kotlinx.coroutines.runBlocking

class SavedRepository (application: Application){

    private var appDB: AppDatabase? = null
    private var savedDAO: SavedDAO? = null
    private lateinit var collectionLiveData: LiveData<List<File>>

    init {
        appDB = AppDatabase.getDatabase(application)
        savedDAO = appDB?.collectionAccessor()
        fetch { collectionLiveData = it }
    }

    private fun fetch( onFetch: (LiveData<List<File>>) -> Unit) = runBlocking {
        val itemList = savedDAO?.getFiles()
        val mutableLiveData: MutableLiveData<List<File>> = MutableLiveData()
        mutableLiveData.value = itemList
        onFetch(mutableLiveData)
    }

    fun insert(file: File) = runBlocking {
        savedDAO?.insert(file)
    }

    fun remove(file: File) = runBlocking {
        savedDAO?.remove(file)
    }

    fun getCollections(): LiveData<List<File>> = collectionLiveData
}