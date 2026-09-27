package com.adotarpet.campinas.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.adotarpet.campinas.data.PetRepository
import com.adotarpet.campinas.model.PetSpecies
import com.adotarpet.campinas.ui.components.FilterChip
import com.adotarpet.campinas.ui.components.PetCard
import com.adotarpet.campinas.ui.theme.TerracottaDark
import com.adotarpet.campinas.ui.theme.TextSecondary

private enum class SpeciesFilter(val label: String) {
    TODOS("Todos"), CACHORROS("Cachorros"), GATOS("Gatos")
}

@Composable
fun AdotarScreen(
    onConhecerClick: (String) -> Unit,
    onAdotarClick: (String) -> Unit
) {
    val pets by PetRepository.pets.collectAsState()
    var filter by remember { mutableStateOf(SpeciesFilter.TODOS) }

    val filteredPets = pets.filter {
        when (filter) {
            SpeciesFilter.TODOS -> true
            SpeciesFilter.CACHORROS -> it.species == PetSpecies.CACHORRO
            SpeciesFilter.GATOS -> it.species == PetSpecies.GATO
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp)
    ) {
        item {
            Text("Adotar", style = MaterialTheme.typography.headlineLarge, color = TerracottaDark)
            Text("Pet Campinas", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)

            Column(modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)) {
                Text(
                    "Quem está esperando por você em Campinas",
                    style = MaterialTheme.typography.titleLarge,
                    color = TerracottaDark
                )
            }

            androidx.compose.foundation.layout.Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                SpeciesFilter.entries.forEach { option ->
                    FilterChip(
                        text = option.label,
                        selected = filter == option,
                        onClick = { filter = option }
                    )
                }
            }
        }

        items(filteredPets, key = { it.id }) { pet ->
            PetCard(
                pet = pet,
                onConhecerClick = { onConhecerClick(pet.id) },
                onAdotarClick = { onAdotarClick(pet.id) },
                onFavoriteClick = { PetRepository.toggleFavorite(pet.id) },
                modifier = Modifier.padding(bottom = 20.dp)
            )
        }
    }
}
