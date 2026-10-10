package com.adotarpet.campinas.data

import android.content.Context
import android.content.SharedPreferences
import com.adotarpet.campinas.model.HistoryEntry
import com.adotarpet.campinas.model.HistoryType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

/** Histórico do que o usuário fez no app (adoções, resgates, doações...), guardado no aparelho. */
object HistoryRepository {
    private var prefs: SharedPreferences? = null
    private val _entries = MutableStateFlow<List<HistoryEntry>>(emptyList())
    val entries: StateFlow<List<HistoryEntry>> = _entries

    fun init(context: Context) {
        if (prefs != null) return
        val p = context.applicationContext.getSharedPreferences("adotar_pet_history", Context.MODE_PRIVATE)
        prefs = p
        _entries.value = runCatching { parse(p.getString("entries", null)) }.getOrDefault(emptyList())
    }

    fun add(type: HistoryType, title: String, subtitle: String = "") {
        val entry = HistoryEntry(UUID.randomUUID().toString(), type, title, subtitle, System.currentTimeMillis())
        _entries.update { listOf(entry) + it }
        prefs?.edit()?.putString("entries", toJson(_entries.value))?.apply()
    }

    fun clear() {
        _entries.value = emptyList()
        prefs?.edit()?.remove("entries")?.apply()
    }

    internal fun toJson(list: List<HistoryEntry>): String = JSONArray().apply {
        list.forEach {
            put(
                JSONObject()
                    .put("id", it.id)
                    .put("type", it.type.name)
                    .put("title", it.title)
                    .put("subtitle", it.subtitle)
                    .put("timestamp", it.timestamp)
            )
        }
    }.toString()

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
}
