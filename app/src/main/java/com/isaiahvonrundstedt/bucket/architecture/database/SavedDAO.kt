package com.isaiahvonrundstedt.bucket.architecture.database

import androidx.room.*
import com.isaiahvonrundstedt.bucket.objects.core.StorageItem
import org.jetbrains.annotations.NotNull

@Dao
interface SavedDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: StorageItem)

    @Update
    suspend fun update(item: StorageItem)

    @Delete
    suspend fun remove(item: StorageItem)

    @Query("SELECT * FROM collections WHERE id == :id")
    suspend fun getFileByID(@NotNull id: String?): List<StorageItem>

    @Query("SELECT * FROM collections")
    suspend fun getFiles(): List<StorageItem>

    @Query("DELETE FROM collections")
    suspend fun clearAll()

    @Transaction
    suspend fun checkIfExists(item: StorageItem?): Boolean {
        val itemList = ArrayList<StorageItem>()
        itemList.addAll(getFileByID(item?.id))
        return itemList.isNotEmpty()
    }

}