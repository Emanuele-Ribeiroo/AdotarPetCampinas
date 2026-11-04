package com.adotarpet.campinas.data

import android.content.Context
import com.adotarpet.campinas.data.local.AppDatabase
import com.adotarpet.campinas.data.local.dao.HistoryDao
import com.adotarpet.campinas.data.local.entity.toEntity
import com.adotarpet.campinas.data.local.entity.toModel
import com.adotarpet.campinas.model.HistoryEntry
import com.adotarpet.campinas.model.HistoryType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.util.UUID

/** Histórico do que o usuário fez no app (adoções, resgates, doações...), guardado no banco Room. */
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
            // Importa uma vez o histórico do formato antigo (SharedPreferences) e apaga de lá.
            prefs.getString(KEY_LEGACY, null)?.let { json ->
                runCatching { parse(json) }.getOrNull()?.let { list -> historyDao.insertAll(list.map { it.toEntity() }) }
                prefs.edit().remove(KEY_LEGACY).apply()
            }
            historyDao.observeAll().collect { list -> _entries.value = list.map { it.toModel() } }
        }
    }

    fun add(type: HistoryType, title: String, subtitle: String = "") {
        val entry = HistoryEntry(UUID.randomUUID().toString(), type, title, subtitle, System.currentTimeMillis())
        write { insert(entry.toEntity()) }
    }

    fun clear() = write { deleteAll() }

    private fun write(block: suspend HistoryDao.() -> Unit) {
        val historyDao = dao ?: return
        scope.launch { historyDao.block() }
    }

    /** Lê o histórico do formato antigo em JSON; usado só para importar para o Room. */
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
