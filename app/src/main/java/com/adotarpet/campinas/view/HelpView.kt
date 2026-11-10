package com.adotarpet.campinas.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.adotarpet.campinas.view.components.BackButton
import com.adotarpet.campinas.view.theme.CreamCard
import com.adotarpet.campinas.view.theme.Terracotta
import com.adotarpet.campinas.view.theme.TerracottaDark
import com.adotarpet.campinas.view.theme.TextSecondary
import com.adotarpet.campinas.view.theme.White
import com.adotarpet.campinas.util.OngInfo
import com.adotarpet.campinas.util.dial
import com.adotarpet.campinas.util.openMap
import com.adotarpet.campinas.util.openWhatsApp
import com.adotarpet.campinas.util.sendEmail

private data class Partner(val name: String, val kind: String, val address: String, val phone: String)

private val partners = listOf(
    Partner("Clínica Vet Amigo", "Clínica veterinária", "Rua Barão de Jaguara, Centro, Campinas", "1930000001"),
    Partner("Hospital Veterinário Patas", "Hospital 24h", "Av. Norte-Sul, Campinas", "1930000002"),
    Partner("Petshop Cantinho Feliz", "Ração e banho", "Rua Culto à Ciência, Botafogo, Campinas", "1930000003"),
    Partner("Abrigo Esperança", "ONG parceira", "Estrada da Rhodia, Campinas", "1930000004"),
)

@Composable
fun HelpView(onBackClick: () -> Unit) {
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(24.dp)
    ) {
        item {
            BackButton(onBackClick)
            Text("AJUDA", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            Spacer(Modifier.height(4.dp))
            Text("Fale com a ONG", style = MaterialTheme.typography.headlineMedium, color = TerracottaDark)
            Spacer(Modifier.height(16.dp))

            Button(
                onClick = { context.openWhatsApp(OngInfo.WHATSAPP, "Olá! Vim pelo app Adotar Pet Campinas.") },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = Terracotta, contentColor = White)
            ) { Text("Chamar no WhatsApp") }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = { context.dial(OngInfo.PHONE_DIAL) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(50)
                ) { Text("Ligar") }
                OutlinedButton(
                    onClick = { context.sendEmail(OngInfo.EMAIL, "Contato pelo app Adotar Pet Campinas") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(50)
                ) { Text("E-mail") }
            }

            Spacer(Modifier.height(28.dp))
            Text(
                "PARCEIROS EM CAMPINAS",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }
        items(partners) { partner ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CreamCard),
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(partner.name, style = MaterialTheme.typography.titleMedium, color = TerracottaDark)
                    Text(partner.kind, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    Spacer(Modifier.height(4.dp))
                    Text(partner.address, style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { context.dial(partner.phone) }, shape = RoundedCornerShape(50)) {
                            Text("Ligar")
                        }
                        OutlinedButton(onClick = { context.openMap(partner.address) }, shape = RoundedCornerShape(50)) {
                            Text("Ver no mapa")
                        }
                    }
                }
            }
        }
    }
}
