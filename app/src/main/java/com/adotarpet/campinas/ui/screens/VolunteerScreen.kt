package com.adotarpet.campinas.ui.screens

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.adotarpet.campinas.data.HistoryRepository
import com.adotarpet.campinas.data.UserRepository
import com.adotarpet.campinas.model.HistoryType
import com.adotarpet.campinas.ui.components.BackButton
import com.adotarpet.campinas.ui.components.rememberDiscardGuard
import com.adotarpet.campinas.ui.components.FilterChip
import com.adotarpet.campinas.ui.components.FormSectionLabel
import com.adotarpet.campinas.ui.components.FormTextField
import com.adotarpet.campinas.ui.components.SuccessMessage
import com.adotarpet.campinas.ui.theme.Terracotta
import com.adotarpet.campinas.ui.theme.TerracottaDark
import com.adotarpet.campinas.ui.theme.TextSecondary
import com.adotarpet.campinas.ui.theme.White
import com.adotarpet.campinas.util.Validators

private val areas = listOf("Lar temporário", "Passeios", "Eventos de adoção", "Transporte", "Divulgação", "Veterinária")
private val disponibilidades = listOf("Dias úteis", "Fins de semana", "Qualquer dia")

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VolunteerScreen(onVoltarClick: () -> Unit, onBackClick: () -> Unit) {
    val perfil = remember { UserRepository.profile.value }
    var nome by remember { mutableStateOf(perfil.name) }
    var telefone by remember { mutableStateOf(perfil.phone) }
    var email by remember { mutableStateOf(perfil.email) }
    var areasSelecionadas by remember { mutableStateOf(setOf<String>()) }
    var disponibilidade by remember { mutableStateOf(disponibilidades.first()) }
    var mensagem by remember { mutableStateOf("") }
    var enviado by remember { mutableStateOf(false) }

    val tentarVoltar = rememberDiscardGuard(
        hasChanges = !enviado && (nome != perfil.name || telefone != perfil.phone || email != perfil.email ||
            areasSelecionadas.isNotEmpty() || mensagem.isNotBlank()),
        onDiscard = onBackClick
    )

    if (enviado) {
        SuccessMessage(
            title = "Obrigado por\nse voluntariar",
            message = "Recebemos seu cadastro, $nome. Nossa equipe vai entrar em contato pelo WhatsApp para combinar os próximos passos.",
            buttonLabel = "Voltar ao início",
            onButtonClick = onVoltarClick
        )
        return
    }

    val telefoneOk = Validators.isValidPhone(telefone)
    val emailOk = Validators.isValidEmail(email)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        BackButton(tentarVoltar)
        Text("VOLUNTARIADO", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        Spacer(Modifier.height(4.dp))
        Text("Quero ajudar", style = MaterialTheme.typography.headlineMedium, color = TerracottaDark)

        Spacer(Modifier.height(24.dp))
        FormSectionLabel("SEUS DADOS")
        FormTextField("Nome completo", "Como você se chama?", nome) { nome = it }
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

        Spacer(Modifier.height(8.dp))
        FormSectionLabel("COMO QUER AJUDAR? (ESCOLHA UMA OU MAIS)")
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(bottom = 20.dp)
        ) {
            areas.forEach { area ->
                FilterChip(area, area in areasSelecionadas, {
                    areasSelecionadas = if (area in areasSelecionadas) areasSelecionadas - area else areasSelecionadas + area
                })
            }
        }

        FormSectionLabel("DISPONIBILIDADE")
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(bottom = 20.dp)
        ) {
            disponibilidades.forEach { op ->
                FilterChip(op, disponibilidade == op, { disponibilidade = op })
            }
        }

        FormTextField(
            "Quer contar algo mais?", "Experiência, horários, dúvidas…", mensagem,
            singleLine = false
        ) { mensagem = it }

        Spacer(Modifier.height(12.dp))
        Button(
            onClick = {
                UserRepository.fillBlanks(name = nome.trim(), phone = telefone, email = email)
                HistoryRepository.add(HistoryType.VOLUNTARIO, "Cadastro de voluntário", areasSelecionadas.sorted().joinToString(", "))
                enviado = true /* TODO: enviar para backend/API */
            },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = Terracotta, contentColor = White),
            enabled = nome.isNotBlank() && telefoneOk && emailOk && areasSelecionadas.isNotEmpty()
        ) {
            Text("Quero ser voluntário")
        }
        Spacer(Modifier.height(24.dp))
    }
}
