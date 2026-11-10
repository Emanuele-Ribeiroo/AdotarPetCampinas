package com.adotarpet.campinas.view

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adotarpet.campinas.viewmodel.RescueViewModel
import com.adotarpet.campinas.view.components.AttachmentPicker
import com.adotarpet.campinas.view.components.FilterChip
import com.adotarpet.campinas.util.OngInfo
import com.adotarpet.campinas.util.dial
import com.adotarpet.campinas.view.components.FormTextField
import com.adotarpet.campinas.view.components.SuccessMessage
import com.adotarpet.campinas.util.Validators
import com.adotarpet.campinas.view.theme.CreamCard
import com.adotarpet.campinas.view.theme.SalmonBanner
import com.adotarpet.campinas.view.theme.Terracotta
import com.adotarpet.campinas.view.theme.TerracottaDark
import com.adotarpet.campinas.view.theme.TextPrimary
import com.adotarpet.campinas.view.theme.TextSecondary
import com.adotarpet.campinas.view.theme.White

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RescueView(
    onEnviarResgate: () -> Unit
) {
    // Estado e regras ficam no ViewModel; a tela só desenha e dispara eventos.
    val vm: RescueViewModel = viewModel()

    if (vm.enviado) {
        SuccessMessage(
            title = "Aviso\nenviado",
            message = "Recebemos o seu aviso de resgate. A equipe vai entrar em contato pelo telefone informado o mais rápido possível. Se o animal corre risco de morte, ligue agora para (19) 3232-0000.",
            buttonLabel = "Fazer outro aviso",
            onButtonClick = vm::novoAviso
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
            vm.animalOptions.forEach { option ->
                FilterChip(option, selected = vm.animalSelecionado == option, onClick = { vm.animalSelecionado = option })
            }
        }

        Spacer(Modifier.height(20.dp))
        Text("Situação", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 10.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            vm.situacaoOptions.forEach { option ->
                FilterChip(option, selected = vm.situacaoSelecionada == option, onClick = { vm.situacaoSelecionada = option })
            }
        }

        Spacer(Modifier.height(20.dp))
        FormTextField("Onde está o animal?", "Rua, número, bairro", vm.endereco) { vm.endereco = it }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            FormTextField(
                "CEP", "13000-000", vm.cep,
                modifier = Modifier.weight(1f),
                keyboardType = KeyboardType.Number,
                errorText = if (!vm.cepBuscando && vm.cepInvalido) "CEP inválido" else null,
                visualTransformation = Validators.cepTransformation
            ) { vm.onCepChange(it) }
            FormTextField(
                "Seu telefone", "(19) 90000-0000", vm.telefone,
                modifier = Modifier.weight(1f),
                keyboardType = KeyboardType.Phone,
                errorText = if (vm.telefone.isNotEmpty() && !vm.telefoneOk) "Telefone inválido" else null,
                visualTransformation = Validators.phoneTransformation
            ) { vm.onTelefoneChange(it) }
        }

        Spacer(Modifier.height(8.dp))
        AttachmentPicker(
            label = "Fotos e vídeo do animal",
            helperText = "Ajuda muito a equipe de resgate",
            attachments = vm.anexos,
            onAttachmentsChange = { vm.anexos = it }
        )

        Spacer(Modifier.height(32.dp))
        Button(
            onClick = {
                vm.enviarResgate()
                onEnviarResgate()
            },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = Terracotta, contentColor = White),
            enabled = vm.podeEnviar
        ) {
            Text("Enviar resgate")
        }
        Spacer(Modifier.height(24.dp))
    }
}
