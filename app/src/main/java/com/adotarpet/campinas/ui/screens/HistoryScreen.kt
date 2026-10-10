package com.adotarpet.campinas.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.adotarpet.campinas.data.HistoryRepository
import com.adotarpet.campinas.model.HistoryEntry
import com.adotarpet.campinas.model.HistoryType
import com.adotarpet.campinas.ui.components.BackButton
import com.adotarpet.campinas.ui.theme.CreamCard
import com.adotarpet.campinas.ui.theme.TerracottaDark
import com.adotarpet.campinas.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Lista o que o usuário já fez no app. [tipo] é o nome de um [HistoryType] ou "TODOS". */
@Composable
fun HistoryScreen(tipo: String, onBackClick: () -> Unit) {
    val all by HistoryRepository.entries.collectAsState()
    val type = HistoryType.entries.firstOrNull { it.name == tipo }
    val entries = if (type == null) all else all.filter { it.type == type }

    val title = when (type) {
        HistoryType.ADOCAO -> "Minhas solicitações"
        HistoryType.RESGATE -> "Meus resgates"
        null -> "Meu histórico"
        else -> type.label
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(24.dp)
    ) {
        item {
            BackButton(onBackClick)
            Text("HISTÓRICO", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            Spacer(Modifier.height(4.dp))
            Text(title, style = MaterialTheme.typography.headlineMedium, color = TerracottaDark)
            Spacer(Modifier.height(20.dp))
            if (entries.isEmpty()) {
                Text(
                    "Nada por aqui ainda. Quando você fizer um pedido, aviso ou doação, ele aparece nesta lista.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }
        items(entries, key = { it.id }) { HistoryRow(it) }
    }
}

@Composable
private fun HistoryRow(entry: HistoryEntry) {
    val date = remember(entry.timestamp) {
        SimpleDateFormat("dd/MM/yyyy 'às' HH:mm", Locale("pt", "BR")).format(Date(entry.timestamp))
    }
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CreamCard),
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(entry.type.label.uppercase(), style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            Spacer(Modifier.height(4.dp))
            Text(entry.title, style = MaterialTheme.typography.titleMedium, color = TerracottaDark)
            if (entry.subtitle.isNotBlank()) {
                Text(entry.subtitle, style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(Modifier.height(4.dp))
            Text(date, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
    }
}
