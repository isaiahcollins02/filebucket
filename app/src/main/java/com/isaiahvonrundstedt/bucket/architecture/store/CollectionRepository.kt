package com.isaiahvonrundstedt.bucket.architecture.store

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import com.isaiahvonrundstedt.bucket.architecture.database.AppDatabase
import com.isaiahvonrundstedt.bucket.architecture.database.CollectionAccessor
import com.isaiahvonrundstedt.bucket.objects.File

class CollectionRepository (application: Application){

    private var appDB: AppDatabase? = null
    private var collectionAccessor: CollectionAccessor? = null
    private var collectionLiveData: LiveData<List<File>>

    init {
        appDB = AppDatabase.getDatabase(application)
        collectionAccessor = appDB?.collectionAccessor()
        collectionLiveData = collectionAccessor?.getFiles()!!
    }

    fun insert(file: File){
        InsertTask(collectionAccessor!!).execute(file)
    }

    fun remove(file: File){
        RemoveTask(collectionAccessor!!).execute(file)
    }

    fun getCollections(): LiveData<List<File>> = collectionLiveData

    private class InsertTask(private var dao: CollectionAccessor): AsyncTask<File, Void, Void>() {
        override fun doInBackground(vararg params: File): Void? {
            dao.insert(params[0])
            return null
        }
    }

    private class RemoveTask(private var dao: CollectionAccessor): AsyncTask<File, Void, Void>(){
        override fun doInBackground(vararg params: File): Void? {
            dao.remove(params[0])
            return null
        }
    }

}