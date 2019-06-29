package com.isaiahvonrundstedt.bucket.architecture.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.isaiahvonrundstedt.bucket.objects.File
import org.jetbrains.annotations.NotNull

@Dao
interface SavedDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(file: File)

    @Update
    fun update(file: File)

    @Delete
    fun remove(file: File)

    @Query("SELECT * FROM collections WHERE fileID == :id")
    fun getFileByID(@NotNull id: String): List<File>

    @Query("SELECT * FROM collections")
    fun getFiles(): LiveData<List<File>>

    @Query("DELETE FROM collections")
    fun clearAll()

    @Transaction
    fun checkIfExists(file: File?): Boolean {
        val itemList = ArrayList<File>()
        itemList.addAll(getFileByID(file?.fileID!!))

        return itemList.isNotEmpty()
    }

}