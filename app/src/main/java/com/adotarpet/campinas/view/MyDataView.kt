package com.adotarpet.campinas.view

import androidx.compose.foundation.layout.Column
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adotarpet.campinas.viewmodel.MyDataViewModel
import com.adotarpet.campinas.model.UserProfile
import com.adotarpet.campinas.view.components.BackButton
import com.adotarpet.campinas.view.components.FormSectionLabel
import com.adotarpet.campinas.view.components.FormTextField
import com.adotarpet.campinas.view.components.rememberDiscardGuard
import com.adotarpet.campinas.view.theme.Terracotta
import com.adotarpet.campinas.view.theme.TerracottaDark
import com.adotarpet.campinas.view.theme.TextSecondary
import com.adotarpet.campinas.view.theme.White
import com.adotarpet.campinas.util.Validators

@Composable
fun MyDataView(onSaved: () -> Unit, onBackClick: () -> Unit) {
    val vm: MyDataViewModel = viewModel()
    val saved = remember { vm.perfil }
    var nome by remember { mutableStateOf(saved.name) }
    var cpf by remember { mutableStateOf(saved.cpf) }
    var telefone by remember { mutableStateOf(saved.phone) }
    var email by remember { mutableStateOf(saved.email) }

    val cpfOk = cpf.isEmpty() || Validators.isValidCpf(cpf)
    val telefoneOk = telefone.isEmpty() || Validators.isValidPhone(telefone)
    val emailOk = email.isEmpty() || Validators.isValidEmail(email)
    val changed = nome != saved.name || cpf != saved.cpf || telefone != saved.phone || email != saved.email

    val tentarVoltar = rememberDiscardGuard(hasChanges = changed, onDiscard = onBackClick)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        BackButton(tentarVoltar)
        Text("PERFIL", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        Spacer(Modifier.height(4.dp))
        Text("Meus dados", style = MaterialTheme.typography.headlineMedium, color = TerracottaDark)
        Spacer(Modifier.height(8.dp))
        Text(
            "Ficam guardados só neste celular e preenchem os formulários de adoção, voluntariado e resgate para você.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        Spacer(Modifier.height(24.dp))
        FormSectionLabel("DADOS PESSOAIS")
        FormTextField("Nome completo", "Como você se chama?", nome) { nome = it }
        FormTextField(
            "CPF", "000.000.000-00", cpf,
            keyboardType = KeyboardType.Number,
            errorText = if (!cpfOk) "CPF inválido" else null,
            visualTransformation = Validators.cpfTransformation
        ) { cpf = Validators.onlyCpf(it) }

        Spacer(Modifier.height(8.dp))
        FormSectionLabel("CONTATO")
        FormTextField(
            "Telefone / WhatsApp", "(19) 90000-0000", telefone,
            keyboardType = KeyboardType.Phone,
            errorText = if (!telefoneOk) "Telefone inválido" else null,
            visualTransformation = Validators.phoneTransformation
        ) { telefone = Validators.onlyPhone(it) }
        FormTextField(
            "E-mail", "voce@email.com", email,
            keyboardType = KeyboardType.Email,
            errorText = if (!emailOk) "E-mail inválido" else null
        ) { email = it.trim() }

        Spacer(Modifier.height(20.dp))
        Button(
            onClick = {
                vm.salvar(UserProfile(nome.trim(), cpf, telefone, email))
                onSaved()
            },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = Terracotta, contentColor = White),
            enabled = changed && cpfOk && telefoneOk && emailOk
        ) {
            Text("Salvar")
        }
        Spacer(Modifier.height(24.dp))
    }
}
