package com.adotarpet.campinas.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.adotarpet.campinas.data.PetRepository
import com.adotarpet.campinas.ui.theme.SuccessGreen
import com.adotarpet.campinas.ui.theme.SuccessGreenIcon
import com.adotarpet.campinas.ui.theme.Terracotta
import com.adotarpet.campinas.ui.theme.TerracottaDark
import com.adotarpet.campinas.ui.theme.TextPrimary
import com.adotarpet.campinas.ui.theme.White

@Composable
fun ConfirmationScreen(
    petId: String,
    onVerOutrosClick: () -> Unit,
    onVoltarInicioClick: () -> Unit
) {
    val pet = PetRepository.petById(petId)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.weight(1f))

        Box(
            modifier = Modifier
                .size(96.dp)
                .background(SuccessGreen, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Check, contentDescription = null, tint = SuccessGreenIcon, modifier = Modifier.size(40.dp))
        }

        Spacer(Modifier.height(24.dp))
        Text(
            "Solicitação\nenviada",
            style = MaterialTheme.typography.headlineLarge,
            color = TerracottaDark,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))

        val petName = pet?.name ?: "esse animal"
        Text(
            buildString {
                append("Recebemos seu pedido de adoção para $petName. ")
                append("Entramos em contato pelo WhatsApp em até 2 dias úteis para combinar a entrevista.")
            },
            style = MaterialTheme.typography.bodyLarge,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.weight(1f))

        Button(
            onClick = onVerOutrosClick,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = Terracotta, contentColor = White)
        ) {
            Text("Ver outros animais")
        }
        Spacer(Modifier.height(8.dp))
        TextButton(onClick = onVoltarInicioClick) {
            Text("Voltar ao início", color = Terracotta, fontWeight = FontWeight.SemiBold)
        }
    }
}
