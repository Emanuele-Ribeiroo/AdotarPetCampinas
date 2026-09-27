package com.adotarpet.campinas.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.adotarpet.campinas.model.Pet
import com.adotarpet.campinas.ui.theme.CreamCard
import com.adotarpet.campinas.ui.theme.Terracotta
import com.adotarpet.campinas.ui.theme.TerracottaDark
import com.adotarpet.campinas.ui.theme.TerracottaLight
import com.adotarpet.campinas.ui.theme.TextSecondary
import com.adotarpet.campinas.ui.theme.White

/** Placeholder de foto — substitua por Coil (AsyncImage) quando houver fotos reais. */
@Composable
private fun PetPhotoPlaceholder(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.background(TerracottaLight),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Pets,
            contentDescription = null,
            tint = TerracottaDark,
            modifier = Modifier.size(48.dp)
        )
    }
}

@Composable
fun PetCard(
    pet: Pet,
    onConhecerClick: () -> Unit,
    onAdotarClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CreamCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.15f)
            ) {
                PetPhotoPlaceholder(modifier = Modifier.fillMaxWidth().aspectRatio(1.15f))

                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .size(40.dp)
                        .background(White, CircleShape)
                ) {
                    Icon(
                        imageVector = if (pet.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favoritar ${pet.name}",
                        tint = Terracotta
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(pet.name, style = MaterialTheme.typography.titleLarge, color = TerracottaDark)
                    Text(
                        "${pet.speciesLabel} · ${pet.genderLabel}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }

                Spacer(Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    InfoTag(pet.age, TagStyle.PEACH)
                    InfoTag(pet.size, TagStyle.NEUTRAL)
                }
                Spacer(Modifier.height(8.dp))
                InfoTag(pet.healthLabel, TagStyle.GREEN)

                Spacer(Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = onConhecerClick,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text("Conhecer")
                    }
                    Button(
                        onClick = onAdotarClick,
                        modifier = Modifier.weight(1.3f),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(containerColor = Terracotta, contentColor = White)
                    ) {
                        Text("Quero adotar")
                    }
                }
            }
        }
    }
}
