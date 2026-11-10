package com.adotarpet.campinas.repository.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.adotarpet.campinas.model.HistoryEntry
import com.adotarpet.campinas.model.HistoryType
import com.adotarpet.campinas.model.RequestStatus

@Entity(tableName = "history", indices = [Index("timestamp")])
data class HistoryEntryEntity(
    @PrimaryKey val id: String,
    val type: HistoryType,
    val title: String,
    val subtitle: String,
    val timestamp: Long,
    @ColumnInfo(defaultValue = "EM_ANALISE")
    val status: RequestStatus = RequestStatus.EM_ANALISE,
    @ColumnInfo(defaultValue = "")
    val details: String = "",
    @ColumnInfo(defaultValue = "")
    val motivoCancelamento: String = ""
)

fun HistoryEntryEntity.toModel(): HistoryEntry =
    HistoryEntry(id, type, title, subtitle, timestamp, status, details, motivoCancelamento)

fun HistoryEntry.toEntity(): HistoryEntryEntity =
    HistoryEntryEntity(id, type, title, subtitle, timestamp, status, details, motivoCancelamento)
