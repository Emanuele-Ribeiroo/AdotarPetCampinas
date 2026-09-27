package com.adotarpet.campinas.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.adotarpet.campinas.ui.components.FilterChip
import com.adotarpet.campinas.ui.theme.CreamCard
import com.adotarpet.campinas.ui.theme.SalmonBanner
import com.adotarpet.campinas.ui.theme.Terracotta
import com.adotarpet.campinas.ui.theme.TerracottaDark
import com.adotarpet.campinas.ui.theme.TextPrimary
import com.adotarpet.campinas.ui.theme.TextSecondary
import com.adotarpet.campinas.ui.theme.White

private val animalOptions = listOf("Cão", "Gato", "Outro")
private val situacaoOptions = listOf("Ferido", "Abandonado", "Maus-tratos", "Preso")

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RescueScreen(
    onEnviarResgate: () -> Unit
) {
    var animalSelecionado by remember { mutableStateOf(animalOptions.first()) }
    var situacaoSelecionada by remember { mutableStateOf(situacaoOptions.first()) }
    var endereco by remember { mutableStateOf("") }
    var cep by remember { mutableStateOf("") }
    var telefone by remember { mutableStateOf("") }
    var fotoAnexada by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text("RESGATE", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        Spacer(Modifier.height(4.dp))
        Text(
            "Encontrei um\nanimal em risco",
            style = MaterialTheme.typography.headlineMedium,
            color = TerracottaDark
        )

        Spacer(Modifier.height(20.dp))
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SalmonBanner)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.Phone, contentDescription = null, tint = TerracottaDark)
                Spacer(Modifier.width(10.dp))
                Text(
                    "Risco de morte? Ligue agora: (19) 3232-0000",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(Modifier.height(24.dp))
        Text("Qual animal?", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            animalOptions.forEach { option ->
                FilterChip(option, selected = animalSelecionado == option, onClick = { animalSelecionado = option })
            }
        }

        Spacer(Modifier.height(20.dp))
        Text("Situação", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 10.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            situacaoOptions.forEach { option ->
                FilterChip(option, selected = situacaoSelecionada == option, onClick = { situacaoSelecionada = option })
            }
        }

        Spacer(Modifier.height(20.dp))
        RescueField("Onde está o animal?", "Rua, número, bairro", endereco) { endereco = it }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            RescueField("CEP", "13000-000", cep, modifier = Modifier.weight(1f)) { cep = it }
            RescueField("Seu telefone", "(19) 90000-0000", telefone, modifier = Modifier.weight(1f)) { telefone = it }
        }

        Spacer(Modifier.height(8.dp))
        Text("Foto do animal", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 10.dp))
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CreamCard),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { fotoAnexada = !fotoAnexada }
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.AddAPhoto, contentDescription = null, tint = TerracottaDark)
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        if (fotoAnexada) "Foto anexada ✓" else "Toque para anexar uma foto",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        "ajuda muito a equipe de resgate",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }

        Spacer(Modifier.height(32.dp))
        Button(
            onClick = onEnviarResgate,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = Terracotta, contentColor = White),
            enabled = endereco.isNotBlank() && telefone.isNotBlank()
        ) {
            Text("Enviar resgate")
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun RescueField(
    label: String,
    placeholder: String,
    value: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit
) {
    Column(modifier = modifier.padding(bottom = 16.dp)) {
        Text(label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = TextSecondary) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = CreamCard,
                focusedContainerColor = CreamCard,
                unfocusedBorderColor = CreamCard,
                focusedBorderColor = Terracotta
            ),
            singleLine = true
        )
    }
}
