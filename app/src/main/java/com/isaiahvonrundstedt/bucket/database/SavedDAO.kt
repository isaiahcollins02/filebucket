package com.isaiahvonrundstedt.bucket.database

import androidx.room.*
import com.isaiahvonrundstedt.bucket.features.shared.StorageItem
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
    suspend fun itemByID(@NotNull id: String?): List<StorageItem>

    @Query("SELECT * FROM collections")
    suspend fun storageItems(): List<StorageItem>

    @Query("DELETE FROM collections")
    suspend fun removeEverything()

    @Transaction
    suspend fun checkIfExists(item: StorageItem?): Boolean {
        val itemList = ArrayList<StorageItem>()
        itemList.addAll(itemByID(item?.id))
        return itemList.isNotEmpty()
    }

}