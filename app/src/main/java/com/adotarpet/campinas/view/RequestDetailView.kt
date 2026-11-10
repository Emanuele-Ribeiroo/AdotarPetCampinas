package com.adotarpet.campinas.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adotarpet.campinas.model.RequestStatus
import com.adotarpet.campinas.view.components.BackButton
import com.adotarpet.campinas.view.theme.CreamCard
import com.adotarpet.campinas.view.theme.NeutralTagBg
import com.adotarpet.campinas.view.theme.Terracotta
import com.adotarpet.campinas.view.theme.TerracottaDark
import com.adotarpet.campinas.view.theme.TerracottaLight
import com.adotarpet.campinas.view.theme.TextPrimary
import com.adotarpet.campinas.view.theme.TextSecondary
import com.adotarpet.campinas.viewmodel.CancelState
import com.adotarpet.campinas.viewmodel.RequestDetailViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RequestDetailView(
    entryId: String,
    onBackClick: () -> Unit
) {
    val vm: RequestDetailViewModel = viewModel()
    val entries by vm.entries.collectAsState()
    val entry = entries.find { it.id == entryId }
    val cancelState by vm.cancelState.collectAsState()

    var confirmCancel by remember { mutableStateOf(false) }
    var motivo by remember { mutableStateOf("") }

    LaunchedEffect(cancelState) {
        if (cancelState is CancelState.Concluido) confirmCancel = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        BackButton(onBackClick)

        if (entry == null) {
            Text(
                "Solicitação não encontrada.",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary
            )
            return@Column
        }

        val date = remember(entry.timestamp) {
            SimpleDateFormat("dd/MM/yyyy 'às' HH:mm", Locale("pt", "BR")).format(Date(entry.timestamp))
        }
        val campos = remember(entry.details) { vm.camposExibicao(entry) }

        Text(entry.type.label.uppercase(), style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        Spacer(Modifier.height(4.dp))
        Text(entry.title, style = MaterialTheme.typography.headlineMedium, color = TerracottaDark)
        Spacer(Modifier.height(12.dp))
        StatusBadge(entry.status)

        Spacer(Modifier.height(24.dp))
        SectionCard {
            DetailRow("Tipo", entry.type.label)
            Spacer(Modifier.height(12.dp))
            DetailRow("Enviada em", date)
            Spacer(Modifier.height(12.dp))
            DetailRow("Situação", entry.status.label)
            if (entry.status == RequestStatus.CANCELADA && entry.motivoCancelamento.isNotBlank()) {
                Spacer(Modifier.height(12.dp))
                DetailRow("Motivo do cancelamento", entry.motivoCancelamento)
            }
        }

        if (campos.isNotEmpty()) {
            Spacer(Modifier.height(24.dp))
            Text(
                "RESUMO DA SOLICITAÇÃO",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
            Spacer(Modifier.height(12.dp))
            SectionCard {
                campos.forEachIndexed { i, (rotulo, valor) ->
                    if (i > 0) Spacer(Modifier.height(12.dp))
                    DetailRow(rotulo, valor)
                }
            }
        } else if (entry.subtitle.isNotBlank()) {
            Spacer(Modifier.height(24.dp))
            SectionCard { DetailRow("Detalhes", entry.subtitle) }
        }

        Spacer(Modifier.height(28.dp))

        when {
            entry.cancelavel -> {
                Button(
                    onClick = { confirmCancel = true },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Terracotta,
                        contentColor = Color.White
                    )
                ) {
                    Text("Cancelar solicitação")
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    "Você pode cancelar enquanto a solicitação está em análise.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            entry.status == RequestStatus.CANCELADA -> {
                OutlinedButton(
                    onClick = {},
                    enabled = false,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(50)
                ) {
                    Text("Solicitação cancelada")
                }
            }
        }

        Spacer(Modifier.height(24.dp))
    }

    if (confirmCancel && entry != null) {
        val enviando = cancelState is CancelState.Enviando
        AlertDialog(
            onDismissRequest = { if (!enviando) confirmCancel = false },
            title = { Text("Cancelar solicitação?") },
            text = {
                Column {
                    Text("Conte o motivo do cancelamento. Ele será registrado junto com a solicitação.")
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = motivo,
                        onValueChange = { motivo = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Motivo do cancelamento") },
                        minLines = 3,
                        enabled = !enviando
                    )
                    (cancelState as? CancelState.Erro)?.let {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Cancelada aqui, mas não foi possível enviar para a planilha: ${it.message}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { vm.cancelar(entry, motivo.trim()) },
                    enabled = motivo.isNotBlank() && !enviando
                ) {
                    if (enviando) {
                        CircularProgressIndicator(modifier = Modifier.height(18.dp).padding(2.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Confirmar cancelamento", color = Terracotta)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmCancel = false }, enabled = !enviando) { Text("Voltar") }
            }
        )
    }
}

@Composable
private fun SectionCard(content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CreamCard),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), content = content)
    }
}

@Composable
private fun StatusBadge(status: RequestStatus) {
    val bg = when (status) {
        RequestStatus.EM_ANALISE -> TerracottaLight
        RequestStatus.CANCELADA -> NeutralTagBg
    }
    val fg = when (status) {
        RequestStatus.EM_ANALISE -> TerracottaDark
        RequestStatus.CANCELADA -> TextSecondary
    }
    Box(
        modifier = Modifier
            .background(bg, RoundedCornerShape(50))
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Text(status.label, style = MaterialTheme.typography.bodyMedium, color = fg, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            modifier = Modifier.weight(1f)
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary,
            modifier = Modifier
                .weight(1.4f)
                .padding(start = 16.dp)
        )
    }
}
