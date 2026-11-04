package com.adotarpet.campinas.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.adotarpet.campinas.util.Validators
import com.adotarpet.campinas.data.HistoryRepository
import com.adotarpet.campinas.data.PetRepository
import com.adotarpet.campinas.data.UserRepository
import com.adotarpet.campinas.model.HistoryType
import com.adotarpet.campinas.ui.components.BackButton
import com.adotarpet.campinas.ui.components.rememberDiscardGuard
import com.adotarpet.campinas.ui.components.Attachment
import com.adotarpet.campinas.ui.components.AttachmentPicker
import com.adotarpet.campinas.ui.components.FilterChip
import com.adotarpet.campinas.ui.components.FormSectionLabel
import com.adotarpet.campinas.ui.components.FormTextField
import com.adotarpet.campinas.ui.theme.CreamCard
import com.adotarpet.campinas.ui.theme.Terracotta
import com.adotarpet.campinas.ui.theme.TerracottaDark
import com.adotarpet.campinas.ui.theme.TerracottaLight
import com.adotarpet.campinas.ui.theme.TextSecondary
import com.adotarpet.campinas.ui.theme.White

private val moradiaOptions = listOf("Casa", "Apartamento", "Sítio/Chácara", "Outro")
private val experienciaOptions = listOf("Primeira vez", "Já tive pets", "Tenho pets")

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AdoptionFormScreen(
    petId: String,
    onEnviarClick: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val pet = PetRepository.petById(petId) ?: return

    val perfil = remember { UserRepository.profile.value }
    var nome by remember { mutableStateOf(perfil.name) }
    var cpf by remember { mutableStateOf(perfil.cpf) }
    var telefone by remember { mutableStateOf(perfil.phone) }
    var email by remember { mutableStateOf(perfil.email) }
    var cep by remember { mutableStateOf("") }
    var cidade by remember { mutableStateOf(pet.city) }
    var endereco by remember { mutableStateOf("") }
    var anexosCasa by remember { mutableStateOf<List<Attachment>>(emptyList()) }
    // RF06: perfil do lar e motivação do adotante.
    var moradia by remember { mutableStateOf<String?>(null) }
    var temOutrosAnimais by remember { mutableStateOf<Boolean?>(null) }
    var quaisAnimais by remember { mutableStateOf("") }
    var experiencia by remember { mutableStateOf<String?>(null) }
    var rotina by remember { mutableStateOf("") }
    var motivo by remember { mutableStateOf("") }

    val tentarVoltar = rememberDiscardGuard(
        hasChanges = nome != perfil.name || cpf != perfil.cpf || telefone != perfil.phone || email != perfil.email ||
            cep.isNotBlank() || endereco.isNotBlank() || anexosCasa.isNotEmpty() ||
            moradia != null || temOutrosAnimais != null || quaisAnimais.isNotBlank() ||
            experiencia != null || rotina.isNotBlank() || motivo.isNotBlank(),
        onDiscard = onBackClick
    )

    val cpfOk = Validators.isValidCpf(cpf)
    val telefoneOk = Validators.isValidPhone(telefone)
    val emailOk = Validators.isValidEmail(email)
    val cepOk = Validators.isValidCep(cep)
    // "Quais?" só é exigido quando o adotante marca que tem outros animais.
    val outrosAnimaisOk = temOutrosAnimais == false || (temOutrosAnimais == true && quaisAnimais.isNotBlank())
    val perfilLarOk = moradia != null && temOutrosAnimais != null && outrosAnimaisOk &&
        experiencia != null && rotina.isNotBlank() && motivo.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        BackButton(tentarVoltar)
        Text("Adoção · ${pet.name}", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        Spacer(Modifier.height(4.dp))
        Text("Formulário de adoção", style = MaterialTheme.typography.headlineMedium, color = TerracottaDark)

        Spacer(Modifier.height(24.dp))
        FormSectionLabel("SEUS DADOS")
        FormTextField("Nome completo", "Como você se chama?", nome) { nome = it }
        FormTextField(
            "CPF", "000.000.000-00", cpf,
            keyboardType = KeyboardType.Number,
            errorText = if (cpf.isNotEmpty() && !cpfOk) "CPF inválido" else null,
            visualTransformation = Validators.cpfTransformation
        ) { cpf = Validators.onlyCpf(it) }

        Spacer(Modifier.height(16.dp))
        FormSectionLabel("CONTATO")
        FormTextField(
            "Telefone / WhatsApp", "(19) 90000-0000", telefone,
            keyboardType = KeyboardType.Phone,
            errorText = if (telefone.isNotEmpty() && !telefoneOk) "Telefone inválido" else null,
            visualTransformation = Validators.phoneTransformation
        ) { telefone = Validators.onlyPhone(it) }
        FormTextField(
            "E-mail", "voce@email.com", email,
            keyboardType = KeyboardType.Email,
            errorText = if (email.isNotEmpty() && !emailOk) "E-mail inválido" else null
        ) { email = it.trim() }

        Spacer(Modifier.height(16.dp))
        FormSectionLabel("ONDE O ANIMAL VAI MORAR")
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            FormTextField(
                "CEP", "13000-000", cep,
                modifier = Modifier.weight(1f),
                keyboardType = KeyboardType.Number,
                errorText = if (cep.isNotEmpty() && !cepOk) "CEP inválido" else null,
                visualTransformation = Validators.cepTransformation
            ) { cep = Validators.onlyCep(it) }
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

        Spacer(Modifier.height(16.dp))
        FormSectionLabel("SOBRE A MORADIA")
        Text("Tipo de moradia", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 10.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            moradiaOptions.forEach { option ->
                FilterChip(option, selected = moradia == option, onClick = { moradia = option })
            }
        }

        Spacer(Modifier.height(16.dp))
        Text("Já tem outros animais?", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            FilterChip("Sim", selected = temOutrosAnimais == true, onClick = { temOutrosAnimais = true })
            FilterChip("Não", selected = temOutrosAnimais == false, onClick = {
                temOutrosAnimais = false
                quaisAnimais = ""
            })
        }
        if (temOutrosAnimais == true) {
            Spacer(Modifier.height(12.dp))
            FormTextField("Quais animais?", "Ex.: 1 cachorro e 2 gatos", quaisAnimais) { quaisAnimais = it }
        }

        Spacer(Modifier.height(16.dp))
        Text("Experiência com pets", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 10.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            experienciaOptions.forEach { option ->
                FilterChip(option, selected = experiencia == option, onClick = { experiencia = option })
            }
        }

        Spacer(Modifier.height(20.dp))
        FormSectionLabel("ROTINA E MOTIVAÇÃO")
        FormTextField(
            "Rotina", "Quanto tempo o animal ficará sozinho? Quem vai cuidar dele?", rotina,
            singleLine = false
        ) { rotina = it }
        FormTextField(
            "Motivo da adoção", "Por que você quer adotar este animal?", motivo,
            singleLine = false
        ) { motivo = it }

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
            onClick = {
                UserRepository.fillBlanks(nome.trim(), cpf, telefone, email)
                HistoryRepository.add(HistoryType.ADOCAO, "Adoção de ${pet.name}", "${pet.speciesLabel} · ${pet.age}")
                onEnviarClick(pet.id)
            },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = Terracotta, contentColor = White),
            enabled = nome.isNotBlank() && cpfOk && telefoneOk && emailOk && cepOk && endereco.isNotBlank() &&
                perfilLarOk
        ) {
            Text("Enviar solicitação")
        }
        Spacer(Modifier.height(24.dp))
    }
}
