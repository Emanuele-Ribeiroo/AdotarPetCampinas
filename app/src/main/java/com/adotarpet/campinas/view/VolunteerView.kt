package com.adotarpet.campinas.view

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adotarpet.campinas.viewmodel.VolunteerViewModel
import com.adotarpet.campinas.view.components.BackButton
import com.adotarpet.campinas.view.components.rememberDiscardGuard
import com.adotarpet.campinas.view.components.FilterChip
import com.adotarpet.campinas.view.components.FormSectionLabel
import com.adotarpet.campinas.view.components.FormTextField
import com.adotarpet.campinas.view.components.SuccessMessage
import com.adotarpet.campinas.view.theme.Terracotta
import com.adotarpet.campinas.view.theme.TerracottaDark
import com.adotarpet.campinas.view.theme.TextSecondary
import com.adotarpet.campinas.view.theme.White
import com.adotarpet.campinas.util.Validators

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VolunteerView(onVoltarClick: () -> Unit, onBackClick: () -> Unit) {
    // Estado e regras ficam no ViewModel; a tela só desenha e dispara eventos.
    val vm: VolunteerViewModel = viewModel()

    val tentarVoltar = rememberDiscardGuard(hasChanges = vm.temAlteracoes, onDiscard = onBackClick)

    if (vm.enviado) {
        SuccessMessage(
            title = "Obrigado por\nse voluntariar",
            message = "Recebemos seu cadastro, ${vm.nome}. Nossa equipe vai entrar em contato pelo WhatsApp para combinar os próximos passos.",
            buttonLabel = "Voltar ao início",
            onButtonClick = onVoltarClick
        )
        return
    }

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
        FormTextField("Nome completo", "Como você se chama?", vm.nome) { vm.nome = it }
        FormTextField(
            "Telefone / WhatsApp", "(19) 90000-0000", vm.telefone,
            keyboardType = KeyboardType.Phone,
            errorText = if (vm.telefone.isNotEmpty() && !vm.telefoneOk) "Telefone inválido" else null,
            visualTransformation = Validators.phoneTransformation
        ) { vm.onTelefoneChange(it) }
        FormTextField(
            "E-mail", "voce@email.com", vm.email,
            keyboardType = KeyboardType.Email,
            errorText = if (vm.email.isNotEmpty() && !vm.emailOk) "E-mail inválido" else null
        ) { vm.onEmailChange(it) }

        Spacer(Modifier.height(8.dp))
        FormSectionLabel("COMO QUER AJUDAR? (ESCOLHA UMA OU MAIS)")
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(bottom = 20.dp)
        ) {
            vm.areas.forEach { area ->
                FilterChip(area, area in vm.areasSelecionadas, { vm.alternarArea(area) })
            }
        }

        FormSectionLabel("DISPONIBILIDADE")
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(bottom = 20.dp)
        ) {
            vm.disponibilidades.forEach { op ->
                FilterChip(op, vm.disponibilidade == op, { vm.disponibilidade = op })
            }
        }

        FormTextField(
            "Quer contar algo mais?", "Experiência, horários, dúvidas…", vm.mensagem,
            singleLine = false
        ) { vm.mensagem = it }

        Spacer(Modifier.height(12.dp))
        Button(
            onClick = vm::enviarCadastro,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = Terracotta, contentColor = White),
            enabled = vm.podeEnviar
        ) {
            Text("Quero ser voluntário")
        }
        Spacer(Modifier.height(24.dp))
    }
}
