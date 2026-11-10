package com.adotarpet.campinas.repository

import android.content.Context
import com.adotarpet.campinas.repository.local.AppDatabase
import com.adotarpet.campinas.repository.local.dao.HistoryDao
import com.adotarpet.campinas.repository.local.entity.toEntity
import com.adotarpet.campinas.repository.local.entity.toModel
import com.adotarpet.campinas.model.HistoryEntry
import com.adotarpet.campinas.model.HistoryType
import com.adotarpet.campinas.model.RequestStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.util.UUID

object HistoryRepository {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var dao: HistoryDao? = null
    private val _entries = MutableStateFlow<List<HistoryEntry>>(emptyList())
    val entries: StateFlow<List<HistoryEntry>> = _entries

    fun init(context: Context) {
        if (dao != null) return
        val historyDao = AppDatabase.get(context).historyDao()
        dao = historyDao
        val prefs = context.applicationContext.getSharedPreferences("adotar_pet_history", Context.MODE_PRIVATE)
        scope.launch {

            prefs.getString(KEY_LEGACY, null)?.let { json ->
                runCatching { parse(json) }.getOrNull()?.let { list -> historyDao.insertAll(list.map { it.toEntity() }) }
                prefs.edit().remove(KEY_LEGACY).apply()
            }
            historyDao.observeAll().collect { list -> _entries.value = list.map { it.toModel() } }
        }
    }

    fun add(type: HistoryType, title: String, subtitle: String = "", details: String = "") {
        val entry = HistoryEntry(
            id = UUID.randomUUID().toString(),
            type = type,
            title = title,
            subtitle = subtitle,
            timestamp = System.currentTimeMillis(),
            details = details
        )
        write { insert(entry.toEntity()) }
    }

    fun cancelar(id: String, motivo: String) =
        write { cancelar(id, RequestStatus.CANCELADA.name, motivo) }

    fun clear() = write { deleteAll() }

    private fun write(block: suspend HistoryDao.() -> Unit) {
        val historyDao = dao ?: return
        scope.launch { historyDao.block() }
    }

    internal fun parse(json: String?): List<HistoryEntry> {
        if (json.isNullOrBlank()) return emptyList()
        val array = JSONArray(json)
        return (0 until array.length()).map { i ->
            val o = array.getJSONObject(i)
            HistoryEntry(
                id = o.getString("id"),
                type = HistoryType.valueOf(o.getString("type")),
                title = o.getString("title"),
                subtitle = o.getString("subtitle"),
                timestamp = o.getLong("timestamp")
            )
        }
    }

    private const val KEY_LEGACY = "entries"
}
