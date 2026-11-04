package com.adotarpet.campinas.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.adotarpet.campinas.model.HistoryEntry
import com.adotarpet.campinas.model.HistoryType

/** Tabela do histórico (adoções, resgates, doações...). */
@Entity(tableName = "history", indices = [Index("timestamp")])
data class HistoryEntryEntity(
    @PrimaryKey val id: String,
    val type: HistoryType,
    val title: String,
    val subtitle: String,
    val timestamp: Long
)

fun HistoryEntryEntity.toModel(): HistoryEntry = HistoryEntry(id, type, title, subtitle, timestamp)

fun HistoryEntry.toEntity(): HistoryEntryEntity = HistoryEntryEntity(id, type, title, subtitle, timestamp)
