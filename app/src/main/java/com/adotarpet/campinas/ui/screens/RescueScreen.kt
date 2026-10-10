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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.adotarpet.campinas.data.HistoryRepository
import com.adotarpet.campinas.data.UserRepository
import com.adotarpet.campinas.model.HistoryType
import com.adotarpet.campinas.ui.components.Attachment
import com.adotarpet.campinas.ui.components.AttachmentPicker
import com.adotarpet.campinas.ui.components.FilterChip
import com.adotarpet.campinas.util.OngInfo
import com.adotarpet.campinas.util.dial
import com.adotarpet.campinas.ui.components.FormTextField
import com.adotarpet.campinas.ui.components.SuccessMessage
import com.adotarpet.campinas.util.Validators
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
    val perfil = remember { UserRepository.profile.value }
    var telefone by remember { mutableStateOf(perfil.phone) }
    var anexos by remember { mutableStateOf<List<Attachment>>(emptyList()) }
    var enviado by remember { mutableStateOf(false) }

    if (enviado) {
        SuccessMessage(
            title = "Aviso\nenviado",
            message = "Recebemos o seu aviso de resgate. A equipe vai entrar em contato pelo telefone informado o mais rápido possível. Se o animal corre risco de morte, ligue agora para (19) 3232-0000.",
            buttonLabel = "Fazer outro aviso",
            onButtonClick = {
                animalSelecionado = animalOptions.first()
                situacaoSelecionada = situacaoOptions.first()
                endereco = ""
                cep = ""
                telefone = UserRepository.profile.value.phone
                anexos = emptyList()
                enviado = false
            }
        )
        return
    }

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
        val context = LocalContext.current
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SalmonBanner),
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClickLabel = "Ligar para a ONG") { context.dial(OngInfo.PHONE_DIAL) }
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.Phone, contentDescription = null, tint = TerracottaDark)
                Spacer(Modifier.width(10.dp))
                Text(
                    "Risco de morte? Toque para ligar agora: ${OngInfo.PHONE_DISPLAY}",
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
        FormTextField("Onde está o animal?", "Rua, número, bairro", endereco) { endereco = it }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            FormTextField(
                "CEP", "13000-000", cep,
                modifier = Modifier.weight(1f),
                keyboardType = KeyboardType.Number,
                errorText = if (cep.isNotEmpty() && !Validators.isValidCep(cep)) "CEP inválido" else null,
                visualTransformation = Validators.cepTransformation
            ) { cep = Validators.onlyCep(it) }
            FormTextField(
                "Seu telefone", "(19) 90000-0000", telefone,
                modifier = Modifier.weight(1f),
                keyboardType = KeyboardType.Phone,
                errorText = if (telefone.isNotEmpty() && !Validators.isValidPhone(telefone)) "Telefone inválido" else null,
                visualTransformation = Validators.phoneTransformation
        ) { telefone = Validators.onlyPhone(it) }
        }

        Spacer(Modifier.height(8.dp))
        AttachmentPicker(
            label = "Fotos e vídeo do animal",
            helperText = "Ajuda muito a equipe de resgate",
            attachments = anexos,
            onAttachmentsChange = { anexos = it }
        )

        Spacer(Modifier.height(32.dp))
        Button(
            onClick = {
                UserRepository.fillBlanks(phone = telefone)
                HistoryRepository.add(
                    HistoryType.RESGATE,
                    "$animalSelecionado · $situacaoSelecionada",
                    endereco.trim()
                )
                onEnviarResgate()
                enviado = true
            },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = Terracotta, contentColor = White),
            enabled = endereco.isNotBlank() && Validators.isValidPhone(telefone) &&
                (cep.isEmpty() || Validators.isValidCep(cep))
        ) {
            Text("Enviar resgate")
        }
        Spacer(Modifier.height(24.dp))
    }
}

