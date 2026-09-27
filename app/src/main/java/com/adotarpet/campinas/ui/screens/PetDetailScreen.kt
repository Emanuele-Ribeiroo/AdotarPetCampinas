package com.adotarpet.campinas.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.adotarpet.campinas.data.PetRepository
import com.adotarpet.campinas.ui.components.InfoTag
import com.adotarpet.campinas.ui.components.TagStyle
import com.adotarpet.campinas.ui.theme.CreamBackground
import com.adotarpet.campinas.ui.theme.Terracotta
import com.adotarpet.campinas.ui.theme.TerracottaDark
import com.adotarpet.campinas.ui.theme.TerracottaLight
import com.adotarpet.campinas.ui.theme.TextPrimary
import com.adotarpet.campinas.ui.theme.TextSecondary
import com.adotarpet.campinas.ui.theme.White

@Composable
fun PetDetailScreen(
    petId: String,
    onBackClick: () -> Unit,
    onAdotarClick: (String) -> Unit
) {
    val pets by PetRepository.pets.collectAsState()
    val pet = pets.find { it.id == petId } ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBackground)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.05f)
                    .background(TerracottaLight)
            ) {
                Icon(
                    Icons.Filled.Pets,
                    contentDescription = null,
                    tint = TerracottaDark.copy(alpha = 0.4f),
                    modifier = Modifier.align(Alignment.Center).size(140.dp)
                )
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(16.dp)
                        .size(40.dp)
                        .background(White, CircleShape)
                ) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Voltar", tint = TerracottaDark)
                }
            }

            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(pet.name, style = MaterialTheme.typography.headlineLarge, color = TerracottaDark)
                    Text(
                        "${pet.city} · ${pet.state}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }

                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    InfoTag(pet.speciesLabel, TagStyle.PEACH)
                    InfoTag(pet.genderLabel, TagStyle.PEACH)
                    InfoTag(pet.age, TagStyle.NEUTRAL)
                }
                Spacer(Modifier.height(8.dp))
                Row {
                    InfoTag(pet.size, TagStyle.NEUTRAL)
                }
                Spacer(Modifier.height(8.dp))
                InfoTag(pet.healthLabel, TagStyle.GREEN)

                Spacer(Modifier.height(20.dp))
                Text(pet.description, style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
            }
        }

        // Barra inferior fixa com favoritar + CTA de adoção
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(White)
                .padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedIconButton(
                onClick = { PetRepository.toggleFavorite(pet.id) },
                modifier = Modifier.size(52.dp)
            ) {
                Icon(
                    imageVector = if (pet.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Favoritar",
                    tint = Terracotta
                )
            }
            Button(
                onClick = { onAdotarClick(pet.id) },
                modifier = Modifier.weight(1f).height(52.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = Terracotta, contentColor = White)
            ) {
                Text("Quero adotar ${pet.name}")
            }
        }
    }
}
