package com.isaiahvonrundstedt.bucket.core.utils

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.google.firebase.Timestamp
import com.isaiahvonrundstedt.bucket.core.objects.File
import java.util.*
import kotlin.collections.ArrayList

class Database(var context: Context) {

    private var sqLiteDatabase: SQLiteDatabase? = null

    init {
        sqLiteDatabase = context.openOrCreateDatabase(localCache, Context.MODE_PRIVATE, null)
    }

    fun saveToCollections(file: File?){
        val timestamp = convertTimestamp(file?.timestamp!!)

        sqLiteDatabase?.run {
            beginTransaction()
            execSQL("CREATE TABLE IF NOT EXISTS collections (fileID VARCHAR (100), fileName VARCHAR (500), fileAuthor VARCHAR(500), fileType INT, fileSize DOUBLE, downloadURL VARCHAR(500), timestamp INT)")
            execSQL("INSERT OR IGNORE INTO collections VALUES ('${file.id}', '${file.name}', '${file.author}', ${file.fileType}, ${file.fileSize}, '${file.downloadURL}', $timestamp)")
            setTransactionSuccessful()
            endTransaction()
            close()
        }
    }

    fun removeFromCollections(file: File?){
        sqLiteDatabase?.run {
            beginTransaction()
            execSQL("DELETE FROM collections WHERE fileID = '${file?.id}'")
            setTransactionSuccessful()
            endTransaction()
            close()
        }
    }

    fun checkFromCollections(file: File?): Boolean {
        val items: ArrayList<File> = ArrayList()

        val cursor: Cursor = sqLiteDatabase!!.rawQuery("SELECT * FROM collections", null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast){
            val newFile: File = File().apply {
                id = cursor.getString(cursor.getColumnIndex(COLUMN_FILE_ID))
                name = cursor.getString(cursor.getColumnIndex(COLUMN_FILE_NAME))
                author = cursor.getString(cursor.getColumnIndex(COLUMN_FILE_AUTHOR))
                downloadURL = cursor.getString(cursor.getColumnIndex(COLUMN_FILE_DOWNLOAD_URL))
                fileType = cursor.getInt(cursor.getColumnIndex(COLUMN_FILE_TYPE))
                fileSize = cursor.getDouble(cursor.getColumnIndex(COLUMN_FILE_SIZE))
                timestamp = convertLong(cursor.getInt(cursor.getColumnIndex(COLUMN_FILE_TIMESTAMP)).toLong())
            }
            items.add(newFile)
            cursor.moveToNext()
        }
        cursor.close()
        sqLiteDatabase!!.close()

        var itemStatus = false
        items.forEachIndexed { _, iteratedFile ->
            itemStatus = iteratedFile.id == file?.id
        }
        return itemStatus
    }

    fun convertLong(long: Long): Timestamp {
        return Timestamp(Date(long))
    }

    private fun convertTimestamp(timestamp: Timestamp): Long {
        return timestamp.toDate().time
    }

    companion object {
        const val localCache = "appDB"

        const val COLUMN_FILE_NAME = "fileName"
        const val COLUMN_FILE_AUTHOR = "fileAuthor"
        const val COLUMN_FILE_ID = "fileID"
        const val COLUMN_FILE_TYPE = "fileType"
        const val COLUMN_FILE_SIZE = "fileSize"
        const val COLUMN_FILE_TIMESTAMP = "timestamp"
        const val COLUMN_FILE_DOWNLOAD_URL = "downloadURL"
    }

}