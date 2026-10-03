package com.adotarpet.campinas.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.adotarpet.campinas.data.PetRepository
import com.adotarpet.campinas.ui.theme.CreamCard
import com.adotarpet.campinas.ui.theme.SalmonBanner
import com.adotarpet.campinas.ui.theme.Terracotta
import com.adotarpet.campinas.ui.theme.TerracottaDark
import com.adotarpet.campinas.ui.theme.TerracottaLight
import com.adotarpet.campinas.ui.theme.TextPrimary
import com.adotarpet.campinas.ui.theme.TextSecondary
import com.adotarpet.campinas.ui.theme.White

@Composable
fun HomeScreen(
    onQuemAdotarClick: () -> Unit,
    onDoarClick: () -> Unit,
    onVoluntarioClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item { HeroSection(onQuemAdotarClick) }
        item { HighlightCards(onDoarClick) }
        item { DonationCallout(onDoarClick) }
        item { VolunteersSection() }
        item { BecomeVolunteerBanner(onVoluntarioClick) }
    }
}

@Composable
private fun HeroSection(onQuemAdotarClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.05f)
            .background(TerracottaDark)
    ) {
        // Placeholder de imagem de fundo (cão) — troque por Image/AsyncImage real.
        Icon(
            imageVector = Icons.Filled.Pets,
            contentDescription = null,
            tint = Color(0x33FFFFFF),
            modifier = Modifier
                .align(Alignment.Center)
                .size(160.dp)
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(24.dp)
        ) {
            Text(
                "Adotar Pet\nCampinas",
                style = MaterialTheme.typography.headlineLarge,
                color = White
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Onde toda vida vale a pena",
                style = MaterialTheme.typography.bodyLarge,
                color = White.copy(alpha = 0.9f)
            )
            Spacer(Modifier.height(20.dp))
            OutlinedButton(
                onClick = onQuemAdotarClick,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = White)
            ) {
                Text("Quem adotar")
            }
        }
    }
}

@Composable
private fun HighlightCards(onDoarClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
            // IntrinsicSize.Min faz o Row medir a altura que o card mais alto
            // precisaria, e cada filho com fillMaxHeight() estica até lá.
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        HighlightCard(
            icon = Icons.Filled.Favorite,
            title = "Faça uma\ndoação",
            description = "Ração, remédio ou qualquer valor ajuda um resgate.",
            buttonText = "Saiba mais",
            onClick = onDoarClick,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        )
        HighlightCard(
            icon = Icons.Filled.Pets,
            title = "Adote",
            description = "Ganhe um companheiro para a vida toda.",
            buttonText = "Saiba mais",
            onClick = onDoarClick,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        )
    }
}

@Composable
private fun HighlightCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    buttonText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CreamCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(TerracottaLight, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = TerracottaDark)
            }
            Spacer(Modifier.height(12.dp))
            Text(title, style = MaterialTheme.typography.titleMedium, color = TerracottaDark, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text(description, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            Spacer(Modifier.weight(1f))
            Button(
                onClick = onClick,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = Terracotta, contentColor = White)
            ) {
                Text(buttonText, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun DonationCallout(onDoarClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Ajude com sua doação",
            style = MaterialTheme.typography.headlineMedium,
            color = TerracottaDark,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Uma forma de fazer a diferença na vida de um animalzinho",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = onDoarClick,
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = Terracotta, contentColor = White)
        ) {
            Text("Quero doar", modifier = Modifier.padding(horizontal = 12.dp))
        }
    }
}

@Composable
private fun VolunteersSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Voluntários", style = MaterialTheme.typography.headlineMedium, color = TerracottaDark)
        Spacer(Modifier.height(20.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(28.dp)) {
            PetRepository.volunteers.forEach { volunteer ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(TerracottaLight, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            volunteer.name.first().toString(),
                            style = MaterialTheme.typography.titleLarge,
                            color = TerracottaDark
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(volunteer.name, style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                }
            }
        }
    }
}

@Composable
private fun BecomeVolunteerBanner(onVoluntarioClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = SalmonBanner)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Favorite, contentDescription = null, tint = TerracottaDark)
                Spacer(Modifier.width(8.dp))
                Text("Seja voluntário", style = MaterialTheme.typography.titleLarge, color = TerracottaDark)
            }
            Spacer(Modifier.height(12.dp))
            Text(
                "Junte-se a nós para fazer a diferença na vida dos animais. Ajude nos resgates, " +
                        "no lar temporário ou nas feiras de adoção em Campinas.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary
            )
            Spacer(Modifier.height(20.dp))
            Button(
                onClick = onVoluntarioClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = TerracottaDark, contentColor = White)
            ) {
                Text("Quero ajudar")
            }
        }
    }
}

// Extensão local só para o ícone de gota reaproveitando o mesmo import já usado alhures.
private val DropIcon = Icons.Filled.WaterDrop