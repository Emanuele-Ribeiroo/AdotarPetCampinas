package com.adotarpet.campinas.view

import androidx.compose.foundation.Image
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adotarpet.campinas.R
import com.adotarpet.campinas.viewmodel.HomeViewModel
import com.adotarpet.campinas.view.theme.BrandBrown
import com.adotarpet.campinas.view.theme.CreamBackground
import com.adotarpet.campinas.view.theme.CreamCard
import com.adotarpet.campinas.view.theme.SalmonBanner
import com.adotarpet.campinas.view.theme.Terracotta
import com.adotarpet.campinas.view.theme.TerracottaDark
import com.adotarpet.campinas.view.theme.TerracottaLight
import com.adotarpet.campinas.view.theme.TextPrimary
import com.adotarpet.campinas.view.theme.TextSecondary
import com.adotarpet.campinas.view.theme.White

@Composable
fun HomeView(
    onQuemAdotarClick: () -> Unit,
    onDoarClick: () -> Unit,
    onVoluntarioClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item { HeroSection(onQuemAdotarClick) }
        item { HighlightCards(onDoarClick, onQuemAdotarClick) }
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
            .background(BrandBrown)
    ) {

        Image(
            painter = painterResource(R.drawable.home_hero),
            contentDescription = "Cachorro para adoção em Campinas",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x59C1702E))
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0.30f to Color.Transparent,
                        1f to Color(0xCC5A2A10)
                    )
                )
        )

        Image(
            painter = painterResource(R.drawable.logo_adotar),
            contentDescription = "Adotar Pet Campinas",
            contentScale = ContentScale.Fit,
            alignment = Alignment.TopEnd,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 10.dp, end = 14.dp)
                .size(100.dp)
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
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
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = White),
                border = androidx.compose.foundation.BorderStroke(1.dp, White.copy(alpha = 0.8f))
            ) {
                Text("Quem adotar")
            }
        }
    }
}

@Composable
private fun HighlightCards(onDoarClick: () -> Unit, onAdotarClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)

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
            onClick = onAdotarClick,
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
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(
                        CreamBackground,
                        SalmonBanner,
                        Terracotta.copy(alpha = 0.55f)
                    )
                )
            )
    ) {
        Image(
            painter = painterResource(R.drawable.donation_pets),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            alignment = Alignment.BottomEnd,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(200.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 28.dp),
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
            Spacer(Modifier.height(150.dp))
        }
    }
}

@Composable
private fun VolunteersSection() {
    val vm: HomeViewModel = viewModel()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Voluntários", style = MaterialTheme.typography.headlineMedium, color = TerracottaDark)
        Spacer(Modifier.height(20.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(28.dp)) {
            vm.volunteers.forEach { volunteer ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(TerracottaLight),
                        contentAlignment = Alignment.Center
                    ) {
                        if (volunteer.photoRes != null) {
                            Image(
                                painter = painterResource(volunteer.photoRes),
                                contentDescription = "Foto de ${volunteer.name}",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Text(
                                volunteer.name.first().toString(),
                                style = MaterialTheme.typography.titleLarge,
                                color = TerracottaDark
                            )
                        }
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
                colors = ButtonDefaults.buttonColors(containerColor = BrandBrown, contentColor = White)
            ) {
                Text("Quero ajudar")
            }
        }
    }
}

private val DropIcon = Icons.Filled.WaterDrop
