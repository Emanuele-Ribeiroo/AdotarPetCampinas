package com.adotarpet.campinas.repository.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.adotarpet.campinas.repository.local.entity.HistoryEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {

    @Query("SELECT * FROM history ORDER BY timestamp DESC")
    fun observeAll(): Flow<List<HistoryEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: HistoryEntryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entries: List<HistoryEntryEntity>)

    @Query("UPDATE history SET status = :status, motivoCancelamento = :motivo WHERE id = :id")
    suspend fun cancelar(id: String, status: String, motivo: String)

    @Query("DELETE FROM history")
    suspend fun deleteAll()
}
