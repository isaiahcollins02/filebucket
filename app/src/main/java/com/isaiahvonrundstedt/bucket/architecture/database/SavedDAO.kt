package com.isaiahvonrundstedt.bucket.architecture.database

import androidx.room.*
import com.isaiahvonrundstedt.bucket.objects.File
import org.jetbrains.annotations.NotNull

@Dao
interface SavedDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(file: File)

    @Update
    suspend fun update(file: File)

    @Delete
    suspend fun remove(file: File)

    @Query("SELECT * FROM collections WHERE fileID == :id")
    suspend fun getFileByID(@NotNull id: String): List<File>

    @Query("SELECT * FROM collections")
    suspend fun getFiles(): List<File>

    @Query("DELETE FROM collections")
    suspend fun clearAll()

    @Transaction
    suspend fun checkIfExists(file: File?): Boolean {
        val itemList = ArrayList<File>()
        itemList.addAll(getFileByID(file?.fileID!!))
        return itemList.isNotEmpty()
    }

}