package com.adotarpet.campinas.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adotarpet.campinas.viewmodel.AdminReportsViewModel
import com.adotarpet.campinas.model.HistoryType
import com.adotarpet.campinas.view.components.BackButton
import com.adotarpet.campinas.view.theme.CreamCard
import com.adotarpet.campinas.view.theme.Terracotta
import com.adotarpet.campinas.view.theme.TerracottaDark
import com.adotarpet.campinas.view.theme.TextSecondary
import com.adotarpet.campinas.view.theme.White
import com.adotarpet.campinas.util.Report
import com.adotarpet.campinas.util.buildReport
import com.adotarpet.campinas.util.shareText
import com.adotarpet.campinas.util.toText

@Composable
fun AdminReportsView(onBackClick: () -> Unit) {
    val vm: AdminReportsViewModel = viewModel()
    val pets by vm.pets.collectAsState()
    val history by vm.history.collectAsState()
    val report = remember(pets, history) { buildReport(pets, history) }
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(24.dp)
    ) {
        item {
            BackButton(onBackClick)
            Text("ADMINISTRAÇÃO", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            Spacer(Modifier.height(4.dp))
            Text("Relatórios", style = MaterialTheme.typography.headlineMedium, color = TerracottaDark)
            Spacer(Modifier.height(4.dp))
            Text(
                "Considera só os dados deste aparelho (ainda não há servidor).",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )

            Spacer(Modifier.height(20.dp))
            SectionTitle("ANIMAIS")
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Stat("${report.totalPets}", "Total", Modifier.weight(1f))
                Stat("${report.dogs}", "Cães", Modifier.weight(1f))
                Stat("${report.cats}", "Gatos", Modifier.weight(1f))
            }
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Stat("${report.males}", "Machos", Modifier.weight(1f))
                Stat("${report.females}", "Fêmeas", Modifier.weight(1f))
                Stat("${report.favorites}", "Favoritados", Modifier.weight(1f))
            }
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Stat("${report.percent(report.neutered)}%", "Castrados", Modifier.weight(1f))
                Stat("${report.percent(report.vaccinated)}%", "Vacinados", Modifier.weight(1f))
            }

            Spacer(Modifier.height(24.dp))
            SectionTitle("ATIVIDADE")
            HistoryType.entries.forEach { type ->
                ActivityRow(type.label, report.countByType[type] ?: 0)
            }

            Spacer(Modifier.height(24.dp))
            SectionTitle("ÚLTIMOS REGISTROS")
            if (history.isEmpty()) {
                Text("Nenhum registro ainda.", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            }
            history.take(10).forEach { entry ->
                Column(modifier = Modifier.padding(bottom = 10.dp)) {
                    Text(entry.title, style = MaterialTheme.typography.bodyMedium)
                    Text(
                        entry.type.label + if (entry.subtitle.isBlank()) "" else " · ${entry.subtitle}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }

            Spacer(Modifier.height(20.dp))
            Button(
                onClick = { context.shareText(report.toText(history), "Compartilhar relatório") },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = Terracotta, contentColor = White)
            ) {
                Text("Compartilhar relatório")
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.bodySmall,
        color = TextSecondary,
        modifier = Modifier.padding(bottom = 10.dp)
    )
}

@Composable
private fun Stat(value: String, label: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CreamCard)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, style = MaterialTheme.typography.headlineMedium, color = TerracottaDark)
            Text(label, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
    }
}

@Composable
private fun ActivityRow(label: String, count: Int) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text("$count", style = MaterialTheme.typography.titleMedium, color = TerracottaDark)
    }
}
