package com.adotarpet.campinas.ui.screens

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.adotarpet.campinas.data.PetRepository
import com.adotarpet.campinas.ui.components.Attachment
import com.adotarpet.campinas.ui.components.AttachmentPicker
import com.adotarpet.campinas.ui.components.FormSectionLabel
import com.adotarpet.campinas.ui.components.FormTextField
import com.adotarpet.campinas.ui.theme.CreamCard
import com.adotarpet.campinas.ui.theme.Terracotta
import com.adotarpet.campinas.ui.theme.TerracottaDark
import com.adotarpet.campinas.ui.theme.TerracottaLight
import com.adotarpet.campinas.ui.theme.TextSecondary
import com.adotarpet.campinas.ui.theme.White

@Composable
fun AdoptionFormScreen(
    petId: String,
    onEnviarClick: (String) -> Unit
) {
    val pet = PetRepository.petById(petId) ?: return

    var nome by remember { mutableStateOf("") }
    var cpf by remember { mutableStateOf("") }
    var telefone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var cep by remember { mutableStateOf("") }
    var cidade by remember { mutableStateOf(pet.city) }
    var endereco by remember { mutableStateOf("") }
    var anexosCasa by remember { mutableStateOf<List<Attachment>>(emptyList()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text("Adoção · ${pet.name}", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        Spacer(Modifier.height(4.dp))
        Text("Formulário de adoção", style = MaterialTheme.typography.headlineMedium, color = TerracottaDark)

        Spacer(Modifier.height(24.dp))
        FormSectionLabel("SEUS DADOS")
        FormTextField("Nome completo", "Como você se chama?", nome) { nome = it }
        FormTextField("CPF", "000.000.000-00", cpf) { cpf = it }

        Spacer(Modifier.height(16.dp))
        FormSectionLabel("CONTATO")
        FormTextField("Telefone / WhatsApp", "(19) 90000-0000", telefone) { telefone = it }
        FormTextField("E-mail", "voce@email.com", email) { email = it }

        Spacer(Modifier.height(16.dp))
        FormSectionLabel("ONDE O ANIMAL VAI MORAR")
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            FormTextField("CEP", "13000-000", cep, modifier = Modifier.weight(1f)) { cep = it }
            FormTextField("Cidade", "Campinas", cidade, modifier = Modifier.weight(1f)) { cidade = it }
        }
        FormTextField("Endereço completo", "Rua, número, bairro", endereco) { endereco = it }

        Spacer(Modifier.height(8.dp))
        AttachmentPicker(
            label = "Fotos e vídeo da casa",
            helperText = "Ajuda a equipe a avaliar o espaço onde o animal vai morar",
            attachments = anexosCasa,
            onAttachmentsChange = { anexosCasa = it }
        )

        Spacer(Modifier.height(20.dp))
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CreamCard)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(TerracottaLight, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Pets, contentDescription = null, tint = TerracottaDark)
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        "Solicitação para ${pet.name}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        "${pet.speciesLabel} · ${pet.age}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }

        Spacer(Modifier.height(28.dp))
        Button(
            onClick = { onEnviarClick(pet.id) },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = Terracotta, contentColor = White),
            enabled = nome.isNotBlank() && telefone.isNotBlank()
        ) {
            Text("Enviar solicitação")
        }
        Spacer(Modifier.height(24.dp))
    }
}
