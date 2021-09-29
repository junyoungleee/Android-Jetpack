package com.june.archpractice.model.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.june.archpractice.model.entity.Contact
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(contact: Contact)

    @Delete
    suspend fun delete(contact: Contact)

    @Query("SELECT * FROM contact ORDER BY name ASC")
    fun getAll(): Flow<List<Contact>>

    @Query("DELETE FROM contact")
    suspend fun deleteAll()
}