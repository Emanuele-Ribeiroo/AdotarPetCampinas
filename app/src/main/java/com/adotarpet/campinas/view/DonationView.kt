package com.adotarpet.campinas.view

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adotarpet.campinas.viewmodel.DonationViewModel
import com.adotarpet.campinas.view.components.BackButton
import com.adotarpet.campinas.view.components.FilterChip
import com.adotarpet.campinas.view.components.FormSectionLabel
import com.adotarpet.campinas.view.components.SuccessMessage
import com.adotarpet.campinas.view.theme.CreamCard
import com.adotarpet.campinas.view.theme.Terracotta
import com.adotarpet.campinas.view.theme.TerracottaDark
import com.adotarpet.campinas.view.theme.TextPrimary
import com.adotarpet.campinas.view.theme.TextSecondary
import com.adotarpet.campinas.view.theme.White

private const val PIX_KEY = "doacoes@adotarpetcampinas.org"
private val valores = listOf("R$ 10", "R$ 25", "R$ 50", "R$ 100", "Outro valor")

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DonationView(onVoltarClick: () -> Unit, onBackClick: () -> Unit) {
    val clipboard = LocalClipboardManager.current
    val context = LocalContext.current
    val vm: DonationViewModel = viewModel()
    var valor by remember { mutableStateOf(valores[1]) }
    var concluido by remember { mutableStateOf(false) }

    if (concluido) {
        SuccessMessage(
            title = "Obrigado pela\ndoação",
            message = "Sua ajuda vira ração, vacinas e cuidados para os animais que estão esperando um lar.",
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
        BackButton(onBackClick)
        Text("DOAÇÃO", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        Spacer(Modifier.height(4.dp))
        Text("Ajude quem\nprecisa de um lar", style = MaterialTheme.typography.headlineMedium, color = TerracottaDark)
        Spacer(Modifier.height(12.dp))
        Text(
            "Toda doação é usada em ração, vacinas, castração e cuidados veterinários dos animais resgatados.",
            style = MaterialTheme.typography.bodyLarge,
            color = TextPrimary
        )

        Spacer(Modifier.height(24.dp))
        FormSectionLabel("QUANTO QUER DOAR?")
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            valores.forEach { v -> FilterChip(v, valor == v, { valor = v }) }
        }

        Spacer(Modifier.height(24.dp))
        FormSectionLabel("DOE POR PIX")
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CreamCard),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Chave PIX (e-mail)", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                Spacer(Modifier.height(4.dp))
                Text(PIX_KEY, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(12.dp))
                OutlinedButton(
                    onClick = {
                        clipboard.setText(AnnotatedString(PIX_KEY))
                        Toast.makeText(context, "Chave PIX copiada", Toast.LENGTH_SHORT).show()
                    },
                    shape = RoundedCornerShape(50)
                ) {
                    Text("Copiar chave")
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            "Abra o app do seu banco, escolha pagar com PIX e cole a chave. Informe o valor escolhido ($valor).",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )

        Spacer(Modifier.height(28.dp))
        Button(
            onClick = {
                vm.registrarDoacao(valor)
                concluido = true
            },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = Terracotta, contentColor = White)
        ) {
            Text("Já fiz minha doação")
        }
        Spacer(Modifier.height(24.dp))
    }
}
