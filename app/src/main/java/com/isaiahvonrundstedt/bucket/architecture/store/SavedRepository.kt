package com.isaiahvonrundstedt.bucket.architecture.store

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import com.isaiahvonrundstedt.bucket.architecture.database.AppDatabase
import com.isaiahvonrundstedt.bucket.architecture.database.SavedDAO
import com.isaiahvonrundstedt.bucket.objects.File

class SavedRepository (application: Application){

    private var appDB: AppDatabase? = null
    private var savedDAO: SavedDAO? = null
    private var collectionLiveData: LiveData<List<File>>

    init {
        appDB = AppDatabase.getDatabase(application)
        savedDAO = appDB?.collectionAccessor()
        collectionLiveData = savedDAO?.getFiles()!!
    }

    fun insert(file: File){
        InsertTask(savedDAO!!).execute(file)
    }

    fun remove(file: File){
        RemoveTask(savedDAO!!).execute(file)
    }

    fun getCollections(): LiveData<List<File>> = collectionLiveData

    private class InsertTask(private var dao: SavedDAO): AsyncTask<File, Void, Void>() {
        override fun doInBackground(vararg params: File): Void? {
            dao.insert(params[0])
            return null
        }
    }

    private class RemoveTask(private var dao: SavedDAO): AsyncTask<File, Void, Void>(){
        override fun doInBackground(vararg params: File): Void? {
            dao.remove(params[0])
            return null
        }
    }

}